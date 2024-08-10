package org.semprotdb.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private final HashMap<String, String[]> joins = new HashMap<>();

    @PersistenceContext
    private EntityManager entityManager;

    private CriteriaBuilder builder;
    private CriteriaQuery<D> criteria;
    private Root<T> root;

    public LigthDTORepository(Class<T> tipoT, Class<D> tipoD) {
        this.tipoT = tipoT;
        this.tipoD = tipoD;
        getJoins().forEach((e, c) -> this.joins.put(e, findParams(c)));
        ArrayList<String> filterCol = new ArrayList<>();
        this.joins.forEach((k, vs) -> Arrays.stream(vs).map(v -> k + v.toUpperCase()).forEach(filterCol::add));
        this.params = Arrays.stream(findParams(this.tipoD)).filter(x -> !filterCol.contains(x)).toList().toArray(String[]::new);
    }

    public HashMap<String, Class> getJoins() {
        return new HashMap<>();
    }

    private String[] findParams(Class klass) {
        Constructor<?> constructor = Arrays.stream(klass.getConstructors())
            .filter(c -> c.getParameters().length > 0 && c.getParameters()[0].getType() == Long.class)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Wrong DTO constructors " + VersaoDTO.class.getSimpleName()));
        return Arrays.stream(constructor.getParameters()).map(Parameter::getName).toArray(String[]::new);
    }

    private LigthDTORepository project() {
        builder = entityManager.getCriteriaBuilder();
        criteria = builder.createQuery(this.tipoD);
        root = criteria.from(this.tipoT);

        ArrayList<Path> fields = new ArrayList<>();
        fields.addAll(Arrays.stream(params).map(p -> root.get(p)).toList());
        joins.forEach((c, v) -> {
            Join filho = root.join(c);
            Arrays.stream(v).forEach(x -> {
                Path path = filho.get(x);
                path.alias(c + x.toUpperCase());
                fields.add(path);
            });
        });

        criteria.select(builder.construct(this.tipoD, fields.toArray(new Path[] {})));
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
        return new PageImpl<>(query.getResultList());
    }

    public Page<D> project_filter_paginateDTO(Specification<T> spec, Pageable pag) {
        return this.project().filter(spec).paginate(pag);
    }
}
