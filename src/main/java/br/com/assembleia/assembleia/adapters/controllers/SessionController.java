package br.com.assembleia.assembleia.adapters.controllers;

import br.com.assembleia.assembleia.application.usecases.SessionUseCase;
import br.com.assembleia.assembleia.adapters.dtos.ResponseDTO;
import br.com.assembleia.assembleia.adapters.dtos.SessionRequestDTO;
import br.com.assembleia.assembleia.adapters.dtos.SessionResponseDTO;
import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
import br.com.assembleia.assembleia.infra.db.entities.Session;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Sessões", description = "Gerenciamento de sessões de votação")
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

    @Operation(summary = "Criar nova sessão", description = "Cria uma nova sessão de votação com data de início e fim")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Sessão criada com sucesso", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseDTO> create(
            @Parameter(description = "Dados da sessão a ser criada", required = true)
            @RequestBody SessionRequestDTO sessionRequestDTO) {
        try {
            Session session = new Session(
                sessionRequestDTO.startDate(),
                sessionRequestDTO.endDate()
            );
            logger.info("Creating session: {}", session);
            sessionUseCase.save(session);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("X-Session-ID", session.getId().toString())
                .body(ResponseDTO.of(HttpStatus.CREATED.value(), "Session created successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating session: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.of(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating session: {}", e.getMessage());
            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error creating session"));
        }
    }

    @Operation(summary = "Buscar sessão por ID", description = "Retorna uma sessão específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessão encontrada", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = SessionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "ID inválido", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(
            @Parameter(description = "ID da sessão", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
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
