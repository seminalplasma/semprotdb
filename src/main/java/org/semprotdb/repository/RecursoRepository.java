package org.semprotdb.repository;

import java.util.List;
import java.util.Optional;
import org.semprotdb.domain.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Recurso entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long>, RecursoDTORepository {
    List<Recurso> findAllByProteinasIsEmpty();

    Optional<Recurso> findByUid(String uid);
}
