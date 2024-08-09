package org.semprotdb.repository;

import org.semprotdb.domain.Referencia;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Referencia entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReferenciaRepository extends JpaRepository<Referencia, Long> {}
