package org.semprotdb.repository;

import org.semprotdb.domain.Organismo;
import org.semprotdb.service.dto.OrganismoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrganismoDTORepository {
    Page<OrganismoDTO> findAllDTO(Specification<Organismo> specification, Pageable pageable);

    Page<Organismo> findAllLight(Specification<Organismo> specification, Pageable pageable);
}
