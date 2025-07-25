package br.com.assembleia.assembleia.adapters.controllers;

import br.com.assembleia.assembleia.adapters.dtos.VoteRequestDTO;
import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import br.com.assembleia.assembleia.adapters.repositories.AgendaRepository;
import br.com.assembleia.assembleia.adapters.repositories.SessionRepository;
import br.com.assembleia.assembleia.adapters.repositories.VoteRepository;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Session;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@DisplayName("VoteController Integration Tests")
class VoteControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Session activeSession;
    private Session expiredSession;
    private Agenda testAgenda;
    private Agenda expiredAgenda;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        voteRepository.deleteAll();
        agendaRepository.deleteAll();
        sessionRepository.deleteAll();
        
        // Create an active session
        activeSession = new Session(
            LocalDateTime.now().minusMinutes(10),
            LocalDateTime.now().plusHours(1)
        );
        activeSession = sessionRepository.save(activeSession);
        
        // Create an expired session
        expiredSession = new Session(
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().minusHours(1)
        );
        expiredSession = sessionRepository.save(expiredSession);
        
        // Create test agendas
        testAgenda = new Agenda("Test Agenda", "Test Description", activeSession);
        testAgenda = agendaRepository.save(testAgenda);
        
        expiredAgenda = new Agenda("Expired Agenda", "Expired Description", expiredSession);
        expiredAgenda = agendaRepository.save(expiredAgenda);
    }

    @Test
    @DisplayName("Should create vote successfully with YES vote in active session")
    void shouldCreateVoteSuccessfullyWithYesVoteInActiveSession() throws Exception {
        // Given
        VoteRequestDTO voteRequest = new VoteRequestDTO(
            testAgenda.getId(),
            "12345678901",
            VoteStatus.YES
        );

        // When & Then
        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists("X-Vote-ID"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Vote registered successfully."))
                .andExpect(jsonPath("$.data.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.cpf").value("12345678901"))
                .andExpect(jsonPath("$.data.vote").value("YES"));
    }

    @Test
    @DisplayName("Should create vote successfully with NO vote in active session")
    void shouldCreateVoteSuccessfullyWithNoVoteInActiveSession() throws Exception {
        // Given
        VoteRequestDTO voteRequest = new VoteRequestDTO(
            testAgenda.getId(),
            "98765432100",
            VoteStatus.NO
        );

        // When & Then
        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists("X-Vote-ID"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Vote registered successfully."))
                .andExpect(jsonPath("$.data.vote").value("NO"));
    }

    @Test
    @DisplayName("Should return bad request when trying to vote in expired session")
    void shouldReturnBadRequestWhenTryingToVoteInExpiredSession() throws Exception {
        // Given
        VoteRequestDTO voteRequest = new VoteRequestDTO(
            expiredAgenda.getId(),
            "12345678901",
            VoteStatus.YES
        );

        // When & Then
        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("The session has already ended. Voting is no longer allowed."));
    }

    @Test
    @DisplayName("Should return bad request when CPF is null")
    void shouldReturnBadRequestWhenCpfIsNull() throws Exception {
        // Given
        VoteRequestDTO voteRequest = new VoteRequestDTO(
            testAgenda.getId(),
            null,
            VoteStatus.YES
        );

        // When & Then
        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("CPF is required."));
    }

    @Test
    @DisplayName("Should return bad request when vote is null")
    void shouldReturnBadRequestWhenVoteIsNull() throws Exception {
        // Given
        VoteRequestDTO voteRequest = new VoteRequestDTO(
            testAgenda.getId(),
            "12345678901",
            null
        );

        // When & Then
        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Vote is required."));
    }

    @Test
    @DisplayName("Should return bad request when agenda ID is null")
    void shouldReturnBadRequestWhenAgendaIdIsNull() throws Exception {
        // Given
        VoteRequestDTO voteRequest = new VoteRequestDTO(
            null,
            "12345678901",
            VoteStatus.YES
        );

        // When & Then
        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Agenda ID is required."));
    }

}
