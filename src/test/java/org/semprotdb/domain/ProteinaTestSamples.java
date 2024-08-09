package org.semprotdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProteinaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Proteina getProteinaSample1() {
        return new Proteina().id(1L).nome("nome1").tamanho(1).massa("massa1").descricao("descricao1");
    }

    public static Proteina getProteinaSample2() {
        return new Proteina().id(2L).nome("nome2").tamanho(2).massa("massa2").descricao("descricao2");
    }

    public static Proteina getProteinaRandomSampleGenerator() {
        return new Proteina()
            .id(longCount.incrementAndGet())
            .nome(UUID.randomUUID().toString())
            .tamanho(intCount.incrementAndGet())
            .massa(UUID.randomUUID().toString())
            .descricao(UUID.randomUUID().toString());
    }
}
