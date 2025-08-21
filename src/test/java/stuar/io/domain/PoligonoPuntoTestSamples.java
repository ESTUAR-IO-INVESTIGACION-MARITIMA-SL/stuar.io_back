package stuar.io.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PoligonoPuntoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PoligonoPunto getPoligonoPuntoSample1() {
        return new PoligonoPunto().id(1L).orderIndex(1);
    }

    public static PoligonoPunto getPoligonoPuntoSample2() {
        return new PoligonoPunto().id(2L).orderIndex(2);
    }

    public static PoligonoPunto getPoligonoPuntoRandomSampleGenerator() {
        return new PoligonoPunto().id(longCount.incrementAndGet()).orderIndex(intCount.incrementAndGet());
    }
}
