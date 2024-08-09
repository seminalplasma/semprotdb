package org.semprotdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CargaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Carga getCargaSample1() {
        return new Carga().id(1L).status("status1").ordem(1).nome("nome1").caminho("caminho1").linhas(1).checksum("checksum1");
    }

    public static Carga getCargaSample2() {
        return new Carga().id(2L).status("status2").ordem(2).nome("nome2").caminho("caminho2").linhas(2).checksum("checksum2");
    }

    public static Carga getCargaRandomSampleGenerator() {
        return new Carga()
            .id(longCount.incrementAndGet())
            .status(UUID.randomUUID().toString())
            .ordem(intCount.incrementAndGet())
            .nome(UUID.randomUUID().toString())
            .caminho(UUID.randomUUID().toString())
            .linhas(intCount.incrementAndGet())
            .checksum(UUID.randomUUID().toString());
    }
}
