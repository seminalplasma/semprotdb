package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.Proteina;
import org.semprotdb.domain.Versao;
import org.semprotdb.domain.enumeration.Status;

public class VersaoDTO extends Versao implements IDTO<VersaoDTO> {

    @JsonIgnore
    private final Set<Proteina> proteinas = new HashSet<>();

    @JsonIgnore
    private final Set<Carga> cargas = new HashSet<>();

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

    public VersaoDTO() {}

    public VersaoDTO(Long id, String nome, Integer numero, Status status, Instant release) {
        setId(id);
        setNome(nome);
        setNumero(numero);
        setStatus(status);
        setRelease(release);
    }

    @Override
    public Path[] getConstructorArgsPath(Root<VersaoDTO> root) {
        return new Path[] { root.get("id"), root.get("nome"), root.get("numero"), root.get("status"), root.get("release") };
    }

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
}
