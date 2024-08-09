package org.semprotdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GeneTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Gene getGeneSample1() {
        return new Gene().id(1L).nome("nome1").descricao("descricao1");
    }

    public static Gene getGeneSample2() {
        return new Gene().id(2L).nome("nome2").descricao("descricao2");
    }

    public static Gene getGeneRandomSampleGenerator() {
        return new Gene().id(longCount.incrementAndGet()).nome(UUID.randomUUID().toString()).descricao(UUID.randomUUID().toString());
    }
}
