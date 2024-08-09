package org.semprotdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semprotdb.domain.CuradoriaTestSamples.*;
import static org.semprotdb.domain.GeneTestSamples.*;
import static org.semprotdb.domain.ProteinaTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.semprotdb.web.rest.TestUtil;

class CuradoriaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Curadoria.class);
        Curadoria curadoria1 = getCuradoriaSample1();
        Curadoria curadoria2 = new Curadoria();
        assertThat(curadoria1).isNotEqualTo(curadoria2);

        curadoria2.setId(curadoria1.getId());
        assertThat(curadoria1).isEqualTo(curadoria2);

        curadoria2 = getCuradoriaSample2();
        assertThat(curadoria1).isNotEqualTo(curadoria2);
    }

    @Test
    void proteinaTest() {
        Curadoria curadoria = getCuradoriaRandomSampleGenerator();
        Proteina proteinaBack = getProteinaRandomSampleGenerator();

        curadoria.addProteina(proteinaBack);
        assertThat(curadoria.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getCuradoria()).isEqualTo(curadoria);

        curadoria.removeProteina(proteinaBack);
        assertThat(curadoria.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getCuradoria()).isNull();

        curadoria.proteinas(new HashSet<>(Set.of(proteinaBack)));
        assertThat(curadoria.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getCuradoria()).isEqualTo(curadoria);

        curadoria.setProteinas(new HashSet<>());
        assertThat(curadoria.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getCuradoria()).isNull();
    }

    @Test
    void geneTest() {
        Curadoria curadoria = getCuradoriaRandomSampleGenerator();
        Gene geneBack = getGeneRandomSampleGenerator();

        curadoria.addGene(geneBack);
        assertThat(curadoria.getGenes()).containsOnly(geneBack);
        assertThat(geneBack.getCuradoria()).isEqualTo(curadoria);

        curadoria.removeGene(geneBack);
        assertThat(curadoria.getGenes()).doesNotContain(geneBack);
        assertThat(geneBack.getCuradoria()).isNull();

        curadoria.genes(new HashSet<>(Set.of(geneBack)));
        assertThat(curadoria.getGenes()).containsOnly(geneBack);
        assertThat(geneBack.getCuradoria()).isEqualTo(curadoria);

        curadoria.setGenes(new HashSet<>());
        assertThat(curadoria.getGenes()).doesNotContain(geneBack);
        assertThat(geneBack.getCuradoria()).isNull();
    }
}
