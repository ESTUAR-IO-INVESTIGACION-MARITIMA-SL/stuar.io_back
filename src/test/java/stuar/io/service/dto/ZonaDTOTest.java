package stuar.io.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class ZonaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ZonaDTO.class);
        ZonaDTO zonaDTO1 = new ZonaDTO();
        zonaDTO1.setId(1L);
        ZonaDTO zonaDTO2 = new ZonaDTO();
        assertThat(zonaDTO1).isNotEqualTo(zonaDTO2);
        zonaDTO2.setId(zonaDTO1.getId());
        assertThat(zonaDTO1).isEqualTo(zonaDTO2);
        zonaDTO2.setId(2L);
        assertThat(zonaDTO1).isNotEqualTo(zonaDTO2);
        zonaDTO1.setId(null);
        assertThat(zonaDTO1).isNotEqualTo(zonaDTO2);
    }
}
