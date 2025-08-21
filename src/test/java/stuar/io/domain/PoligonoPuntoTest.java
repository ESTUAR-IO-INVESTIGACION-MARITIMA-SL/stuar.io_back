package stuar.io.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static stuar.io.domain.PoligonoPuntoTestSamples.*;
import static stuar.io.domain.PoligonoTestSamples.*;

import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class PoligonoPuntoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PoligonoPunto.class);
        PoligonoPunto poligonoPunto1 = getPoligonoPuntoSample1();
        PoligonoPunto poligonoPunto2 = new PoligonoPunto();
        assertThat(poligonoPunto1).isNotEqualTo(poligonoPunto2);

        poligonoPunto2.setId(poligonoPunto1.getId());
        assertThat(poligonoPunto1).isEqualTo(poligonoPunto2);

        poligonoPunto2 = getPoligonoPuntoSample2();
        assertThat(poligonoPunto1).isNotEqualTo(poligonoPunto2);
    }

    @Test
    void poligonoTest() {
        PoligonoPunto poligonoPunto = getPoligonoPuntoRandomSampleGenerator();
        Poligono poligonoBack = getPoligonoRandomSampleGenerator();

        poligonoPunto.setPoligono(poligonoBack);
        assertThat(poligonoPunto.getPoligono()).isEqualTo(poligonoBack);

        poligonoPunto.poligono(null);
        assertThat(poligonoPunto.getPoligono()).isNull();
    }
}
