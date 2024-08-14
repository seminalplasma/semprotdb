package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.HashSet;
import java.util.Set;
import org.semprotdb.domain.Gene;
import org.semprotdb.domain.Proteina;

public class GeneDTO extends Gene implements IDTO<GeneDTO> {

    public static final String[] rels = new String[] { "curadoria", "organismo" };

    @JsonIgnore
    private final Set<Proteina> proteinas = new HashSet<>();

    @JsonIgnore
    private String descricao;

    private CuradoriaDTO curadoria;
    private OrganismoDTO organismo;

    public GeneDTO(Long id, String nome, Long curadoriaID, Long organismoID, String organismoAPELIDO, String organismoSIGLA) {
        setId(id);
        setNome(nome);
        setCuradoria(new CuradoriaDTO(curadoriaID));
        setOrganismo(new OrganismoDTO(organismoID, organismoAPELIDO, organismoSIGLA));
    }

    @Override
    public Path[] getConstructorArgsPath(Root<GeneDTO> root) {
        Join curadoria = root.join("curadoria");
        Join organismo = root.join("organismo");
        return new Path[] {
            root.get("id"),
            root.get("nome"),
            curadoria.get("id"),
            organismo.get("id"),
            organismo.get("apelido"),
            organismo.get("sigla"),
        };
    }
}
