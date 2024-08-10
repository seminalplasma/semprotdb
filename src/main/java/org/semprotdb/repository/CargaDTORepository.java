package org.semprotdb.repository;

import org.semprotdb.domain.Carga;
import org.semprotdb.service.dto.CargaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CargaDTORepository {
    Page<CargaDTO> findAllDTO(Specification<Carga> specification, Pageable pageable);

    Page<Carga> findAllLight(Specification<Carga> specification, Pageable pageable);
}
