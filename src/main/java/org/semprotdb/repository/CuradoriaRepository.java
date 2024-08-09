package org.semprotdb.repository;

import org.semprotdb.domain.Curadoria;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Curadoria entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CuradoriaRepository extends JpaRepository<Curadoria, Long> {}
