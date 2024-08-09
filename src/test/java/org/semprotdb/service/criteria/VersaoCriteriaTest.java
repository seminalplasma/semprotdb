package org.semprotdb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class VersaoCriteriaTest {

    @Test
    void newVersaoCriteriaHasAllFiltersNullTest() {
        var versaoCriteria = new VersaoCriteria();
        assertThat(versaoCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void versaoCriteriaFluentMethodsCreatesFiltersTest() {
        var versaoCriteria = new VersaoCriteria();

        setAllFilters(versaoCriteria);

        assertThat(versaoCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void versaoCriteriaCopyCreatesNullFilterTest() {
        var versaoCriteria = new VersaoCriteria();
        var copy = versaoCriteria.copy();

        assertThat(versaoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(versaoCriteria)
        );
    }

    @Test
    void versaoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var versaoCriteria = new VersaoCriteria();
        setAllFilters(versaoCriteria);

        var copy = versaoCriteria.copy();

        assertThat(versaoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(versaoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var versaoCriteria = new VersaoCriteria();

        assertThat(versaoCriteria).hasToString("VersaoCriteria{}");
    }

    private static void setAllFilters(VersaoCriteria versaoCriteria) {
        versaoCriteria.id();
        versaoCriteria.nome();
        versaoCriteria.detalhes();
        versaoCriteria.release();
        versaoCriteria.label();
        versaoCriteria.status();
        versaoCriteria.numero();
        versaoCriteria.logo();
        versaoCriteria.proteinaId();
        versaoCriteria.cargaId();
        versaoCriteria.distinct();
    }

    private static Condition<VersaoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNome()) &&
                condition.apply(criteria.getDetalhes()) &&
                condition.apply(criteria.getRelease()) &&
                condition.apply(criteria.getLabel()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getNumero()) &&
                condition.apply(criteria.getLogo()) &&
                condition.apply(criteria.getProteinaId()) &&
                condition.apply(criteria.getCargaId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<VersaoCriteria> copyFiltersAre(VersaoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNome(), copy.getNome()) &&
                condition.apply(criteria.getDetalhes(), copy.getDetalhes()) &&
                condition.apply(criteria.getRelease(), copy.getRelease()) &&
                condition.apply(criteria.getLabel(), copy.getLabel()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getNumero(), copy.getNumero()) &&
                condition.apply(criteria.getLogo(), copy.getLogo()) &&
                condition.apply(criteria.getProteinaId(), copy.getProteinaId()) &&
                condition.apply(criteria.getCargaId(), copy.getCargaId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
