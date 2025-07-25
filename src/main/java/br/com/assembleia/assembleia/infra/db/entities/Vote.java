package br.com.assembleia.assembleia.infra.db.entities;

import jakarta.persistence.*;
import br.com.assembleia.assembleia.adapters.enums.VoteStatus;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Objects;

@Entity
@Table(name = "votos", schema = "votacao")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pauta"))
    private Agenda agenda;

    @Column(nullable = false)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteStatus vote;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dateTime;

    public Vote() {}

    public Vote(Agenda agenda, String cpf, VoteStatus vote, LocalDateTime dateTime) {
        this.agenda = agenda;
        this.cpf = cpf;
        this.vote = vote;
        this.dateTime = dateTime;
    }

    public Vote(UUID id, Agenda agenda, String cpf, VoteStatus vote, LocalDateTime dateTime) {
        this.id = id;
        this.agenda = agenda;
        this.cpf = cpf;
        this.vote = vote;
        this.dateTime = dateTime;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public VoteStatus getVote() {
        return vote;
    }

    public void setVote(VoteStatus vote) {
        this.vote = vote;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public UUID id() {
        return id;
    }

    public Agenda agenda() {
        return agenda;
    }

    public String cpf() {
        return cpf;
    }

    public VoteStatus vote() {
        return vote;
    }

    public LocalDateTime dateTime() {
        return dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote1 = (Vote) o;
        return Objects.equals(id, vote1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", agenda=" + agenda +
                ", cpf='" + cpf + '\'' +
                ", vote=" + vote +
                ", dateTime=" + dateTime +
                '}';
    }
}
