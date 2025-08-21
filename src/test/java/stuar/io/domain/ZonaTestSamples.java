package stuar.io.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ZonaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Zona getZonaSample1() {
        return new Zona().id(1L).name("name1").description("description1");
    }

    public static Zona getZonaSample2() {
        return new Zona().id(2L).name("name2").description("description2");
    }

    public static Zona getZonaRandomSampleGenerator() {
        return new Zona().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
