package org.semprotdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CuradoriaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Curadoria getCuradoriaSample1() {
        return new Curadoria().id(1L).email("email1").anotacoes("anotacoes1");
    }

    public static Curadoria getCuradoriaSample2() {
        return new Curadoria().id(2L).email("email2").anotacoes("anotacoes2");
    }

    public static Curadoria getCuradoriaRandomSampleGenerator() {
        return new Curadoria().id(longCount.incrementAndGet()).email(UUID.randomUUID().toString()).anotacoes(UUID.randomUUID().toString());
    }
}
