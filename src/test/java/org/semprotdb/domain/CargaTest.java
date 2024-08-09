package org.semprotdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semprotdb.domain.CargaTestSamples.*;
import static org.semprotdb.domain.VersaoTestSamples.*;

import org.junit.jupiter.api.Test;
import org.semprotdb.web.rest.TestUtil;

class CargaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Carga.class);
        Carga carga1 = getCargaSample1();
        Carga carga2 = new Carga();
        assertThat(carga1).isNotEqualTo(carga2);

        carga2.setId(carga1.getId());
        assertThat(carga1).isEqualTo(carga2);

        carga2 = getCargaSample2();
        assertThat(carga1).isNotEqualTo(carga2);
    }

    @Test
    void versaoTest() {
        Carga carga = getCargaRandomSampleGenerator();
        Versao versaoBack = getVersaoRandomSampleGenerator();

        carga.setVersao(versaoBack);
        assertThat(carga.getVersao()).isEqualTo(versaoBack);

        carga.versao(null);
        assertThat(carga.getVersao()).isNull();
    }
}
