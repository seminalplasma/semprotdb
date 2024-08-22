package org.semprotdb.repository;

import org.semprotdb.domain.Recurso;
import org.semprotdb.service.dto.RecursoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecursoDTORepository {
    Page<RecursoDTO> findAllDTO(Pageable pageable);

    Page<Recurso> findAllLight(Pageable pageable);
}
