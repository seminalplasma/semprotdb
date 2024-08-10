package org.semprotdb.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.semprotdb.domain.Versao;
import org.semprotdb.service.dto.VersaoDTO;
import org.semprotdb.util.LigthDTORepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class VersaoDTORepositoryImpl extends LigthDTORepository<Versao, VersaoDTO> implements VersaoDTORepository {

    @PersistenceContext
    private EntityManager entityManager;

    public VersaoDTORepositoryImpl() {
        super(Versao.class, VersaoDTO.class);
    }

    @Override
    public Page<VersaoDTO> findAllDTO(Specification<Versao> specification, Pageable pageable) {
        return project_filter_paginateDTO(entityManager, specification, pageable);
    }

    @Override
    public Page<Versao> findAllLight(Specification<Versao> specification, Pageable pageable) {
        return findAllDTO(specification, pageable).map(x -> x);
    }
}
