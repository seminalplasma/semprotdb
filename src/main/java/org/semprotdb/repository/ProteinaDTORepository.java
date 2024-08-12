package org.semprotdb.repository;

import org.semprotdb.domain.Proteina;
import org.semprotdb.service.dto.ProteinaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProteinaDTORepository {
    Page<ProteinaDTO> findAllDTO(Specification<Proteina> specification, Pageable pageable);

    Page<Proteina> findAllLight(Specification<Proteina> specification, Pageable pageable);
}
