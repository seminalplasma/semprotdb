package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.HashSet;
import java.util.Set;
import org.semprotdb.domain.Gene;
import org.semprotdb.domain.Organismo;

public class OrganismoDTO extends Organismo implements IDTO<OrganismoDTO> {

    @JsonIgnore
    private final Set<Gene> genes = new HashSet<>();

    @JsonIgnore
    private String nome;

    @JsonIgnore
    private byte[] silhueta;

    @JsonIgnore
    private String silhuetaContentType;

    @JsonIgnore
    private String icone;

    @JsonIgnore
    private String pos;

    @JsonIgnore
    private String imagem;

    @JsonIgnore
    private String descricao;

    public OrganismoDTO(Long id, String apelido, String sigla) {
        setId(id);
        setApelido(apelido);
        setSigla(sigla);
    }

    public OrganismoDTO() {}

    @Override
    public Path[] getConstructorArgsPath(Root<OrganismoDTO> root) {
        return new Path[] { root.get("id"), root.get("apelido"), root.get("sigla") };
    }
}
