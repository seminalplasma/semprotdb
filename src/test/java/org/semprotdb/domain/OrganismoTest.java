package org.semprotdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semprotdb.domain.GeneTestSamples.*;
import static org.semprotdb.domain.OrganismoTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.semprotdb.web.rest.TestUtil;

class OrganismoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Organismo.class);
        Organismo organismo1 = getOrganismoSample1();
        Organismo organismo2 = new Organismo();
        assertThat(organismo1).isNotEqualTo(organismo2);

        organismo2.setId(organismo1.getId());
        assertThat(organismo1).isEqualTo(organismo2);

        organismo2 = getOrganismoSample2();
        assertThat(organismo1).isNotEqualTo(organismo2);
    }

    @Test
    void geneTest() {
        Organismo organismo = getOrganismoRandomSampleGenerator();
        Gene geneBack = getGeneRandomSampleGenerator();

        organismo.addGene(geneBack);
        assertThat(organismo.getGenes()).containsOnly(geneBack);
        assertThat(geneBack.getOrganismo()).isEqualTo(organismo);

        organismo.removeGene(geneBack);
        assertThat(organismo.getGenes()).doesNotContain(geneBack);
        assertThat(geneBack.getOrganismo()).isNull();

        organismo.genes(new HashSet<>(Set.of(geneBack)));
        assertThat(organismo.getGenes()).containsOnly(geneBack);
        assertThat(geneBack.getOrganismo()).isEqualTo(organismo);

        organismo.setGenes(new HashSet<>());
        assertThat(organismo.getGenes()).doesNotContain(geneBack);
        assertThat(geneBack.getOrganismo()).isNull();
    }
}
