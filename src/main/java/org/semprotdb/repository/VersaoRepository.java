package org.semprotdb.repository;

import org.semprotdb.domain.Versao;
import org.semprotdb.service.dto.VersaoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Versao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VersaoRepository extends JpaRepository<Versao, Long>, JpaSpecificationExecutor<Versao>, VersaoDTORepository {}
