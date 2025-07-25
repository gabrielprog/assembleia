package br.com.assembleia.assembleia.external.db.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ForeignKey;

@Entity
@Table(name = "pautas", schema = "votacao")
public record Pauta(
    @Id
    @GeneratedValue
    UUID id,

    @Column(nullable = false)
    String titulo,

    String descricao,

    @ManyToOne
    @JoinColumn(name = "sessao_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_sessao"))
    Sessao sessao
) {
    public Pauta() {
        this(null, null, null, null);
    }
}