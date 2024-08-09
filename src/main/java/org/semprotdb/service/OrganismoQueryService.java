package org.semprotdb.service;

import jakarta.persistence.criteria.JoinType;
import org.semprotdb.domain.*; // for static metamodels
import org.semprotdb.domain.Organismo;
import org.semprotdb.repository.OrganismoRepository;
import org.semprotdb.service.criteria.OrganismoCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Organismo} entities in the database.
 * The main input is a {@link OrganismoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Organismo} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrganismoQueryService extends QueryService<Organismo> {

    private static final Logger log = LoggerFactory.getLogger(OrganismoQueryService.class);

    private final OrganismoRepository organismoRepository;

    public OrganismoQueryService(OrganismoRepository organismoRepository) {
        this.organismoRepository = organismoRepository;
    }

    /**
     * Return a {@link Page} of {@link Organismo} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Organismo> findByCriteria(OrganismoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Organismo> specification = createSpecification(criteria);
        return organismoRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrganismoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Organismo> specification = createSpecification(criteria);
        return organismoRepository.count(specification);
    }

    /**
     * Function to convert {@link OrganismoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Organismo> createSpecification(OrganismoCriteria criteria) {
        Specification<Organismo> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Organismo_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Organismo_.nome));
            }
            if (criteria.getSigla() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSigla(), Organismo_.sigla));
            }
            if (criteria.getApelido() != null) {
                specification = specification.and(buildStringSpecification(criteria.getApelido(), Organismo_.apelido));
            }
            if (criteria.getIcone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIcone(), Organismo_.icone));
            }
            if (criteria.getPos() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPos(), Organismo_.pos));
            }
            if (criteria.getImagem() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImagem(), Organismo_.imagem));
            }
            if (criteria.getDescricao() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescricao(), Organismo_.descricao));
            }
            if (criteria.getGeneId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getGeneId(), root -> root.join(Organismo_.genes, JoinType.LEFT).get(Gene_.id))
                );
            }
        }
        return specification;
    }
}
