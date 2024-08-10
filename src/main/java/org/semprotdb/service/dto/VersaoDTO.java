package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.Proteina;
import org.semprotdb.domain.Versao;
import org.semprotdb.domain.enumeration.Status;

public class VersaoDTO extends Versao {

    @JsonIgnore
    private String detalhes;

    @JsonIgnore
    private String label;

    @JsonIgnore
    private String logo;

    @JsonIgnore
    private String log;

    @JsonIgnore
    private String texto;

    @JsonIgnore
    private byte[] imagem;

    @JsonIgnore
    private String imagemContentType;

    @JsonIgnore
    private final Set<Proteina> proteinas = new HashSet<>();

    @JsonIgnore
    private final Set<Carga> cargas = new HashSet<>();

    public static final class VersaoDTOmin extends VersaoDTO {

        @JsonIgnore
        private Integer numero;

        @JsonIgnore
        private Status status;

        @JsonIgnore
        private Instant release;

        public VersaoDTOmin(Long id, String nome) {
            super(id, nome, null, null, null);
        }
    }

    public VersaoDTO(Long id, String nome, Integer numero, Status status, Instant release) {
        setId(id);
        setNome(nome);
        setNumero(numero);
        setStatus(status);
        setRelease(release);
    }
}
