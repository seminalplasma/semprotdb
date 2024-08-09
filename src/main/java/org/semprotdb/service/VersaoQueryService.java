package org.semprotdb.service;

import jakarta.persistence.criteria.JoinType;
import org.semprotdb.domain.*; // for static metamodels
import org.semprotdb.domain.Versao;
import org.semprotdb.repository.VersaoRepository;
import org.semprotdb.service.criteria.VersaoCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Versao} entities in the database.
 * The main input is a {@link VersaoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Versao} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VersaoQueryService extends QueryService<Versao> {

    private static final Logger log = LoggerFactory.getLogger(VersaoQueryService.class);

    private final VersaoRepository versaoRepository;

    public VersaoQueryService(VersaoRepository versaoRepository) {
        this.versaoRepository = versaoRepository;
    }

    /**
     * Return a {@link Page} of {@link Versao} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Versao> findByCriteria(VersaoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Versao> specification = createSpecification(criteria);
        return versaoRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VersaoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Versao> specification = createSpecification(criteria);
        return versaoRepository.count(specification);
    }

    /**
     * Function to convert {@link VersaoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Versao> createSpecification(VersaoCriteria criteria) {
        Specification<Versao> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Versao_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Versao_.nome));
            }
            if (criteria.getDetalhes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDetalhes(), Versao_.detalhes));
            }
            if (criteria.getRelease() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRelease(), Versao_.release));
            }
            if (criteria.getLabel() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLabel(), Versao_.label));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Versao_.status));
            }
            if (criteria.getNumero() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumero(), Versao_.numero));
            }
            if (criteria.getLogo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLogo(), Versao_.logo));
            }
            if (criteria.getProteinaId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProteinaId(), root -> root.join(Versao_.proteinas, JoinType.LEFT).get(Proteina_.id))
                );
            }
            if (criteria.getCargaId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCargaId(), root -> root.join(Versao_.cargas, JoinType.LEFT).get(Carga_.id))
                );
            }
        }
        return specification;
    }
}
