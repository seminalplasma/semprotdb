package org.semprotdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ReferenciaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Referencia getReferenciaSample1() {
        return new Referencia().id(1L).citacao("citacao1").link("link1").ano(1).autores("autores1");
    }

    public static Referencia getReferenciaSample2() {
        return new Referencia().id(2L).citacao("citacao2").link("link2").ano(2).autores("autores2");
    }

    public static Referencia getReferenciaRandomSampleGenerator() {
        return new Referencia()
            .id(longCount.incrementAndGet())
            .citacao(UUID.randomUUID().toString())
            .link(UUID.randomUUID().toString())
            .ano(intCount.incrementAndGet())
            .autores(UUID.randomUUID().toString());
    }
}
