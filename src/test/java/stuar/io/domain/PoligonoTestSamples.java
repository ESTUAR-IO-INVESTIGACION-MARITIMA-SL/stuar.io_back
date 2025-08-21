package stuar.io.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PoligonoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Poligono getPoligonoSample1() {
        return new Poligono().id(1L).name("name1").description("description1");
    }

    public static Poligono getPoligonoSample2() {
        return new Poligono().id(2L).name("name2").description("description2");
    }

    public static Poligono getPoligonoRandomSampleGenerator() {
        return new Poligono().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
