package br.com.assembleia.assembleia.adapters.dtos;

import java.util.UUID;

import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado da votação com porcentagens e status")
public record VotingResultDTO(
    @Schema(description = "ID da agenda", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID agendaId,
    
    @Schema(description = "Título da agenda", example = "Aprovação do novo orçamento")
    String agendaTitle,
    
    @Schema(description = "Número de votos SIM", example = "15")
    long yesCount,
    
    @Schema(description = "Número de votos NÃO", example = "8")
    long noCount,
    
    @Schema(description = "Total de votos registrados", example = "23")
    long totalVotes,
    
    @Schema(description = "Porcentagem de votos SIM", example = "65.22")
    double yesPercentage,
    
    @Schema(description = "Porcentagem de votos NÃO", example = "34.78")
    double noPercentage,
    
    @Schema(description = "Se a sessão de votação já terminou", example = "true")
    boolean sessionEnded,
    
    @Schema(description = "Vencedor da votação (apenas quando sessão terminou)", allowableValues = {"YES", "NO"}, nullable = true)
    VoteStatus winner,
    
    @Schema(description = "Resultado textual da votação", example = "Aprovado", allowableValues = {"Votação em andamento", "Aprovado", "Rejeitado", "Empate", "Nenhum voto registrado"})
    String result
) {
    
    public static VotingResultDTO create(
        UUID agendaId,
        String agendaTitle,
        long yesCount,
        long noCount,
        boolean sessionEnded
    ) {
        long totalVotes = yesCount + noCount;
        double yesPercentage = totalVotes > 0 ? (yesCount * 100.0) / totalVotes : 0.0;
        double noPercentage = totalVotes > 0 ? (noCount * 100.0) / totalVotes : 0.0;
        
        VoteStatus winner = null;
        String result = "Votação em andamento";
        
        if (sessionEnded && totalVotes > 0) {
            if (yesCount > noCount) {
                winner = VoteStatus.YES;
                result = "Aprovado";
            } else if (noCount > yesCount) {
                winner = VoteStatus.NO;
                result = "Rejeitado";
            } else {
                result = "Empate";
            }
        } else if (sessionEnded) {
            result = "Nenhum voto registrado";
        }
        
        return new VotingResultDTO(
            agendaId,
            agendaTitle,
            yesCount,
            noCount,
            totalVotes,
            Math.round(yesPercentage * 100.0) / 100.0,
            Math.round(noPercentage * 100.0) / 100.0,
            sessionEnded,
            winner,
            result
        );
    }
}
