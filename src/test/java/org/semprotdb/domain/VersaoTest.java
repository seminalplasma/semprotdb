package org.semprotdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semprotdb.domain.CargaTestSamples.*;
import static org.semprotdb.domain.ProteinaTestSamples.*;
import static org.semprotdb.domain.VersaoTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.semprotdb.web.rest.TestUtil;

class VersaoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Versao.class);
        Versao versao1 = getVersaoSample1();
        Versao versao2 = new Versao();
        assertThat(versao1).isNotEqualTo(versao2);

        versao2.setId(versao1.getId());
        assertThat(versao1).isEqualTo(versao2);

        versao2 = getVersaoSample2();
        assertThat(versao1).isNotEqualTo(versao2);
    }

    @Test
    void proteinaTest() {
        Versao versao = getVersaoRandomSampleGenerator();
        Proteina proteinaBack = getProteinaRandomSampleGenerator();

        versao.addProteina(proteinaBack);
        assertThat(versao.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getVersao()).isEqualTo(versao);

        versao.removeProteina(proteinaBack);
        assertThat(versao.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getVersao()).isNull();

        versao.proteinas(new HashSet<>(Set.of(proteinaBack)));
        assertThat(versao.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getVersao()).isEqualTo(versao);

        versao.setProteinas(new HashSet<>());
        assertThat(versao.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getVersao()).isNull();
    }

    @Test
    void cargaTest() {
        Versao versao = getVersaoRandomSampleGenerator();
        Carga cargaBack = getCargaRandomSampleGenerator();

        versao.addCarga(cargaBack);
        assertThat(versao.getCargas()).containsOnly(cargaBack);
        assertThat(cargaBack.getVersao()).isEqualTo(versao);

        versao.removeCarga(cargaBack);
        assertThat(versao.getCargas()).doesNotContain(cargaBack);
        assertThat(cargaBack.getVersao()).isNull();

        versao.cargas(new HashSet<>(Set.of(cargaBack)));
        assertThat(versao.getCargas()).containsOnly(cargaBack);
        assertThat(cargaBack.getVersao()).isEqualTo(versao);

        versao.setCargas(new HashSet<>());
        assertThat(versao.getCargas()).doesNotContain(cargaBack);
        assertThat(cargaBack.getVersao()).isNull();
    }
}
