package stuar.io.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class ZonaPuntoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ZonaPuntoDTO.class);
        ZonaPuntoDTO zonaPuntoDTO1 = new ZonaPuntoDTO();
        zonaPuntoDTO1.setId(1L);
        ZonaPuntoDTO zonaPuntoDTO2 = new ZonaPuntoDTO();
        assertThat(zonaPuntoDTO1).isNotEqualTo(zonaPuntoDTO2);
        zonaPuntoDTO2.setId(zonaPuntoDTO1.getId());
        assertThat(zonaPuntoDTO1).isEqualTo(zonaPuntoDTO2);
        zonaPuntoDTO2.setId(2L);
        assertThat(zonaPuntoDTO1).isNotEqualTo(zonaPuntoDTO2);
        zonaPuntoDTO1.setId(null);
        assertThat(zonaPuntoDTO1).isNotEqualTo(zonaPuntoDTO2);
    }
}
