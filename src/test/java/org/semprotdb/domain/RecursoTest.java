package org.semprotdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semprotdb.domain.ProteinaTestSamples.*;
import static org.semprotdb.domain.RecursoTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.semprotdb.web.rest.TestUtil;

class RecursoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Recurso.class);
        Recurso recurso1 = getRecursoSample1();
        Recurso recurso2 = new Recurso();
        assertThat(recurso1).isNotEqualTo(recurso2);

        recurso2.setId(recurso1.getId());
        assertThat(recurso1).isEqualTo(recurso2);

        recurso2 = getRecursoSample2();
        assertThat(recurso1).isNotEqualTo(recurso2);
    }

    @Test
    void proteinaTest() {
        Recurso recurso = getRecursoRandomSampleGenerator();
        Proteina proteinaBack = getProteinaRandomSampleGenerator();

        recurso.addProteina(proteinaBack);
        assertThat(recurso.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getRecursos()).containsOnly(recurso);

        recurso.removeProteina(proteinaBack);
        assertThat(recurso.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getRecursos()).doesNotContain(recurso);

        recurso.proteinas(new HashSet<>(Set.of(proteinaBack)));
        assertThat(recurso.getProteinas()).containsOnly(proteinaBack);
        assertThat(proteinaBack.getRecursos()).containsOnly(recurso);

        recurso.setProteinas(new HashSet<>());
        assertThat(recurso.getProteinas()).doesNotContain(proteinaBack);
        assertThat(proteinaBack.getRecursos()).doesNotContain(recurso);
    }
}
