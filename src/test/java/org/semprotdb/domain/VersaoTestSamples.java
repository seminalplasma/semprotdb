package org.semprotdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VersaoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Versao getVersaoSample1() {
        return new Versao().id(1L).nome("nome1").detalhes("detalhes1").label("label1").numero(1).logo("logo1");
    }

    public static Versao getVersaoSample2() {
        return new Versao().id(2L).nome("nome2").detalhes("detalhes2").label("label2").numero(2).logo("logo2");
    }

    public static Versao getVersaoRandomSampleGenerator() {
        return new Versao()
            .id(longCount.incrementAndGet())
            .nome(UUID.randomUUID().toString())
            .detalhes(UUID.randomUUID().toString())
            .label(UUID.randomUUID().toString())
            .numero(intCount.incrementAndGet())
            .logo(UUID.randomUUID().toString());
    }
}
