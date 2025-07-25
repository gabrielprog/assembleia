package br.com.assembleia.assembleia.external.db.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessoes", schema = "votacao")
public record Sessao(
    @Id
    @GeneratedValue
    UUID id,
    
    @Column(name = "data_inicio", nullable = false)
    LocalDateTime dataInicio,

    @Column(name = "data_fim", nullable = false)
    LocalDateTime dataFim
) {
    public Sessao() {
        this(null, null, null);
    }
}