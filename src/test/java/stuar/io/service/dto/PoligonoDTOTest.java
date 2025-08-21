package stuar.io.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class PoligonoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PoligonoDTO.class);
        PoligonoDTO poligonoDTO1 = new PoligonoDTO();
        poligonoDTO1.setId(1L);
        PoligonoDTO poligonoDTO2 = new PoligonoDTO();
        assertThat(poligonoDTO1).isNotEqualTo(poligonoDTO2);
        poligonoDTO2.setId(poligonoDTO1.getId());
        assertThat(poligonoDTO1).isEqualTo(poligonoDTO2);
        poligonoDTO2.setId(2L);
        assertThat(poligonoDTO1).isNotEqualTo(poligonoDTO2);
        poligonoDTO1.setId(null);
        assertThat(poligonoDTO1).isNotEqualTo(poligonoDTO2);
    }
}
