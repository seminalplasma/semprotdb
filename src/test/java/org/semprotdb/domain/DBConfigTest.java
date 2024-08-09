package org.semprotdb.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semprotdb.domain.DBConfigTestSamples.*;

import org.junit.jupiter.api.Test;
import org.semprotdb.web.rest.TestUtil;

class DBConfigTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DBConfig.class);
        DBConfig dBConfig1 = getDBConfigSample1();
        DBConfig dBConfig2 = new DBConfig();
        assertThat(dBConfig1).isNotEqualTo(dBConfig2);

        dBConfig2.setId(dBConfig1.getId());
        assertThat(dBConfig1).isEqualTo(dBConfig2);

        dBConfig2 = getDBConfigSample2();
        assertThat(dBConfig1).isNotEqualTo(dBConfig2);
    }
}
