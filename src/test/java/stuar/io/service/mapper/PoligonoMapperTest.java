package stuar.io.service.mapper;

import static stuar.io.domain.PoligonoAsserts.*;
import static stuar.io.domain.PoligonoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PoligonoMapperTest {

    private PoligonoMapper poligonoMapper;

    @BeforeEach
    void setUp() {
        poligonoMapper = new PoligonoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPoligonoSample1();
        var actual = poligonoMapper.toEntity(poligonoMapper.toDto(expected));
        assertPoligonoAllPropertiesEquals(expected, actual);
    }
}
