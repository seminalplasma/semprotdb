package org.semprotdb.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.IntStream;
import org.semprotdb.domain.Proteina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ProteinaRepositoryWithBagRelationshipsImpl implements ProteinaRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PROTEINAS_PARAMETER = "proteinas";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Proteina> fetchBagRelationships(Optional<Proteina> proteina) {
        return proteina.map(this::fetchReferencias).map(this::fetchRecursos);
    }

    @Override
    public Page<Proteina> fetchBagRelationships(Page<Proteina> proteinas) {
        return new PageImpl<>(fetchBagRelationships(proteinas.getContent()), proteinas.getPageable(), proteinas.getTotalElements());
    }

    @Override
    public List<Proteina> fetchBagRelationships(List<Proteina> proteinas) {
        return Optional.of(proteinas).map(this::fetchReferencias).map(this::fetchRecursos).orElse(Collections.emptyList());
    }

    Proteina fetchReferencias(Proteina result) {
        return result.referencias(
            entityManager
                .createQuery(
                    "select proteina from Proteina proteina left join fetch proteina.referencias where proteina.id = :id",
                    Proteina.class
                )
                .setParameter(ID_PARAMETER, result.getId())
                .getSingleResult()
                .getReferencias()
        );
    }

    List<Proteina> fetchReferencias(List<Proteina> proteinas) {
        HashMap<Object, Integer> order = new HashMap<>();
        HashMap<Long, Proteina> prs = new HashMap<>();
        proteinas.forEach(p -> prs.put(p.getId(), p));
        IntStream.range(0, proteinas.size()).forEach(index -> order.put(proteinas.get(index).getId(), index));
        List<Proteina> result = new ArrayList<>(
            entityManager
                .createQuery(
                    "select proteina from Proteina proteina left join fetch proteina.referencias where proteina in :proteinas",
                    Proteina.class
                )
                .setParameter(PROTEINAS_PARAMETER, proteinas)
                .getResultList()
                .stream()
                .map(pf -> prs.get(pf.getId()).referencias(pf.getReferencias()))
                .toList()
        );
        Collections.sort(result, Comparator.comparingInt(o -> order.get(o.getId())));
        return result;
    }

    Proteina fetchRecursos(Proteina result) {
        return result.recursos(
            entityManager
                .createQuery(
                    "select proteina from Proteina proteina left join fetch proteina.recursos where proteina.id = :id",
                    Proteina.class
                )
                .setParameter(ID_PARAMETER, result.getId())
                .getSingleResult()
                .getRecursos()
        );
    }

    List<Proteina> fetchRecursos(List<Proteina> proteinas) {
        HashMap<Object, Integer> order = new HashMap<>();
        HashMap<Long, Proteina> prs = new HashMap<>();
        proteinas.forEach(p -> prs.put(p.getId(), p));
        IntStream.range(0, proteinas.size()).forEach(index -> order.put(proteinas.get(index).getId(), index));
        List<Proteina> result = new ArrayList<>(
            entityManager
                .createQuery(
                    "select proteina from Proteina proteina left join fetch proteina.recursos where proteina in :proteinas",
                    Proteina.class
                )
                .setParameter(PROTEINAS_PARAMETER, proteinas)
                .getResultList()
                .stream()
                .map(pf -> prs.get(pf.getId()).recursos(pf.getRecursos()))
                .toList()
        );
        Collections.sort(result, Comparator.comparingInt(o -> order.get(o.getId())));
        return result;
    }
}
