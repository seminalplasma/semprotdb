package org.semprotdb.service;

import jakarta.persistence.criteria.JoinType;
import org.semprotdb.domain.*; // for static metamodels
import org.semprotdb.domain.Gene;
import org.semprotdb.repository.GeneRepository;
import org.semprotdb.service.criteria.GeneCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Gene} entities in the database.
 * The main input is a {@link GeneCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Gene} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class GeneQueryService extends QueryService<Gene> {

    private static final Logger log = LoggerFactory.getLogger(GeneQueryService.class);

    private final GeneRepository geneRepository;

    public GeneQueryService(GeneRepository geneRepository) {
        this.geneRepository = geneRepository;
    }

    /**
     * Return a {@link Page} of {@link Gene} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Gene> findByCriteria(GeneCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Gene> specification = createSpecification(criteria);
        return geneRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(GeneCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Gene> specification = createSpecification(criteria);
        return geneRepository.count(specification);
    }

    /**
     * Function to convert {@link GeneCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Gene> createSpecification(GeneCriteria criteria) {
        Specification<Gene> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Gene_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Gene_.nome));
            }
            if (criteria.getDescricao() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescricao(), Gene_.descricao));
            }
            if (criteria.getCuradoriaId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCuradoriaId(), root -> root.join(Gene_.curadoria, JoinType.LEFT).get(Curadoria_.id))
                );
            }
            if (criteria.getOrganismoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getOrganismoId(), root -> root.join(Gene_.organismo, JoinType.LEFT).get(Organismo_.id))
                );
            }
            if (criteria.getProteinaId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProteinaId(), root -> root.join(Gene_.proteinas, JoinType.LEFT).get(Proteina_.id))
                );
            }
        }
        return specification;
    }
}
