package stuar.io.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static stuar.io.domain.PoligonoTestSamples.*;
import static stuar.io.domain.ZonaPuntoTestSamples.*;
import static stuar.io.domain.ZonaTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class ZonaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Zona.class);
        Zona zona1 = getZonaSample1();
        Zona zona2 = new Zona();
        assertThat(zona1).isNotEqualTo(zona2);

        zona2.setId(zona1.getId());
        assertThat(zona1).isEqualTo(zona2);

        zona2 = getZonaSample2();
        assertThat(zona1).isNotEqualTo(zona2);
    }

    @Test
    void poligonosTest() {
        Zona zona = getZonaRandomSampleGenerator();
        Poligono poligonoBack = getPoligonoRandomSampleGenerator();

        zona.addPoligonos(poligonoBack);
        assertThat(zona.getPoligonos()).containsOnly(poligonoBack);
        assertThat(poligonoBack.getZona()).isEqualTo(zona);

        zona.removePoligonos(poligonoBack);
        assertThat(zona.getPoligonos()).doesNotContain(poligonoBack);
        assertThat(poligonoBack.getZona()).isNull();

        zona.poligonos(new HashSet<>(Set.of(poligonoBack)));
        assertThat(zona.getPoligonos()).containsOnly(poligonoBack);
        assertThat(poligonoBack.getZona()).isEqualTo(zona);

        zona.setPoligonos(new HashSet<>());
        assertThat(zona.getPoligonos()).doesNotContain(poligonoBack);
        assertThat(poligonoBack.getZona()).isNull();
    }

    @Test
    void pointsTest() {
        Zona zona = getZonaRandomSampleGenerator();
        ZonaPunto zonaPuntoBack = getZonaPuntoRandomSampleGenerator();

        zona.addPoints(zonaPuntoBack);
        assertThat(zona.getPoints()).containsOnly(zonaPuntoBack);
        assertThat(zonaPuntoBack.getZona()).isEqualTo(zona);

        zona.removePoints(zonaPuntoBack);
        assertThat(zona.getPoints()).doesNotContain(zonaPuntoBack);
        assertThat(zonaPuntoBack.getZona()).isNull();

        zona.points(new HashSet<>(Set.of(zonaPuntoBack)));
        assertThat(zona.getPoints()).containsOnly(zonaPuntoBack);
        assertThat(zonaPuntoBack.getZona()).isEqualTo(zona);

        zona.setPoints(new HashSet<>());
        assertThat(zona.getPoints()).doesNotContain(zonaPuntoBack);
        assertThat(zonaPuntoBack.getZona()).isNull();
    }
}
