package stuar.io.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static stuar.io.domain.ZonaPuntoTestSamples.*;
import static stuar.io.domain.ZonaTestSamples.*;

import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class ZonaPuntoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ZonaPunto.class);
        ZonaPunto zonaPunto1 = getZonaPuntoSample1();
        ZonaPunto zonaPunto2 = new ZonaPunto();
        assertThat(zonaPunto1).isNotEqualTo(zonaPunto2);

        zonaPunto2.setId(zonaPunto1.getId());
        assertThat(zonaPunto1).isEqualTo(zonaPunto2);

        zonaPunto2 = getZonaPuntoSample2();
        assertThat(zonaPunto1).isNotEqualTo(zonaPunto2);
    }

    @Test
    void zonaTest() {
        ZonaPunto zonaPunto = getZonaPuntoRandomSampleGenerator();
        Zona zonaBack = getZonaRandomSampleGenerator();

        zonaPunto.setZona(zonaBack);
        assertThat(zonaPunto.getZona()).isEqualTo(zonaBack);

        zonaPunto.zona(null);
        assertThat(zonaPunto.getZona()).isNull();
    }
}
