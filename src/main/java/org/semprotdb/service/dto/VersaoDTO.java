package org.semprotdb.service.dto;

import java.time.Instant;
import org.semprotdb.domain.Versao;
import org.semprotdb.domain.enumeration.Status;

public class VersaoDTO extends Versao {

    public VersaoDTO(Long id, String nome, Integer numero, Status status, Instant release) {
        setId(id);
        setNome(nome);
        setNumero(numero);
        setStatus(status);
        setRelease(release);
    }
}
