package org.semprotdb.repository;

import org.semprotdb.domain.Proteina;
import org.semprotdb.service.dto.ProteinaDTO;
import org.semprotdb.util.LigthDTORepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class ProteinaDTORepositoryImpl extends LigthDTORepository<Proteina, ProteinaDTO> implements ProteinaDTORepository {

    public ProteinaDTORepositoryImpl() {
        super(Proteina.class, new ProteinaDTO());
    }

    @Override
    public Page<ProteinaDTO> findAllDTO(Specification<Proteina> specification, Pageable pageable) {
        return project_filter_paginateDTO(specification, pageable);
    }

    @Override
    public Page<Proteina> findAllLight(Specification<Proteina> specification, Pageable pageable) {
        return findAllDTO(specification, pageable).map(p -> p);
    }
}
