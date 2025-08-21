package stuar.io.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import stuar.io.web.rest.TestUtil;

class BateaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BateaDTO.class);
        BateaDTO bateaDTO1 = new BateaDTO();
        bateaDTO1.setId(1L);
        BateaDTO bateaDTO2 = new BateaDTO();
        assertThat(bateaDTO1).isNotEqualTo(bateaDTO2);
        bateaDTO2.setId(bateaDTO1.getId());
        assertThat(bateaDTO1).isEqualTo(bateaDTO2);
        bateaDTO2.setId(2L);
        assertThat(bateaDTO1).isNotEqualTo(bateaDTO2);
        bateaDTO1.setId(null);
        assertThat(bateaDTO1).isNotEqualTo(bateaDTO2);
    }
}
