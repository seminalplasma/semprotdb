package org.semprotdb.repository;

import org.semprotdb.domain.Gene;
import org.semprotdb.service.dto.GeneDTO;
import org.semprotdb.util.LigthDTORepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class GeneDTORepositoryImpl extends LigthDTORepository<Gene, GeneDTO> implements GeneDTORepository {

    public GeneDTORepositoryImpl() {
        super(Gene.class, new GeneDTO());
    }

    @Override
    public Page<GeneDTO> findAllDTO(Specification<Gene> specification, Pageable pageable) {
        return project_filter_paginateDTO(specification, pageable);
    }

    @Override
    public Page<Gene> findAllLight(Specification<Gene> specification, Pageable pageable) {
        return findAllDTO(specification, pageable).map(p -> p);
    }
}
