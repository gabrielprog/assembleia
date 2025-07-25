package br.com.assembleia.assembleia.adapters.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Configuração completa para aplicação mobile")
public record MobileConfigDTO(
    @Schema(description = "Configurações das telas")
    ScreensConfig screens,
    
    @Schema(description = "URLs base da API")
    ApiEndpoints apiEndpoints,
    
    @Schema(description = "Configurações dos formulários")
    FormsConfig forms
) {
    
    @Schema(description = "Configuração das telas da aplicação")
    public record ScreensConfig(
        @Schema(description = "Configuração da tela de sessões")
        ScreenInfo sessions,
        
        @Schema(description = "Configuração da tela de agendas")
        ScreenInfo agendas,
        
        @Schema(description = "Configuração da tela de votos")
        ScreenInfo votes,
        
        @Schema(description = "Configuração da tela de resultados")
        ScreenInfo results
    ) {}
    
    @Schema(description = "Informações de configuração de uma tela")
    public record ScreenInfo(
        @Schema(description = "Título da tela", example = "Gerenciamento de Sessões")
        String title,
        
        @Schema(description = "Subtítulo ou descrição", example = "Crie e gerencie sessões de votação")
        String subtitle,
        
        @Schema(description = "URLs específicas para esta tela")
        ScreenEndpoints endpoints
    ) {}
    
    @Schema(description = "URLs específicas de uma tela")
    public record ScreenEndpoints(
        @Schema(description = "URL para criação", example = "/v1/sessions")
        String create,
        
        @Schema(description = "URL para listagem", example = "/v1/sessions")
        String list,
        
        @Schema(description = "URL para busca por ID", example = "/v1/sessions/{id}")
        String findById,
        
        @Schema(description = "URL para atualização", example = "/v1/sessions/{id}")
        String update,
        
        @Schema(description = "URL para exclusão", example = "/v1/sessions/{id}")
        String delete
    ) {}
    
    @Schema(description = "URLs base da API")
    public record ApiEndpoints(
        @Schema(description = "URL base da API", example = "/v1")
        String baseUrl,
        
        @Schema(description = "URLs para sessões")
        String sessions,
        
        @Schema(description = "URLs para agendas")
        String agendas,
        
        @Schema(description = "URLs para votos")
        String votes
    ) {}
    
    @Schema(description = "Configuração dos formulários")
    public record FormsConfig(
        @Schema(description = "Configuração do formulário de sessão")
        FormConfig sessionForm,
        
        @Schema(description = "Configuração do formulário de agenda")
        FormConfig agendaForm,
        
        @Schema(description = "Configuração do formulário de voto")
        FormConfig voteForm
    ) {}
    
    @Schema(description = "Configuração de um formulário")
    public record FormConfig(
        @Schema(description = "Nome do formulário", example = "Criar Sessão")
        String formName,
        
        @Schema(description = "Descrição do formulário")
        String description,
        
        @Schema(description = "Lista de campos do formulário")
        List<FieldConfig> fields,
        
        @Schema(description = "Configurações de validação")
        ValidationConfig validation
    ) {}
    
    @Schema(description = "Configuração de um campo do formulário")
    public record FieldConfig(
        @Schema(description = "Nome do campo", example = "startDate")
        String name,
        
        @Schema(description = "Label do campo", example = "Data de Início")
        String label,
        
        @Schema(description = "Tipo do campo", example = "datetime-local")
        String type,
        
        @Schema(description = "Se o campo é obrigatório")
        boolean required,
        
        @Schema(description = "Placeholder do campo")
        String placeholder,
        
        @Schema(description = "Validações específicas do campo")
        FieldValidation validation,
        
        @Schema(description = "Opções para campos de seleção")
        List<FieldOption> options
    ) {}
    
    @Schema(description = "Validação de um campo")
    public record FieldValidation(
        @Schema(description = "Tamanho mínimo")
        Integer minLength,
        
        @Schema(description = "Tamanho máximo")
        Integer maxLength,
        
        @Schema(description = "Padrão regex")
        String pattern,
        
        @Schema(description = "Mensagem de erro customizada")
        String errorMessage
    ) {}
    
    @Schema(description = "Opção de um campo de seleção")
    public record FieldOption(
        @Schema(description = "Valor da opção", example = "YES")
        String value,
        
        @Schema(description = "Label da opção", example = "Sim")
        String label
    ) {}
    
    @Schema(description = "Configuração de validação do formulário")
    public record ValidationConfig(
        @Schema(description = "Mensagens de erro padrão")
        ValidationMessages messages,
        
        @Schema(description = "Regras de validação específicas")
        List<ValidationRule> rules
    ) {}
    
    @Schema(description = "Mensagens de validação")
    public record ValidationMessages(
        @Schema(description = "Mensagem para campo obrigatório")
        String required,
        
        @Schema(description = "Mensagem para formato inválido")
        String invalidFormat,
        
        @Schema(description = "Mensagem para tamanho inválido")
        String invalidLength
    ) {}
    
    @Schema(description = "Regra de validação")
    public record ValidationRule(
        @Schema(description = "Nome da regra")
        String ruleName,
        
        @Schema(description = "Descrição da regra")
        String description,
        
        @Schema(description = "Campos afetados")
        List<String> fields
    ) {}
}
