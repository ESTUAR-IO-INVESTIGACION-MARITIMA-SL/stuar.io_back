package stuar.io.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class PoligonoPuntoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PoligonoPuntoDTO.class);
        PoligonoPuntoDTO poligonoPuntoDTO1 = new PoligonoPuntoDTO();
        poligonoPuntoDTO1.setId(1L);
        PoligonoPuntoDTO poligonoPuntoDTO2 = new PoligonoPuntoDTO();
        assertThat(poligonoPuntoDTO1).isNotEqualTo(poligonoPuntoDTO2);
        poligonoPuntoDTO2.setId(poligonoPuntoDTO1.getId());
        assertThat(poligonoPuntoDTO1).isEqualTo(poligonoPuntoDTO2);
        poligonoPuntoDTO2.setId(2L);
        assertThat(poligonoPuntoDTO1).isNotEqualTo(poligonoPuntoDTO2);
        poligonoPuntoDTO1.setId(null);
        assertThat(poligonoPuntoDTO1).isNotEqualTo(poligonoPuntoDTO2);
    }
}
