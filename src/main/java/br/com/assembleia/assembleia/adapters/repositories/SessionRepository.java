package br.com.assembleia.assembleia.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.assembleia.assembleia.infra.db.entities.Session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    
    @Query("SELECT s FROM Session s WHERE s.startDate <= :currentTime AND s.endDate >= :currentTime")
    List<Session> findActiveSessions(@Param("currentTime") LocalDateTime currentTime);
}
