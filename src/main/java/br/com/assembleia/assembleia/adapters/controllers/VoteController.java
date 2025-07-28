package br.com.assembleia.assembleia.adapters.controllers;

import br.com.assembleia.assembleia.application.usecases.VoteUseCase;
import br.com.assembleia.assembleia.adapters.dtos.ResponseDTO;
import br.com.assembleia.assembleia.adapters.dtos.VoteRequestDTO;
import br.com.assembleia.assembleia.adapters.dtos.VotingResultDTO;
import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Vote;

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

import java.util.Optional;
import java.util.UUID;

@Tag(name = "Votos", description = "Gerenciamento de votos e resultados")
@RestController
@RequestMapping("/v1/votes")
public class VoteController {

    private final VoteUseCase voteUseCase;
    private final AgendaGateway agendaGateway;
    private static final Logger logger = LoggerFactory.getLogger(VoteController.class);
    private static final String INTERNAL_SERVER_ERROR_MSG = "Internal server error";

    public VoteController(VoteUseCase voteUseCase, AgendaGateway agendaGateway) {
        this.voteUseCase = voteUseCase;
        this.agendaGateway = agendaGateway;
    }

    @Operation(summary = "Registrar voto", description = "Registra um voto para uma agenda específica com validação de CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "CPF inválido ou dados incorretos", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Agenda não encontrada", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "409", description = "Participante já votou nesta agenda", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseDTO> create(
            @Parameter(description = "Dados do voto a ser registrado", required = true)
            @RequestBody VoteRequestDTO voteRequestDTO) {
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
            
            Agenda agenda = agendaOpt.get();
            
            voteUseCase.registerVote(
                agenda,
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
                .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR_MSG));
        }
    }

    @Operation(summary = "Verificar se já votou", description = "Verifica se um CPF já votou em uma agenda específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "ID de agenda inválido", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class)))
    })
    @GetMapping("/check/{agendaId}/{cpf}")
    public ResponseEntity<ResponseDTO> checkVote(
            @Parameter(description = "ID da agenda", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String agendaId, 
            @Parameter(description = "CPF do participante", required = true, example = "12345678901")
            @PathVariable String cpf) {
        try {
            logger.info("Checking if CPF {} has already voted on agenda {}", cpf, agendaId);
            
            boolean hasVoted = voteUseCase.hasVoted(UUID.fromString(agendaId), cpf);
            
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
                .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR_MSG));
        }
    }

    @Operation(summary = "Obter resultados da votação", 
               description = "Retorna os resultados da votação com porcentagens em tempo real e resultado final se a sessão terminou")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resultados obtidos com sucesso", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = VotingResultDTO.class))),
        @ApiResponse(responseCode = "400", description = "ID de agenda inválido ou agenda não encontrada", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class)))
    })
    @GetMapping("/results/{agendaId}")
    public ResponseEntity<Object> getVotingResults(
            @Parameter(description = "ID da agenda", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String agendaId) {
        try {
            logger.info("Getting voting results for agenda: {}", agendaId);
            
            VotingResultDTO results = voteUseCase.getVotingResults(UUID.fromString(agendaId));
            
            logger.info("Retrieved voting results for agenda {}: {} YES, {} NO", 
                       agendaId, results.yesCount(), results.noCount());
            
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(results);
        } catch (IllegalArgumentException e) {
            logger.error("Error getting voting results: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDTO.of(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error getting voting results: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR_MSG));
        }
    }
}
