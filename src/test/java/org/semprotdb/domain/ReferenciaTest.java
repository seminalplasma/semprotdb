package org.semprotdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semprotdb.domain.ProteinaTestSamples.*;
import static org.semprotdb.domain.ReferenciaTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.semprotdb.web.rest.TestUtil;

class ReferenciaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Referencia.class);
        Referencia referencia1 = getReferenciaSample1();
        Referencia referencia2 = new Referencia();
        assertThat(referencia1).isNotEqualTo(referencia2);

        referencia2.setId(referencia1.getId());
        assertThat(referencia1).isEqualTo(referencia2);

        referencia2 = getReferenciaSample2();
        assertThat(referencia1).isNotEqualTo(referencia2);
    }

    @Test
    void proteinaTest() {
        Referencia referencia = getReferenciaRandomSampleGenerator();
        Proteina proteinaBack = getProteinaRandomSampleGenerator();

        referencia.addProteina(proteinaBack);
        assertThat(referencia.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getReferencias()).containsOnly(referencia);

        referencia.removeProteina(proteinaBack);
        assertThat(referencia.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getReferencias()).doesNotContain(referencia);

        referencia.proteinas(new HashSet<>(Set.of(proteinaBack)));
        assertThat(referencia.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getReferencias()).containsOnly(referencia);

        referencia.setProteinas(new HashSet<>());
        assertThat(referencia.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getReferencias()).doesNotContain(referencia);
    }
}
