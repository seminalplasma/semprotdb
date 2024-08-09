package org.semprotdb.repository;

import org.semprotdb.domain.Versao;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Versao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VersaoRepository extends JpaRepository<Versao, Long>, JpaSpecificationExecutor<Versao> {}
