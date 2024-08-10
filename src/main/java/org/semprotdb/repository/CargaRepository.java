package org.semprotdb.repository;

import org.semprotdb.domain.Carga;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Carga entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CargaRepository extends JpaRepository<Carga, Long>, JpaSpecificationExecutor<Carga>, CargaDTORepository {}
