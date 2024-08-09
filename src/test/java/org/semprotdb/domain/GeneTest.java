package org.semprotdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semprotdb.domain.CuradoriaTestSamples.*;
import static org.semprotdb.domain.GeneTestSamples.*;
import static org.semprotdb.domain.OrganismoTestSamples.*;
import static org.semprotdb.domain.ProteinaTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.semprotdb.web.rest.TestUtil;

class GeneTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Gene.class);
        Gene gene1 = getGeneSample1();
        Gene gene2 = new Gene();
        assertThat(gene1).isNotEqualTo(gene2);

        gene2.setId(gene1.getId());
        assertThat(gene1).isEqualTo(gene2);

        gene2 = getGeneSample2();
        assertThat(gene1).isNotEqualTo(gene2);
    }

    @Test
    void curadoriaTest() {
        Gene gene = getGeneRandomSampleGenerator();
        Curadoria curadoriaBack = getCuradoriaRandomSampleGenerator();

        gene.setCuradoria(curadoriaBack);
        assertThat(gene.getCuradoria()).isEqualTo(curadoriaBack);

        gene.curadoria(null);
        assertThat(gene.getCuradoria()).isNull();
    }

    @Test
    void organismoTest() {
        Gene gene = getGeneRandomSampleGenerator();
        Organismo organismoBack = getOrganismoRandomSampleGenerator();

        gene.setOrganismo(organismoBack);
        assertThat(gene.getOrganismo()).isEqualTo(organismoBack);

        gene.organismo(null);
        assertThat(gene.getOrganismo()).isNull();
    }

    @Test
    void proteinaTest() {
        Gene gene = getGeneRandomSampleGenerator();
        Proteina proteinaBack = getProteinaRandomSampleGenerator();

        gene.addProteina(proteinaBack);
        assertThat(gene.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getGene()).isEqualTo(gene);

        gene.removeProteina(proteinaBack);
        assertThat(gene.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getGene()).isNull();

        gene.proteinas(new HashSet<>(Set.of(proteinaBack)));
        assertThat(gene.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getGene()).isEqualTo(gene);

        gene.setProteinas(new HashSet<>());
        assertThat(gene.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getGene()).isNull();
    }
}
