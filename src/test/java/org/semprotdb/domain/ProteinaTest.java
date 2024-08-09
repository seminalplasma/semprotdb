package org.semprotdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semprotdb.domain.CuradoriaTestSamples.*;
import static org.semprotdb.domain.GeneTestSamples.*;
import static org.semprotdb.domain.ProteinaTestSamples.*;
import static org.semprotdb.domain.RecursoTestSamples.*;
import static org.semprotdb.domain.ReferenciaTestSamples.*;
import static org.semprotdb.domain.VersaoTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.semprotdb.web.rest.TestUtil;

class ProteinaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Proteina.class);
        Proteina proteina1 = getProteinaSample1();
        Proteina proteina2 = new Proteina();
        assertThat(proteina1).isNotEqualTo(proteina2);

        proteina2.setId(proteina1.getId());
        assertThat(proteina1).isEqualTo(proteina2);

        proteina2 = getProteinaSample2();
        assertThat(proteina1).isNotEqualTo(proteina2);
    }

    @Test
    void curadoriaTest() {
        Proteina proteina = getProteinaRandomSampleGenerator();
        Curadoria curadoriaBack = getCuradoriaRandomSampleGenerator();

        proteina.setCuradoria(curadoriaBack);
        assertThat(proteina.getCuradoria()).isEqualTo(curadoriaBack);

        proteina.curadoria(null);
        assertThat(proteina.getCuradoria()).isNull();
    }

    @Test
    void versaoTest() {
        Proteina proteina = getProteinaRandomSampleGenerator();
        Versao versaoBack = getVersaoRandomSampleGenerator();

        proteina.setVersao(versaoBack);
        assertThat(proteina.getVersao()).isEqualTo(versaoBack);

        proteina.versao(null);
        assertThat(proteina.getVersao()).isNull();
    }

    @Test
    void geneTest() {
        Proteina proteina = getProteinaRandomSampleGenerator();
        Gene geneBack = getGeneRandomSampleGenerator();

        proteina.setGene(geneBack);
        assertThat(proteina.getGene()).isEqualTo(geneBack);

        proteina.gene(null);
        assertThat(proteina.getGene()).isNull();
    }

    @Test
    void referenciaTest() {
        Proteina proteina = getProteinaRandomSampleGenerator();
        Referencia referenciaBack = getReferenciaRandomSampleGenerator();

        proteina.addReferencia(referenciaBack);
        assertThat(proteina.getReferencias()).containsOnly(referenciaBack);

        proteina.removeReferencia(referenciaBack);
        assertThat(proteina.getReferencias()).doesNotContain(referenciaBack);

        proteina.referencias(new HashSet<>(Set.of(referenciaBack)));
        assertThat(proteina.getReferencias()).containsOnly(referenciaBack);

        proteina.setReferencias(new HashSet<>());
        assertThat(proteina.getReferencias()).doesNotContain(referenciaBack);
    }

    @Test
    void recursoTest() {
        Proteina proteina = getProteinaRandomSampleGenerator();
        Recurso recursoBack = getRecursoRandomSampleGenerator();

        proteina.addRecurso(recursoBack);
        assertThat(proteina.getRecursos()).containsOnly(recursoBack);

        proteina.removeRecurso(recursoBack);
        assertThat(proteina.getRecursos()).doesNotContain(recursoBack);

        proteina.recursos(new HashSet<>(Set.of(recursoBack)));
        assertThat(proteina.getRecursos()).containsOnly(recursoBack);

        proteina.setRecursos(new HashSet<>());
        assertThat(proteina.getRecursos()).doesNotContain(recursoBack);
    }
}
