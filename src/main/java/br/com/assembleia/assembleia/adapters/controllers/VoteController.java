package br.com.assembleia.assembleia.adapters.controllers;

import br.com.assembleia.assembleia.application.usecases.VoteUseCase;
import br.com.assembleia.assembleia.adapters.dtos.ResponseDTO;
import br.com.assembleia.assembleia.adapters.dtos.VoteRequestDTO;
import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/votes")
public class VoteController {

    private final VoteUseCase voteUseCase;
    private final AgendaGateway agendaGateway;
    private static final Logger logger = LoggerFactory.getLogger(VoteController.class);

    public VoteController(VoteUseCase voteUseCase, AgendaGateway agendaGateway) {
        this.voteUseCase = voteUseCase;
        this.agendaGateway = agendaGateway;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> create(@RequestBody VoteRequestDTO voteRequestDTO) {
        try {
            logger.info("Registering vote: agendaId={}, cpf={}, vote={}", 
                       voteRequestDTO.agendaId(), voteRequestDTO.cpf(), voteRequestDTO.vote());
            
            Optional<Agenda> agendaOpt = agendaGateway.findById(voteRequestDTO.agendaId());
            if (agendaOpt.isEmpty()) {
                logger.error("Agenda not found: {}", voteRequestDTO.agendaId());
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ResponseDTO.of(HttpStatus.NOT_FOUND.value(), "Agenda not found"));
            }
            
            voteUseCase.registerVote(
                voteRequestDTO.agendaId(),
                agendaOpt.get(),
                voteRequestDTO.cpf(),
                voteRequestDTO.vote()
            );
            
            logger.info("Vote registered successfully for CPF: {}", voteRequestDTO.cpf());
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.of(HttpStatus.CREATED.value(), "Vote registered successfully"));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error registering vote: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.of(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (IllegalStateException e) {
            logger.error("State error registering vote: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseDTO.of(HttpStatus.CONFLICT.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error registering vote: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
        }
    }

    @GetMapping("/check/{agendaId}/{cpf}")
    public ResponseEntity<ResponseDTO> checkVote(@PathVariable String agendaId, @PathVariable String cpf) {
        try {
            logger.info("Checking if CPF {} has already voted on agenda {}", cpf, agendaId);
            
            boolean hasVoted = voteUseCase.hasVoted(java.util.UUID.fromString(agendaId), cpf);
            
            String message = hasVoted ? "Participant has already voted on this agenda" : "Participant has not voted on this agenda yet";
            
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.of(HttpStatus.OK.value(), message));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID: {}", agendaId);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.of(HttpStatus.BAD_REQUEST.value(), "Invalid agenda ID"));
        } catch (Exception e) {
            logger.error("Unexpected error checking vote: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
        }
    }
}
