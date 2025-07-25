package br.com.assembleia.assembleia.adapters.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados dinâmicos para formulários mobile")
public record MobileDynamicDataDTO(
    @Schema(description = "Lista de sessões disponíveis")
    List<SessionOption> sessions,
    
    @Schema(description = "Lista de agendas disponíveis")
    List<AgendaOption> agendas,
    
    @Schema(description = "Configurações gerais")
    GeneralSettings settings
) {
    
    @Schema(description = "Opção de sessão para formulários")
    public record SessionOption(
        @Schema(description = "ID da sessão")
        UUID id,
        
        @Schema(description = "Label da sessão")
        String label,
        
        @Schema(description = "Data de início")
        LocalDateTime startDate,
        
        @Schema(description = "Data de fim")
        LocalDateTime endDate,
        
        @Schema(description = "Status da sessão")
        String status
    ) {}
    
    @Schema(description = "Opção de agenda para formulários")
    public record AgendaOption(
        @Schema(description = "ID da agenda")
        UUID id,
        
        @Schema(description = "Título da agenda")
        String title,
        
        @Schema(description = "Descrição da agenda")
        String description,
        
        @Schema(description = "ID da sessão")
        UUID sessionId,
        
        @Schema(description = "Status da agenda")
        String status,
        
        @Schema(description = "Se permite votação")
        boolean canVote
    ) {}
    
    @Schema(description = "Configurações gerais")
    public record GeneralSettings(
        @Schema(description = "Versão da API")
        String apiVersion,
        
        @Schema(description = "Tempo limite para votação (em minutos)")
        int votingTimeoutMinutes,
        
        @Schema(description = "Máximo de caracteres para título")
        int maxTitleLength,
        
        @Schema(description = "Máximo de caracteres para descrição")
        int maxDescriptionLength
    ) {}
}
