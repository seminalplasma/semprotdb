package org.semprotdb.repository;

import org.semprotdb.domain.Organismo;
import org.semprotdb.service.dto.OrganismoDTO;
import org.semprotdb.util.LigthDTORepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class OrganismoDTORepositoryImpl extends LigthDTORepository<Organismo, OrganismoDTO> implements OrganismoDTORepository {

    public OrganismoDTORepositoryImpl() {
        super(Organismo.class, new OrganismoDTO());
    }

    @Override
    public Page<OrganismoDTO> findAllDTO(Specification<Organismo> specification, Pageable pageable) {
        return project_filter_paginateDTO(specification, pageable);
    }

    @Override
    public Page<Organismo> findAllLight(Specification<Organismo> specification, Pageable pageable) {
        return findAllDTO(specification, pageable).map(o -> o);
    }
}
