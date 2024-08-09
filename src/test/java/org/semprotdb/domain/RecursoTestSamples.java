package org.semprotdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RecursoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Recurso getRecursoSample1() {
        return new Recurso().id(1L).uid("uid1").link("link1");
    }

    public static Recurso getRecursoSample2() {
        return new Recurso().id(2L).uid("uid2").link("link2");
    }

    public static Recurso getRecursoRandomSampleGenerator() {
        return new Recurso().id(longCount.incrementAndGet()).uid(UUID.randomUUID().toString()).link(UUID.randomUUID().toString());
    }
}
