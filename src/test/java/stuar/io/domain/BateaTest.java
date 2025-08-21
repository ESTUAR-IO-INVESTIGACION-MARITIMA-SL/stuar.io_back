package stuar.io.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static stuar.io.domain.BateaTestSamples.*;
import static stuar.io.domain.PoligonoTestSamples.*;

import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class BateaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Batea.class);
        Batea batea1 = getBateaSample1();
        Batea batea2 = new Batea();
        assertThat(batea1).isNotEqualTo(batea2);

        batea2.setId(batea1.getId());
        assertThat(batea1).isEqualTo(batea2);

        batea2 = getBateaSample2();
        assertThat(batea1).isNotEqualTo(batea2);
    }

    @Test
    void poligonoTest() {
        Batea batea = getBateaRandomSampleGenerator();
        Poligono poligonoBack = getPoligonoRandomSampleGenerator();

        batea.setPoligono(poligonoBack);
        assertThat(batea.getPoligono()).isEqualTo(poligonoBack);

        batea.poligono(null);
        assertThat(batea.getPoligono()).isNull();
    }
}
