package org.semprotdb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class OrganismoCriteriaTest {

    @Test
    void newOrganismoCriteriaHasAllFiltersNullTest() {
        var organismoCriteria = new OrganismoCriteria();
        assertThat(organismoCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void organismoCriteriaFluentMethodsCreatesFiltersTest() {
        var organismoCriteria = new OrganismoCriteria();

        setAllFilters(organismoCriteria);

        assertThat(organismoCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void organismoCriteriaCopyCreatesNullFilterTest() {
        var organismoCriteria = new OrganismoCriteria();
        var copy = organismoCriteria.copy();

        assertThat(organismoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(organismoCriteria)
        );
    }

    @Test
    void organismoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var organismoCriteria = new OrganismoCriteria();
        setAllFilters(organismoCriteria);

        var copy = organismoCriteria.copy();

        assertThat(organismoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(organismoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var organismoCriteria = new OrganismoCriteria();

        assertThat(organismoCriteria).hasToString("OrganismoCriteria{}");
    }

    private static void setAllFilters(OrganismoCriteria organismoCriteria) {
        organismoCriteria.id();
        organismoCriteria.nome();
        organismoCriteria.sigla();
        organismoCriteria.apelido();
        organismoCriteria.icone();
        organismoCriteria.pos();
        organismoCriteria.imagem();
        organismoCriteria.descricao();
        organismoCriteria.geneId();
        organismoCriteria.distinct();
    }

    private static Condition<OrganismoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNome()) &&
                condition.apply(criteria.getSigla()) &&
                condition.apply(criteria.getApelido()) &&
                condition.apply(criteria.getIcone()) &&
                condition.apply(criteria.getPos()) &&
                condition.apply(criteria.getImagem()) &&
                condition.apply(criteria.getDescricao()) &&
                condition.apply(criteria.getGeneId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<OrganismoCriteria> copyFiltersAre(OrganismoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNome(), copy.getNome()) &&
                condition.apply(criteria.getSigla(), copy.getSigla()) &&
                condition.apply(criteria.getApelido(), copy.getApelido()) &&
                condition.apply(criteria.getIcone(), copy.getIcone()) &&
                condition.apply(criteria.getPos(), copy.getPos()) &&
                condition.apply(criteria.getImagem(), copy.getImagem()) &&
                condition.apply(criteria.getDescricao(), copy.getDescricao()) &&
                condition.apply(criteria.getGeneId(), copy.getGeneId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
