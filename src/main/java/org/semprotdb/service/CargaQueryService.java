package org.semprotdb.service;

import jakarta.persistence.criteria.JoinType;
import org.semprotdb.domain.*; // for static metamodels
import org.semprotdb.domain.Carga;
import org.semprotdb.repository.CargaRepository;
import org.semprotdb.service.criteria.CargaCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Carga} entities in the database.
 * The main input is a {@link CargaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Carga} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CargaQueryService extends QueryService<Carga> {

    private static final Logger log = LoggerFactory.getLogger(CargaQueryService.class);

    private final CargaRepository cargaRepository;

    public CargaQueryService(CargaRepository cargaRepository) {
        this.cargaRepository = cargaRepository;
    }

    /**
     * Return a {@link Page} of {@link Carga} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Carga> findByCriteria(CargaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Carga> specification = createSpecification(criteria);
        return cargaRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CargaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Carga> specification = createSpecification(criteria);
        return cargaRepository.count(specification);
    }

    /**
     * Function to convert {@link CargaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Carga> createSpecification(CargaCriteria criteria) {
        Specification<Carga> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Carga_.id));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStatus(), Carga_.status));
            }
            if (criteria.getOrdem() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrdem(), Carga_.ordem));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Carga_.nome));
            }
            if (criteria.getCaminho() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCaminho(), Carga_.caminho));
            }
            if (criteria.getValidado() != null) {
                specification = specification.and(buildSpecification(criteria.getValidado(), Carga_.validado));
            }
            if (criteria.getTipo() != null) {
                specification = specification.and(buildSpecification(criteria.getTipo(), Carga_.tipo));
            }
            if (criteria.getFormato() != null) {
                specification = specification.and(buildSpecification(criteria.getFormato(), Carga_.formato));
            }
            if (criteria.getDestino() != null) {
                specification = specification.and(buildSpecification(criteria.getDestino(), Carga_.destino));
            }
            if (criteria.getLinhas() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLinhas(), Carga_.linhas));
            }
            if (criteria.getChecksum() != null) {
                specification = specification.and(buildStringSpecification(criteria.getChecksum(), Carga_.checksum));
            }
            if (criteria.getVersaoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getVersaoId(), root -> root.join(Carga_.versao, JoinType.LEFT).get(Versao_.id))
                );
            }
        }
        return specification;
    }
}
