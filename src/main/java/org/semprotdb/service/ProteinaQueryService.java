package org.semprotdb.service;

import jakarta.persistence.criteria.JoinType;
import org.semprotdb.domain.*; // for static metamodels
import org.semprotdb.domain.Proteina;
import org.semprotdb.repository.ProteinaRepository;
import org.semprotdb.service.criteria.ProteinaCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Proteina} entities in the database.
 * The main input is a {@link ProteinaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Proteina} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProteinaQueryService extends QueryService<Proteina> {

    private static final Logger log = LoggerFactory.getLogger(ProteinaQueryService.class);

    private final ProteinaRepository proteinaRepository;

    public ProteinaQueryService(ProteinaRepository proteinaRepository) {
        this.proteinaRepository = proteinaRepository;
    }

    /**
     * Return a {@link Page} of {@link Proteina} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Proteina> findByCriteria(ProteinaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Proteina> specification = createSpecification(criteria);
        ///return proteinaRepository.fetchBagRelationships(proteinaRepository.findAll(specification, page));

        return proteinaRepository.fetchBagRelationships(proteinaRepository.findAllLight(specification, page));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProteinaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Proteina> specification = createSpecification(criteria);
        return proteinaRepository.count(specification);
    }

    /**
     * Function to convert {@link ProteinaCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Proteina> createSpecification(ProteinaCriteria criteria) {
        Specification<Proteina> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Proteina_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Proteina_.nome));
            }
            if (criteria.getTamanho() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTamanho(), Proteina_.tamanho));
            }
            if (criteria.getMassa() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMassa(), Proteina_.massa));
            }
            if (criteria.getDescricao() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescricao(), Proteina_.descricao));
            }
            if (criteria.getCuradoriaId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCuradoriaId(), root -> root.join(Proteina_.curadoria, JoinType.LEFT).get(Curadoria_.id))
                );
            }
            if (criteria.getVersaoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getVersaoId(), root -> root.join(Proteina_.versao, JoinType.LEFT).get(Versao_.id))
                );
            }
            if (criteria.getGeneId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getGeneId(), root -> root.join(Proteina_.gene, JoinType.LEFT).get(Gene_.id))
                );
            }
            if (criteria.getOrganismoId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getOrganismoId(),
                        root -> root.join(Proteina_.gene, JoinType.LEFT).get(Gene_.organismo).get(Organismo_.id)
                    )
                );
            }
            if (criteria.getReferenciaId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getReferenciaId(),
                        root -> root.join(Proteina_.referencias, JoinType.LEFT).get(Referencia_.id)
                    )
                );
            }
            if (criteria.getRecursoId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getRecursoId(), root -> root.join(Proteina_.recursos, JoinType.LEFT).get(Recurso_.id))
                );
            }
        }
        return specification;
    }
}
