package org.semprotdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DBConfigTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static DBConfig getDBConfigSample1() {
        return new DBConfig().id(1L).key("key1").vstring("vstring1").vint(1);
    }

    public static DBConfig getDBConfigSample2() {
        return new DBConfig().id(2L).key("key2").vstring("vstring2").vint(2);
    }

    public static DBConfig getDBConfigRandomSampleGenerator() {
        return new DBConfig()
            .id(longCount.incrementAndGet())
            .key(UUID.randomUUID().toString())
            .vstring(UUID.randomUUID().toString())
            .vint(intCount.incrementAndGet());
    }
}
