package org.semprotdb.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.semprotdb.service.dto.IDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

public abstract class LigthDTORepository<T, D extends IDTO> {

    private final Class<T> tipoT;
    private final Class<D> tipoD;
    private final D dto;

    @PersistenceContext
    private EntityManager entityManager;

    private CriteriaBuilder builder;
    private CriteriaQuery<D> criteria;
    private Root<T> root;

    public LigthDTORepository(Class<T> tipoT, D tipoD) {
        this.tipoT = tipoT;
        this.tipoD = (Class<D>) tipoD.getClass();
        this.dto = tipoD;
    }

    private LigthDTORepository project() {
        builder = entityManager.getCriteriaBuilder();
        criteria = builder.createQuery(this.tipoD);
        root = criteria.from(this.tipoT);
        criteria.select(builder.construct(this.tipoD, dto.getConstructorArgsPath(root)));
        return this;
    }

    private LigthDTORepository filter(Specification<T> specification) {
        if (specification != null) {
            Predicate predicate = specification.toPredicate(root, criteria, builder);
            if (predicate != null) {
                criteria.where(predicate);
            }
        }
        return this;
    }

    private Page<D> paginate(Pageable pageable) {
        //sorting
        criteria.orderBy(QueryUtils.toOrders(pageable.getSort(), root, builder));

        //pagination
        TypedQuery<D> query = entityManager.createQuery(criteria);
        if (pageable.isPaged()) query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, entityManager.createQuery(criteria).getResultStream().count());
    }

    public Page<D> project_filter_paginateDTO(Specification<T> spec, Pageable pag) {
        return this.project().filter(spec).paginate(pag);
    }

    public Page<D> project_paginateDTO(Pageable pag) {
        return this.project().filter(null).paginate(pag);
    }
}
