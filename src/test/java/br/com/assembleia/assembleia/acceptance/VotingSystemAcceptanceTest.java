package br.com.assembleia.assembleia.acceptance;

import br.com.assembleia.assembleia.adapters.dtos.AgendaRequestDTO;
import br.com.assembleia.assembleia.adapters.dtos.SessionRequestDTO;
import br.com.assembleia.assembleia.adapters.dtos.VoteRequestDTO;
import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import br.com.assembleia.assembleia.adapters.repositories.AgendaRepository;
import br.com.assembleia.assembleia.adapters.repositories.SessionRepository;
import br.com.assembleia.assembleia.adapters.repositories.VoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@DisplayName("Voting System Acceptance Tests")
class VotingSystemAcceptanceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        voteRepository.deleteAll();
        agendaRepository.deleteAll();
        sessionRepository.deleteAll();
    }

    @Test
    @DisplayName("Complete voting workflow: Create session, create agenda, and vote successfully")
    void completeVotingWorkflowShouldWork() throws Exception {
        // Step 1: Create a session
        SessionRequestDTO sessionRequest = new SessionRequestDTO(
            LocalDateTime.now().plusMinutes(1),
            LocalDateTime.now().plusHours(2)
        );

        MvcResult sessionResult = mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Session-ID"))
                .andReturn();

        String sessionId = sessionResult.getResponse().getHeader("X-Session-ID");
        assertNotNull(sessionId);

        // Step 2: Create an agenda for the session
        AgendaRequestDTO agendaRequest = new AgendaRequestDTO(
            "Should we approve the new budget?",
            "Discussion about the budget allocation for next year",
            UUID.fromString(sessionId)
        );

        MvcResult agendaResult = mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Agenda-ID"))
                .andExpect(jsonPath("$.data.title").value("Should we approve the new budget?"))
                .andReturn();

        String agendaId = agendaResult.getResponse().getHeader("X-Agenda-ID");
        assertNotNull(agendaId);

        // Step 3: Cast a YES vote
        VoteRequestDTO yesVoteRequest = new VoteRequestDTO(
            UUID.fromString(agendaId),
            "12345678901",
            VoteStatus.YES
        );

        mockMvc.perform(post("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(yesVoteRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Vote-ID"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Vote registered successfully."))
                .andExpect(jsonPath("$.data.cpf").value("12345678901"))
                .andExpect(jsonPath("$.data.vote").value("YES"));

        // Step 4: Cast a NO vote with different CPF
        VoteRequestDTO noVoteRequest = new VoteRequestDTO(
            UUID.fromString(agendaId),
            "98765432100",
            VoteStatus.NO
        );

        mockMvc.perform(post("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noVoteRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("X-Vote-ID"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.vote").value("NO"));
    }

    @Test
    @DisplayName("Should prevent duplicate voting from the same CPF")
    void shouldPreventDuplicateVotingFromSameCpf() throws Exception {
        // Setup: Create session and agenda
        SessionRequestDTO sessionRequest = new SessionRequestDTO(
            LocalDateTime.now().plusMinutes(1),
            LocalDateTime.now().plusHours(2)
        );

        MvcResult sessionResult = mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String sessionId = sessionResult.getResponse().getHeader("X-Session-ID");

        AgendaRequestDTO agendaRequest = new AgendaRequestDTO(
            "Test Agenda",
            "Test Description",
            UUID.fromString(sessionId)
        );

        MvcResult agendaResult = mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String agendaId = agendaResult.getResponse().getHeader("X-Agenda-ID");

        // First vote should succeed
        VoteRequestDTO firstVote = new VoteRequestDTO(
            UUID.fromString(agendaId),
            "12345678901",
            VoteStatus.YES
        );

        mockMvc.perform(post("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstVote)))
                .andExpect(status().isCreated());

        // Second vote with same CPF should fail
        VoteRequestDTO duplicateVote = new VoteRequestDTO(
            UUID.fromString(agendaId),
            "12345678901",
            VoteStatus.NO
        );

        mockMvc.perform(post("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateVote)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("This CPF has already voted on this agenda."));
    }

    @Test
    @DisplayName("Should prevent voting when session has ended")
    void shouldPreventVotingWhenSessionHasEnded() throws Exception {
        // Create an expired session
        SessionRequestDTO expiredSessionRequest = new SessionRequestDTO(
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().minusHours(1)
        );

        MvcResult sessionResult = mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expiredSessionRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String sessionId = sessionResult.getResponse().getHeader("X-Session-ID");

        // Create agenda for expired session
        AgendaRequestDTO agendaRequest = new AgendaRequestDTO(
            "Expired Agenda",
            "This agenda is in an expired session",
            UUID.fromString(sessionId)
        );

        MvcResult agendaResult = mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String agendaId = agendaResult.getResponse().getHeader("X-Agenda-ID");

        // Attempt to vote should fail
        VoteRequestDTO voteRequest = new VoteRequestDTO(
            UUID.fromString(agendaId),
            "12345678901",
            VoteStatus.YES
        );

        mockMvc.perform(post("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("The session has already ended. Voting is no longer allowed."));
    }

    @Test
    @DisplayName("Should handle multiple agendas in the same session")
    void shouldHandleMultipleAgendasInSameSession() throws Exception {
        // Create session
        SessionRequestDTO sessionRequest = new SessionRequestDTO(
            LocalDateTime.now().plusMinutes(1),
            LocalDateTime.now().plusHours(2)
        );

        MvcResult sessionResult = mockMvc.perform(post("/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String sessionId = sessionResult.getResponse().getHeader("X-Session-ID");

        // Create first agenda
        AgendaRequestDTO agenda1Request = new AgendaRequestDTO(
            "First Agenda",
            "First agenda description",
            UUID.fromString(sessionId)
        );

        MvcResult agenda1Result = mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agenda1Request)))
                .andExpect(status().isCreated())
                .andReturn();

        String agenda1Id = agenda1Result.getResponse().getHeader("X-Agenda-ID");

        // Create second agenda
        AgendaRequestDTO agenda2Request = new AgendaRequestDTO(
            "Second Agenda",
            "Second agenda description",
            UUID.fromString(sessionId)
        );

        MvcResult agenda2Result = mockMvc.perform(post("/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agenda2Request)))
                .andExpect(status().isCreated())
                .andReturn();

        String agenda2Id = agenda2Result.getResponse().getHeader("X-Agenda-ID");

        // Vote on first agenda
        VoteRequestDTO vote1Request = new VoteRequestDTO(
            UUID.fromString(agenda1Id),
            "12345678901",
            VoteStatus.YES
        );

        mockMvc.perform(post("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vote1Request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.vote").value("YES"));

        // Vote on second agenda with same CPF (should be allowed)
        VoteRequestDTO vote2Request = new VoteRequestDTO(
            UUID.fromString(agenda2Id),
            "12345678901",
            VoteStatus.NO
        );

        mockMvc.perform(post("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vote2Request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.vote").value("NO"));
    }
}
