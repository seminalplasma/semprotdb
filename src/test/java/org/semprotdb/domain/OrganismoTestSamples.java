package org.semprotdb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrganismoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Organismo getOrganismoSample1() {
        return new Organismo()
            .id(1L)
            .nome("nome1")
            .sigla("sigla1")
            .apelido("apelido1")
            .icone("icone1")
            .pos("pos1")
            .imagem("imagem1")
            .descricao("descricao1");
    }

    public static Organismo getOrganismoSample2() {
        return new Organismo()
            .id(2L)
            .nome("nome2")
            .sigla("sigla2")
            .apelido("apelido2")
            .icone("icone2")
            .pos("pos2")
            .imagem("imagem2")
            .descricao("descricao2");
    }

    public static Organismo getOrganismoRandomSampleGenerator() {
        return new Organismo()
            .id(longCount.incrementAndGet())
            .nome(UUID.randomUUID().toString())
            .sigla(UUID.randomUUID().toString())
            .apelido(UUID.randomUUID().toString())
            .icone(UUID.randomUUID().toString())
            .pos(UUID.randomUUID().toString())
            .imagem(UUID.randomUUID().toString())
            .descricao(UUID.randomUUID().toString());
    }
}
