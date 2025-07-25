package br.com.assembleia.assembleia.adapters.controllers;

import br.com.assembleia.assembleia.application.usecases.AgendaUseCase;
import br.com.assembleia.assembleia.adapters.dtos.ResponseDTO;
import br.com.assembleia.assembleia.adapters.dtos.AgendaRequestDTO;
import br.com.assembleia.assembleia.adapters.dtos.AgendaResponseDTO;
import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;

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

@Tag(name = "Agendas", description = "Gerenciamento de agendas de votação")
@RestController
@RequestMapping("/v1/agendas")
public class AgendaController {

    private final AgendaUseCase agendaUseCase;
    private final AgendaGateway agendaGateway;
    private static final Logger logger = LoggerFactory.getLogger(AgendaController.class);

    public AgendaController(AgendaUseCase agendaUseCase, AgendaGateway agendaGateway) {
        this.agendaUseCase = agendaUseCase;
        this.agendaGateway = agendaGateway;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> create(@RequestBody AgendaRequestDTO agendaRequestDTO) {
        try {
            logger.info("Creating agenda: title={}, sessionId={}", 
                       agendaRequestDTO.title(), agendaRequestDTO.sessionId());
            
            Agenda agenda = agendaUseCase.createAgenda(
                agendaRequestDTO.title(),
                agendaRequestDTO.description(),
                agendaRequestDTO.sessionId()
            );
            
            logger.info("Agenda created successfully: {}", agenda);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("X-Agenda-ID", agenda.getId().toString())
                .body(ResponseDTO.of(HttpStatus.CREATED.value(), "Agenda created successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating agenda: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.of(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error creating agenda: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable String id) {
        try {
            UUID agendaId = UUID.fromString(id);
            Optional<Agenda> agendaOpt = agendaGateway.findById(agendaId);
            
            if (agendaOpt.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.of(HttpStatus.NOT_FOUND.value(), "Agenda not found"));
            }
            
            Agenda agenda = agendaOpt.get();
            AgendaResponseDTO response = new AgendaResponseDTO(
                agenda.getId(),
                agenda.getTitle(),
                agenda.getDescription(),
                agenda.getSession().getId()
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID: {}", id);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.of(HttpStatus.BAD_REQUEST.value(), "Invalid agenda ID"));
        } catch (Exception e) {
            logger.error("Unexpected error searching agenda: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
        }
    }
}
