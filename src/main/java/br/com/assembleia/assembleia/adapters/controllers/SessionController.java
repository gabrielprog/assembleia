package br.com.assembleia.assembleia.adapters.controllers;

import br.com.assembleia.assembleia.application.usecases.SessionUseCase;
import br.com.assembleia.assembleia.adapters.dtos.ResponseDTO;
import br.com.assembleia.assembleia.adapters.dtos.SessionRequestDTO;
import br.com.assembleia.assembleia.adapters.dtos.SessionResponseDTO;
import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
import br.com.assembleia.assembleia.infra.db.entities.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/sessions")
public class SessionController {

    private final SessionUseCase sessionUseCase;
    private final SessionGateway sessionGateway;
    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    public SessionController(SessionUseCase sessionUseCase, SessionGateway sessionGateway) {
        this.sessionUseCase = sessionUseCase;
        this.sessionGateway = sessionGateway;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> create(@RequestBody SessionRequestDTO sessionRequestDTO) {
        try {
            Session session = new Session(
                sessionRequestDTO.startDate(),
                sessionRequestDTO.endDate()
            );
            logger.info("Creating session: {}", session);
            sessionUseCase.save(session);
            return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseDTO.of(HttpStatus.CREATED.value(), "Session created successfully"));
        } catch (Exception e) {
            logger.error("Error creating session: {}", e.getMessage());
            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating session"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        try {
            UUID sessionId = UUID.fromString(id);
            Optional<Session> sessionOpt = sessionGateway.findById(sessionId);
            
            if (sessionOpt.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.of(HttpStatus.NOT_FOUND.value(), "Session not found"));
            }
            
            Session session = sessionOpt.get();
            SessionResponseDTO response = new SessionResponseDTO(
                session.getId(),
                session.getStartDate(),
                session.getEndDate(),
                session.getVersion()
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID: {}", id);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.of(HttpStatus.BAD_REQUEST.value(), "Invalid session ID"));
        } catch (Exception e) {
            logger.error("Unexpected error searching session: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
        }
    }
}
