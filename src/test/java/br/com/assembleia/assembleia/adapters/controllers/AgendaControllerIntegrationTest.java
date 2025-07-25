package br.com.assembleia.assembleia.adapters.controllers;

import br.com.assembleia.assembleia.adapters.dtos.AgendaRequestDTO;
import br.com.assembleia.assembleia.adapters.repositories.AgendaRepository;
import br.com.assembleia.assembleia.adapters.repositories.SessionRepository;
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
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@DisplayName("AgendaController Integration Tests")
class AgendaControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Session testSession;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        agendaRepository.deleteAll();
        sessionRepository.deleteAll();
        
        // Create a test session
        testSession = new Session(
            LocalDateTime.now().plusMinutes(1),
            LocalDateTime.now().plusHours(2)
        );
        testSession = sessionRepository.save(testSession);
    }

    @Test
    @DisplayName("Should create agenda successfully with valid data")
    void shouldCreateAgendaSuccessfullyWithValidData() throws Exception {
        // Given
        AgendaRequestDTO agendaRequest = new AgendaRequestDTO(
            "Test Agenda Title",
            "Test Agenda Description",
            testSession.getId()
        );

        // When & Then
        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists("X-Agenda-ID"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Agenda created successfully."))
                .andExpect(jsonPath("$.data.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.title").value("Test Agenda Title"))
                .andExpect(jsonPath("$.data.description").value("Test Agenda Description"));
    }

    @Test
    @DisplayName("Should return bad request when title is null")
    void shouldReturnBadRequestWhenTitleIsNull() throws Exception {
        // Given
        AgendaRequestDTO agendaRequest = new AgendaRequestDTO(
            null,
            "Test Description",
            testSession.getId()
        );

        // When & Then
        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Agenda title is required."));
    }

    @Test
    @DisplayName("Should return bad request when title is empty")
    void shouldReturnBadRequestWhenTitleIsEmpty() throws Exception {
        // Given
        AgendaRequestDTO agendaRequest = new AgendaRequestDTO(
            "",
            "Test Description",
            testSession.getId()
        );

        // When & Then
        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Agenda title is required."));
    }

    @Test
    @DisplayName("Should return bad request when session ID is null")
    void shouldReturnBadRequestWhenSessionIdIsNull() throws Exception {
        // Given
        AgendaRequestDTO agendaRequest = new AgendaRequestDTO(
            "Test Title",
            "Test Description",
            null
        );

        // When & Then
        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Session ID is required."));
    }

    @Test
    @DisplayName("Should return bad request when session does not exist")
    void shouldReturnBadRequestWhenSessionDoesNotExist() throws Exception {
        // Given
        UUID nonExistentSessionId = UUID.randomUUID();
        AgendaRequestDTO agendaRequest = new AgendaRequestDTO(
            "Test Title",
            "Test Description",
            nonExistentSessionId
        );

        // When & Then
        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Session not found with the provided ID."));
    }

    @Test
    @DisplayName("Should accept agenda with null description")
    void shouldAcceptAgendaWithNullDescription() throws Exception {
        // Given
        AgendaRequestDTO agendaRequest = new AgendaRequestDTO(
            "Test Title",
            null,
            testSession.getId()
        );

        // When & Then
        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists("X-Agenda-ID"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Agenda created successfully."))
                .andExpect(jsonPath("$.data.title").value("Test Title"));
    }
}
