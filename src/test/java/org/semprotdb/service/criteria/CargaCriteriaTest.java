package org.semprotdb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CargaCriteriaTest {

    @Test
    void newCargaCriteriaHasAllFiltersNullTest() {
        var cargaCriteria = new CargaCriteria();
        assertThat(cargaCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void cargaCriteriaFluentMethodsCreatesFiltersTest() {
        var cargaCriteria = new CargaCriteria();

        setAllFilters(cargaCriteria);

        assertThat(cargaCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void cargaCriteriaCopyCreatesNullFilterTest() {
        var cargaCriteria = new CargaCriteria();
        var copy = cargaCriteria.copy();

        assertThat(cargaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(cargaCriteria)
        );
    }

    @Test
    void cargaCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cargaCriteria = new CargaCriteria();
        setAllFilters(cargaCriteria);

        var copy = cargaCriteria.copy();

        assertThat(cargaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(cargaCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cargaCriteria = new CargaCriteria();

        assertThat(cargaCriteria).hasToString("CargaCriteria{}");
    }

    private static void setAllFilters(CargaCriteria cargaCriteria) {
        cargaCriteria.id();
        cargaCriteria.status();
        cargaCriteria.ordem();
        cargaCriteria.nome();
        cargaCriteria.caminho();
        cargaCriteria.validado();
        cargaCriteria.tipo();
        cargaCriteria.formato();
        cargaCriteria.destino();
        cargaCriteria.linhas();
        cargaCriteria.checksum();
        cargaCriteria.versaoId();
        cargaCriteria.distinct();
    }

    private static Condition<CargaCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getOrdem()) &&
                condition.apply(criteria.getNome()) &&
                condition.apply(criteria.getCaminho()) &&
                condition.apply(criteria.getValidado()) &&
                condition.apply(criteria.getTipo()) &&
                condition.apply(criteria.getFormato()) &&
                condition.apply(criteria.getDestino()) &&
                condition.apply(criteria.getLinhas()) &&
                condition.apply(criteria.getChecksum()) &&
                condition.apply(criteria.getVersaoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CargaCriteria> copyFiltersAre(CargaCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getOrdem(), copy.getOrdem()) &&
                condition.apply(criteria.getNome(), copy.getNome()) &&
                condition.apply(criteria.getCaminho(), copy.getCaminho()) &&
                condition.apply(criteria.getValidado(), copy.getValidado()) &&
                condition.apply(criteria.getTipo(), copy.getTipo()) &&
                condition.apply(criteria.getFormato(), copy.getFormato()) &&
                condition.apply(criteria.getDestino(), copy.getDestino()) &&
                condition.apply(criteria.getLinhas(), copy.getLinhas()) &&
                condition.apply(criteria.getChecksum(), copy.getChecksum()) &&
                condition.apply(criteria.getVersaoId(), copy.getVersaoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
