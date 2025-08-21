package stuar.io.service.mapper;

import static stuar.io.domain.BateaAsserts.*;
import static stuar.io.domain.BateaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BateaMapperTest {

    private BateaMapper bateaMapper;

    @BeforeEach
    void setUp() {
        bateaMapper = new BateaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBateaSample1();
        var actual = bateaMapper.toEntity(bateaMapper.toDto(expected));
        assertBateaAllPropertiesEquals(expected, actual);
    }
}
