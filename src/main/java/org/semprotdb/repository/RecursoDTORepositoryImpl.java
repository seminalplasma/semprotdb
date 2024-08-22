package org.semprotdb.repository;

import org.semprotdb.domain.Recurso;
import org.semprotdb.service.dto.RecursoDTO;
import org.semprotdb.util.LigthDTORepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class RecursoDTORepositoryImpl extends LigthDTORepository<Recurso, RecursoDTO> implements RecursoDTORepository {

    public RecursoDTORepositoryImpl() {
        super(Recurso.class, new RecursoDTO());
    }

    @Override
    public Page<RecursoDTO> findAllDTO(Pageable pageable) {
        return project_paginateDTO(pageable);
    }

    @Override
    public Page<Recurso> findAllLight(Pageable pageable) {
        return findAllDTO(pageable).map(r -> r);
    }
}
