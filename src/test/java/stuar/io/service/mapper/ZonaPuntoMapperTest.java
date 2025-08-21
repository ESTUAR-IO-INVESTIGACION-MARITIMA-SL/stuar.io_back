package stuar.io.service.mapper;

import static stuar.io.domain.ZonaPuntoAsserts.*;
import static stuar.io.domain.ZonaPuntoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZonaPuntoMapperTest {

    private ZonaPuntoMapper zonaPuntoMapper;

    @BeforeEach
    void setUp() {
        zonaPuntoMapper = new ZonaPuntoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getZonaPuntoSample1();
        var actual = zonaPuntoMapper.toEntity(zonaPuntoMapper.toDto(expected));
        assertZonaPuntoAllPropertiesEquals(expected, actual);
    }
}
