package br.com.assembleia.assembleia.external.db.entities;

import jakarta.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ForeignKey;

import br.com.assembleia.assembleia.adapters.enums.VotoStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "votos", schema = "votacao")
public record Votos(
    @Id
    @GeneratedValue
    UUID id,

    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pauta"))
    Pauta pauta,

    @Column(name = "participante_id", nullable = false)
    String participanteId,

    @Column(nullable = false)
    String cpf,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    VotoStatus voto,

    @Column(name = "data_hora", nullable = false)
    LocalDateTime dataHora
) {
    public Votos() {
        this(null, null, null, null, null, null);
    }
}
