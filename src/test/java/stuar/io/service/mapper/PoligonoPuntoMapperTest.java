package stuar.io.service.mapper;

import static stuar.io.domain.PoligonoPuntoAsserts.*;
import static stuar.io.domain.PoligonoPuntoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PoligonoPuntoMapperTest {

    private PoligonoPuntoMapper poligonoPuntoMapper;

    @BeforeEach
    void setUp() {
        poligonoPuntoMapper = new PoligonoPuntoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPoligonoPuntoSample1();
        var actual = poligonoPuntoMapper.toEntity(poligonoPuntoMapper.toDto(expected));
        assertPoligonoPuntoAllPropertiesEquals(expected, actual);
    }
}
