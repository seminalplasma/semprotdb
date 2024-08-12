package org.semprotdb.repository;

import java.util.List;
import org.semprotdb.domain.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Recurso entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    List<Recurso> findAllByProteinasIsEmpty();
}
