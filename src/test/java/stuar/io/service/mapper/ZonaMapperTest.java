package stuar.io.service.mapper;

import static stuar.io.domain.ZonaAsserts.*;
import static stuar.io.domain.ZonaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZonaMapperTest {

    private ZonaMapper zonaMapper;

    @BeforeEach
    void setUp() {
        zonaMapper = new ZonaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getZonaSample1();
        var actual = zonaMapper.toEntity(zonaMapper.toDto(expected));
        assertZonaAllPropertiesEquals(expected, actual);
    }
}
