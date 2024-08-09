package org.semprotdb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProteinaCriteriaTest {

    @Test
    void newProteinaCriteriaHasAllFiltersNullTest() {
        var proteinaCriteria = new ProteinaCriteria();
        assertThat(proteinaCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void proteinaCriteriaFluentMethodsCreatesFiltersTest() {
        var proteinaCriteria = new ProteinaCriteria();

        setAllFilters(proteinaCriteria);

        assertThat(proteinaCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void proteinaCriteriaCopyCreatesNullFilterTest() {
        var proteinaCriteria = new ProteinaCriteria();
        var copy = proteinaCriteria.copy();

        assertThat(proteinaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(proteinaCriteria)
        );
    }

    @Test
    void proteinaCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var proteinaCriteria = new ProteinaCriteria();
        setAllFilters(proteinaCriteria);

        var copy = proteinaCriteria.copy();

        assertThat(proteinaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(proteinaCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var proteinaCriteria = new ProteinaCriteria();

        assertThat(proteinaCriteria).hasToString("ProteinaCriteria{}");
    }

    private static void setAllFilters(ProteinaCriteria proteinaCriteria) {
        proteinaCriteria.id();
        proteinaCriteria.nome();
        proteinaCriteria.tamanho();
        proteinaCriteria.massa();
        proteinaCriteria.descricao();
        proteinaCriteria.curadoriaId();
        proteinaCriteria.versaoId();
        proteinaCriteria.geneId();
        proteinaCriteria.referenciaId();
        proteinaCriteria.recursoId();
        proteinaCriteria.distinct();
    }

    private static Condition<ProteinaCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNome()) &&
                condition.apply(criteria.getTamanho()) &&
                condition.apply(criteria.getMassa()) &&
                condition.apply(criteria.getDescricao()) &&
                condition.apply(criteria.getCuradoriaId()) &&
                condition.apply(criteria.getVersaoId()) &&
                condition.apply(criteria.getGeneId()) &&
                condition.apply(criteria.getReferenciaId()) &&
                condition.apply(criteria.getRecursoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProteinaCriteria> copyFiltersAre(ProteinaCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNome(), copy.getNome()) &&
                condition.apply(criteria.getTamanho(), copy.getTamanho()) &&
                condition.apply(criteria.getMassa(), copy.getMassa()) &&
                condition.apply(criteria.getDescricao(), copy.getDescricao()) &&
                condition.apply(criteria.getCuradoriaId(), copy.getCuradoriaId()) &&
                condition.apply(criteria.getVersaoId(), copy.getVersaoId()) &&
                condition.apply(criteria.getGeneId(), copy.getGeneId()) &&
                condition.apply(criteria.getReferenciaId(), copy.getReferenciaId()) &&
                condition.apply(criteria.getRecursoId(), copy.getRecursoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
