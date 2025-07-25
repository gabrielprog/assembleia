package br.com.assembleia.assembleia.adapters.controllers;

import br.com.assembleia.assembleia.adapters.dtos.SessionRequestDTO;
import br.com.assembleia.assembleia.adapters.repositories.SessionRepository;
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
@DisplayName("SessionController Integration Tests")
class SessionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        sessionRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create session successfully with valid data")
    void shouldCreateSessionSuccessfullyWithValidData() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().plusMinutes(1);
        LocalDateTime endDate = LocalDateTime.now().plusHours(2);
        
        SessionRequestDTO sessionRequest = new SessionRequestDTO(startDate, endDate);

        // When & Then
        mockMvc.perform(post("/api/v1/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists("X-Session-ID"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Session created successfully."))
                .andExpect(jsonPath("$.data.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.startDate").value(notNullValue()))
                .andExpect(jsonPath("$.data.endDate").value(notNullValue()));
    }

    @Test
    @DisplayName("Should return bad request when start date is null")
    void shouldReturnBadRequestWhenStartDateIsNull() throws Exception {
        // Given
        SessionRequestDTO sessionRequest = new SessionRequestDTO(
            null,
            LocalDateTime.now().plusHours(2)
        );

        // When & Then
        mockMvc.perform(post("/api/v1/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Start date is required."));
    }

    @Test
    @DisplayName("Should return bad request when end date is null")
    void shouldReturnBadRequestWhenEndDateIsNull() throws Exception {
        // Given
        SessionRequestDTO sessionRequest = new SessionRequestDTO(
            LocalDateTime.now().plusMinutes(1),
            null
        );

        // When & Then
        mockMvc.perform(post("/api/v1/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("End date is required."));
    }

    @Test
    @DisplayName("Should return bad request when start date is in the past")
    void shouldReturnBadRequestWhenStartDateIsInThePast() throws Exception {
        // Given
        SessionRequestDTO sessionRequest = new SessionRequestDTO(
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(1)
        );

        // When & Then
        mockMvc.perform(post("/api/v1/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Start date must be in the future."));
    }

    @Test
    @DisplayName("Should return bad request when end date is before start date")
    void shouldReturnBadRequestWhenEndDateIsBeforeStartDate() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.now().plusHours(2);
        LocalDateTime endDate = LocalDateTime.now().plusHours(1);
        
        SessionRequestDTO sessionRequest = new SessionRequestDTO(startDate, endDate);

        // When & Then
        mockMvc.perform(post("/api/v1/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("End date must be after start date."));
    }

    @Test
    @DisplayName("Should return bad request with invalid JSON format")
    void shouldReturnBadRequestWithInvalidJsonFormat() throws Exception {
        // Given
        String invalidJson = "{ \"startDate\": \"invalid-date\", \"endDate\": \"invalid-date\" }";

        // When & Then
        mockMvc.perform(post("/api/v1/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
