package stuar.io.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BateaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Batea getBateaSample1() {
        return new Batea().id(1L).name("name1").description("description1").ownerName("ownerName1");
    }

    public static Batea getBateaSample2() {
        return new Batea().id(2L).name("name2").description("description2").ownerName("ownerName2");
    }

    public static Batea getBateaRandomSampleGenerator() {
        return new Batea()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .ownerName(UUID.randomUUID().toString());
    }
}
