package stuar.io.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ZonaPuntoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ZonaPunto getZonaPuntoSample1() {
        return new ZonaPunto().id(1L).orderIndex(1);
    }

    public static ZonaPunto getZonaPuntoSample2() {
        return new ZonaPunto().id(2L).orderIndex(2);
    }

    public static ZonaPunto getZonaPuntoRandomSampleGenerator() {
        return new ZonaPunto().id(longCount.incrementAndGet()).orderIndex(intCount.incrementAndGet());
    }
}
