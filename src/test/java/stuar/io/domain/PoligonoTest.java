package stuar.io.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static stuar.io.domain.BateaTestSamples.*;
import static stuar.io.domain.PoligonoPuntoTestSamples.*;
import static stuar.io.domain.PoligonoTestSamples.*;
import static stuar.io.domain.ZonaTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class PoligonoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Poligono.class);
        Poligono poligono1 = getPoligonoSample1();
        Poligono poligono2 = new Poligono();
        assertThat(poligono1).isNotEqualTo(poligono2);

        poligono2.setId(poligono1.getId());
        assertThat(poligono1).isEqualTo(poligono2);

        poligono2 = getPoligonoSample2();
        assertThat(poligono1).isNotEqualTo(poligono2);
    }

    @Test
    void bateasTest() {
        Poligono poligono = getPoligonoRandomSampleGenerator();
        Batea bateaBack = getBateaRandomSampleGenerator();

        poligono.addBateas(bateaBack);
        assertThat(poligono.getBateas()).containsOnly(bateaBack);
        assertThat(bateaBack.getPoligono()).isEqualTo(poligono);

        poligono.removeBateas(bateaBack);
        assertThat(poligono.getBateas()).doesNotContain(bateaBack);
        assertThat(bateaBack.getPoligono()).isNull();

        poligono.bateas(new HashSet<>(Set.of(bateaBack)));
        assertThat(poligono.getBateas()).containsOnly(bateaBack);
        assertThat(bateaBack.getPoligono()).isEqualTo(poligono);

        poligono.setBateas(new HashSet<>());
        assertThat(poligono.getBateas()).doesNotContain(bateaBack);
        assertThat(bateaBack.getPoligono()).isNull();
    }

    @Test
    void pointsTest() {
        Poligono poligono = getPoligonoRandomSampleGenerator();
        PoligonoPunto poligonoPuntoBack = getPoligonoPuntoRandomSampleGenerator();

        poligono.addPoints(poligonoPuntoBack);
        assertThat(poligono.getPoints()).containsOnly(poligonoPuntoBack);
        assertThat(poligonoPuntoBack.getPoligono()).isEqualTo(poligono);

        poligono.removePoints(poligonoPuntoBack);
        assertThat(poligono.getPoints()).doesNotContain(poligonoPuntoBack);
        assertThat(poligonoPuntoBack.getPoligono()).isNull();

        poligono.points(new HashSet<>(Set.of(poligonoPuntoBack)));
        assertThat(poligono.getPoints()).containsOnly(poligonoPuntoBack);
        assertThat(poligonoPuntoBack.getPoligono()).isEqualTo(poligono);

        poligono.setPoints(new HashSet<>());
        assertThat(poligono.getPoints()).doesNotContain(poligonoPuntoBack);
        assertThat(poligonoPuntoBack.getPoligono()).isNull();
    }

    @Test
    void zonaTest() {
        Poligono poligono = getPoligonoRandomSampleGenerator();
        Zona zonaBack = getZonaRandomSampleGenerator();

        poligono.setZona(zonaBack);
        assertThat(poligono.getZona()).isEqualTo(zonaBack);

        poligono.zona(null);
        assertThat(poligono.getZona()).isNull();
    }
}
