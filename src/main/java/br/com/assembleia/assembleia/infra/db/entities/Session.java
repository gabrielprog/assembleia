package br.com.assembleia.assembleia.infra.db.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Objects;

@Entity
@Table(name = "sessoes", schema = "votacao")
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime endDate;

    @Version
    @Column(name = "version")
    private Long version;

    public Session() {}
    
    public Session(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public Session(UUID id, LocalDateTime startDate, LocalDateTime endDate, Long version) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public UUID id() {
        return id;
    }

    public LocalDateTime startDate() {
        return startDate;
    }

    public LocalDateTime endDate() {
        return endDate;
    }

    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", version=" + version +
                '}';
    }
}
