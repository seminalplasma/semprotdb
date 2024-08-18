package org.semprotdb.repository;

import org.semprotdb.domain.Gene;
import org.semprotdb.service.dto.GeneDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface GeneDTORepository {
    Page<GeneDTO> findAllDTO(Specification<Gene> specification, Pageable pageable);

    Page<Gene> findAllLight(Specification<Gene> specification, Pageable pageable);
}
