package br.com.assembleia.assembleia.adapters.controllers;

import br.com.assembleia.assembleia.adapters.dtos.MobileConfigDTO;
import br.com.assembleia.assembleia.adapters.dtos.MobileDynamicDataDTO;
import br.com.assembleia.assembleia.adapters.dtos.ResponseDTO;
import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Tag(name = "Mobile Config", description = "Configurações para aplicação mobile")
@RestController
@RequestMapping("/v1/mobile-config")
public class MobileConfigController {

    private static final Logger logger = LoggerFactory.getLogger(MobileConfigController.class);
    private final SessionGateway sessionGateway;
    private final AgendaGateway agendaGateway;

    public MobileConfigController(SessionGateway sessionGateway, AgendaGateway agendaGateway) {
        this.sessionGateway = sessionGateway;
        this.agendaGateway = agendaGateway;
    }

    @Operation(summary = "Obter configurações da aplicação mobile", 
               description = "Retorna todas as configurações necessárias para montar formulários, telas e endpoints na aplicação mobile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configurações obtidas com sucesso", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = MobileConfigDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<MobileConfigDTO> getMobileConfig() {
        try {
            logger.info("Generating mobile configuration");
            
            MobileConfigDTO config = createMobileConfig();
            
            logger.info("Mobile configuration generated successfully");
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Error generating mobile configuration: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    @Operation(summary = "Obter dados dinâmicos para formulários", 
               description = "Retorna dados dinâmicos como listas de sessões e agendas para popular formulários na aplicação mobile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dados dinâmicos obtidos com sucesso", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = MobileDynamicDataDTO.class))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResponseDTO.class)))
    })
    @GetMapping("/dynamic-data")
    public ResponseEntity<MobileDynamicDataDTO> getDynamicData() {
        try {
            logger.info("Generating dynamic data for mobile forms");
            
            MobileDynamicDataDTO dynamicData = createDynamicData();
            
            logger.info("Dynamic data generated successfully");
            return ResponseEntity.ok(dynamicData);
        } catch (Exception e) {
            logger.error("Error generating dynamic data: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    private MobileConfigDTO createMobileConfig() {
        var screens = new MobileConfigDTO.ScreensConfig(
            new MobileConfigDTO.ScreenInfo(
                "Gerenciamento de Sessões",
                "Crie e gerencie sessões de votação",
                new MobileConfigDTO.ScreenEndpoints(
                    "/v1/sessions",
                    "/v1/sessions",
                    "/v1/sessions/{id}",
                    "/v1/sessions/{id}",
                    "/v1/sessions/{id}"
                )
            ),
            new MobileConfigDTO.ScreenInfo(
                "Gerenciamento de Agendas",
                "Crie e gerencie agendas para as sessões",
                new MobileConfigDTO.ScreenEndpoints(
                    "/v1/agendas",
                    "/v1/agendas",
                    "/v1/agendas/{id}",
                    "/v1/agendas/{id}",
                    "/v1/agendas/{id}"
                )
            ),
            new MobileConfigDTO.ScreenInfo(
                "Sistema de Votação",
                "Registre votos nas agendas disponíveis",
                new MobileConfigDTO.ScreenEndpoints(
                    "/v1/votes",
                    "/v1/votes/check/{agendaId}/{cpf}",
                    "/v1/votes/results/{agendaId}",
                    null,
                    null
                )
            ),
            new MobileConfigDTO.ScreenInfo(
                "Resultados de Votação",
                "Visualize os resultados das votações",
                new MobileConfigDTO.ScreenEndpoints(
                    null,
                    "/v1/votes/results/{agendaId}",
                    "/v1/votes/results/{agendaId}",
                    null,
                    null
                )
            )
        );

        var apiEndpoints = new MobileConfigDTO.ApiEndpoints(
            "/v1",
            "/v1/sessions",
            "/v1/agendas",
            "/v1/votes"
        );

        var forms = new MobileConfigDTO.FormsConfig(
            createSessionForm(),
            createAgendaForm(),
            createVoteForm()
        );

        return new MobileConfigDTO(screens, apiEndpoints, forms);
    }

    private MobileConfigDTO.FormConfig createSessionForm() {
        var fields = List.of(
            new MobileConfigDTO.FieldConfig(
                "startDate",
                "Data e Hora de Início",
                "datetime-local",
                true,
                "Selecione a data e hora de início da sessão",
                new MobileConfigDTO.FieldValidation(
                    null,
                    null,
                    null,
                    "Data de início é obrigatória e deve ser futura"
                ),
                null
            ),
            new MobileConfigDTO.FieldConfig(
                "endDate",
                "Data e Hora de Fim",
                "datetime-local",
                true,
                "Selecione a data e hora de fim da sessão",
                new MobileConfigDTO.FieldValidation(
                    null,
                    null,
                    null,
                    "Data de fim é obrigatória e deve ser posterior à data de início"
                ),
                null
            )
        );

        var validation = new MobileConfigDTO.ValidationConfig(
            new MobileConfigDTO.ValidationMessages(
                "Este campo é obrigatório",
                "Formato inválido",
                "Tamanho inválido"
            ),
            List.of(
                new MobileConfigDTO.ValidationRule(
                    "dateOrder",
                    "Data de fim deve ser posterior à data de início",
                    List.of("startDate", "endDate")
                ),
                new MobileConfigDTO.ValidationRule(
                    "futureDate",
                    "Data de início deve ser futura",
                    List.of("startDate")
                )
            )
        );

        return new MobileConfigDTO.FormConfig(
            "Criar Sessão de Votação",
            "Defina o período em que a sessão de votação estará ativa",
            fields,
            validation
        );
    }

    private MobileConfigDTO.FormConfig createAgendaForm() {
        var fields = List.of(
            new MobileConfigDTO.FieldConfig(
                "title",
                "Título da Agenda",
                "text",
                true,
                "Digite o título da agenda",
                new MobileConfigDTO.FieldValidation(
                    1,
                    255,
                    null,
                    "Título é obrigatório e deve ter entre 1 e 255 caracteres"
                ),
                null
            ),
            new MobileConfigDTO.FieldConfig(
                "description",
                "Descrição da Agenda",
                "textarea",
                false,
                "Digite uma descrição detalhada da agenda (opcional)",
                new MobileConfigDTO.FieldValidation(
                    null,
                    1000,
                    null,
                    "Descrição deve ter no máximo 1000 caracteres"
                ),
                null
            ),
            new MobileConfigDTO.FieldConfig(
                "sessionId",
                "Sessão",
                "select",
                true,
                "Selecione a sessão para esta agenda",
                new MobileConfigDTO.FieldValidation(
                    null,
                    null,
                    null,
                    "Sessão é obrigatória"
                ),
                null
            )
        );

        var validation = new MobileConfigDTO.ValidationConfig(
            new MobileConfigDTO.ValidationMessages(
                "Este campo é obrigatório",
                "Formato inválido",
                "Tamanho inválido"
            ),
            List.of(
                new MobileConfigDTO.ValidationRule(
                    "sessionExists",
                    "A sessão selecionada deve existir e estar ativa",
                    List.of("sessionId")
                )
            )
        );

        return new MobileConfigDTO.FormConfig(
            "Criar Agenda",
            "Crie uma nova agenda para votação em uma sessão específica",
            fields,
            validation
        );
    }

    private MobileConfigDTO.FormConfig createVoteForm() {
        var voteOptions = List.of(
            new MobileConfigDTO.FieldOption("YES", "Sim"),
            new MobileConfigDTO.FieldOption("NO", "Não")
        );

        var fields = List.of(
            new MobileConfigDTO.FieldConfig(
                "agendaId",
                "Agenda",
                "select",
                true,
                "Selecione a agenda para votar",
                new MobileConfigDTO.FieldValidation(
                    null,
                    null,
                    null,
                    "Agenda é obrigatória"
                ),
                null
            ),
            new MobileConfigDTO.FieldConfig(
                "cpf",
                "CPF",
                "text",
                true,
                "Digite seu CPF (somente números)",
                new MobileConfigDTO.FieldValidation(
                    11,
                    11,
                    "^[0-9]{11}$",
                    "CPF deve conter exatamente 11 dígitos numéricos"
                ),
                null
            ),
            new MobileConfigDTO.FieldConfig(
                "vote",
                "Seu Voto",
                "radio",
                true,
                "Selecione sua opção de voto",
                new MobileConfigDTO.FieldValidation(
                    null,
                    null,
                    null,
                    "Voto é obrigatório"
                ),
                voteOptions
            )
        );

        var validation = new MobileConfigDTO.ValidationConfig(
            new MobileConfigDTO.ValidationMessages(
                "Este campo é obrigatório",
                "Formato inválido",
                "Tamanho inválido"
            ),
            List.of(
                new MobileConfigDTO.ValidationRule(
                    "cpfValidation",
                    "CPF deve ser válido e não pode ter votado anteriormente nesta agenda",
                    List.of("cpf", "agendaId")
                ),
                new MobileConfigDTO.ValidationRule(
                    "sessionActive",
                    "A sessão da agenda deve estar ativa",
                    List.of("agendaId")
                )
            )
        );

        return new MobileConfigDTO.FormConfig(
            "Registrar Voto",
            "Registre seu voto na agenda selecionada",
            fields,
            validation
        );
    }

    private MobileDynamicDataDTO createDynamicData() {
        try {
            List<MobileDynamicDataDTO.SessionOption> sessions = new ArrayList<>();
            List<MobileDynamicDataDTO.AgendaOption> agendas = new ArrayList<>();
            
            List<br.com.assembleia.assembleia.infra.db.entities.Session> allSessions = sessionGateway.findAll();
            LocalDateTime now = LocalDateTime.now();
            
            for (var session : allSessions) {
                String status;
                if (now.isBefore(session.getStartDate())) {
                    status = "PROGRAMADA";
                } else if (now.isAfter(session.getEndDate())) {
                    status = "ENCERRADA";
                } else {
                    status = "ATIVA";
                }
                
                sessions.add(new MobileDynamicDataDTO.SessionOption(
                    session.getId(),
                    "Sessão - " + session.getStartDate().toLocalDate(),
                    session.getStartDate(),
                    session.getEndDate(),
                    status
                ));
            }
            
            List<br.com.assembleia.assembleia.infra.db.entities.Agenda> allAgendas = agendaGateway.findAll();
            
            for (var agenda : allAgendas) {
                String status;
                boolean canVote = false;
                
                if (now.isBefore(agenda.getSession().getStartDate())) {
                    status = "PROGRAMADA";
                } else if (now.isAfter(agenda.getSession().getEndDate())) {
                    status = "ENCERRADA";
                } else {
                    status = "ATIVA";
                    canVote = true;
                }
                
                agendas.add(new MobileDynamicDataDTO.AgendaOption(
                    agenda.getId(),
                    agenda.getTitle(),
                    agenda.getDescription(),
                    agenda.getSession().getId(),
                    status,
                    canVote
                ));
            }
            
            var settings = new MobileDynamicDataDTO.GeneralSettings(
                "1.0",
                60,
                255,
                1000
            );
            
            return new MobileDynamicDataDTO(sessions, agendas, settings);
            
        } catch (Exception e) {
            logger.warn("Error fetching real data, falling back to mock data: {}", e.getMessage());
            return createMockDynamicData();
        }
    }

    private MobileDynamicDataDTO createMockDynamicData() {
        var sessions = new ArrayList<MobileDynamicDataDTO.SessionOption>();
        var agendas = new ArrayList<MobileDynamicDataDTO.AgendaOption>();
        
        var now = LocalDateTime.now();
        
        sessions.add(new MobileDynamicDataDTO.SessionOption(
            java.util.UUID.randomUUID(),
            "Sessão de Votação - " + now.getMonth() + "/" + now.getYear(),
            now.plusDays(1),
            now.plusDays(1).plusHours(2),
            "PROGRAMADA"
        ));
        
        sessions.add(new MobileDynamicDataDTO.SessionOption(
            java.util.UUID.randomUUID(),
            "Sessão Extraordinária - " + now.getMonth(),
            now.minusHours(1),
            now.plusHours(1),
            "ATIVA"
        ));
        
        agendas.add(new MobileDynamicDataDTO.AgendaOption(
            java.util.UUID.randomUUID(),
            "Aprovação do Orçamento 2025",
            "Votação para aprovação do orçamento anual",
            sessions.get(1).id(),
            "ATIVA",
            true
        ));
        
        agendas.add(new MobileDynamicDataDTO.AgendaOption(
            java.util.UUID.randomUUID(),
            "Mudança de Estatuto",
            "Alterações propostas no estatuto da assembleia",
            sessions.get(0).id(),
            "PROGRAMADA",
            false
        ));
        
        var settings = new MobileDynamicDataDTO.GeneralSettings(
            "1.0",
            60,
            255,
            1000
        );
        
        return new MobileDynamicDataDTO(sessions, agendas, settings);
    }
}
