package org.semprotdb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class GeneCriteriaTest {

    @Test
    void newGeneCriteriaHasAllFiltersNullTest() {
        var geneCriteria = new GeneCriteria();
        assertThat(geneCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void geneCriteriaFluentMethodsCreatesFiltersTest() {
        var geneCriteria = new GeneCriteria();

        setAllFilters(geneCriteria);

        assertThat(geneCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void geneCriteriaCopyCreatesNullFilterTest() {
        var geneCriteria = new GeneCriteria();
        var copy = geneCriteria.copy();

        assertThat(geneCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(geneCriteria)
        );
    }

    @Test
    void geneCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var geneCriteria = new GeneCriteria();
        setAllFilters(geneCriteria);

        var copy = geneCriteria.copy();

        assertThat(geneCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(geneCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var geneCriteria = new GeneCriteria();

        assertThat(geneCriteria).hasToString("GeneCriteria{}");
    }

    private static void setAllFilters(GeneCriteria geneCriteria) {
        geneCriteria.id();
        geneCriteria.nome();
        geneCriteria.descricao();
        geneCriteria.curadoriaId();
        geneCriteria.organismoId();
        geneCriteria.proteinaId();
        geneCriteria.distinct();
    }

    private static Condition<GeneCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNome()) &&
                condition.apply(criteria.getDescricao()) &&
                condition.apply(criteria.getCuradoriaId()) &&
                condition.apply(criteria.getOrganismoId()) &&
                condition.apply(criteria.getProteinaId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<GeneCriteria> copyFiltersAre(GeneCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNome(), copy.getNome()) &&
                condition.apply(criteria.getDescricao(), copy.getDescricao()) &&
                condition.apply(criteria.getCuradoriaId(), copy.getCuradoriaId()) &&
                condition.apply(criteria.getOrganismoId(), copy.getOrganismoId()) &&
                condition.apply(criteria.getProteinaId(), copy.getProteinaId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
