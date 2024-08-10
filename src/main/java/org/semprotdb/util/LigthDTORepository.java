package org.semprotdb.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import org.semprotdb.service.dto.VersaoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

public abstract class LigthDTORepository<T, D extends T> {

    private final Class<T> tipoT;
    private final Class<D> tipoD;

    private final String[] params;
    private CriteriaBuilder builder;
    private CriteriaQuery<D> criteria;
    private Root<T> root;
    private EntityManager entityManager;

    public LigthDTORepository(Class<T> tipoT, Class<D> tipoD) {
        this.tipoT = tipoT;
        this.tipoD = tipoD;
        Constructor<?> constructor = Arrays.stream(this.tipoD.getConstructors())
            .filter(c -> c.getParameters().length > 0 && c.getParameters()[0].getType() == Long.class)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Wrong DTO constructors " + VersaoDTO.class.getSimpleName()));
        params = Arrays.stream(constructor.getParameters()).map(p -> p.getName()).toArray(String[]::new);
    }

    private LigthDTORepository project(EntityManager em) {
        entityManager = em;
        builder = entityManager.getCriteriaBuilder();
        criteria = builder.createQuery(this.tipoD);
        root = criteria.from(this.tipoT);
        criteria.select(builder.construct(this.tipoD, Arrays.stream(params).map(p -> root.get(p)).toList().toArray(new Path[] {})));
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
        query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
        entityManager = null;
        return new PageImpl<>(query.getResultList());
    }

    public Page<D> project_filter_paginateDTO(EntityManager em, Specification<T> spec, Pageable pag) {
        Page page = this.project(em).filter(spec).paginate(pag);
        return page;
    }
}
