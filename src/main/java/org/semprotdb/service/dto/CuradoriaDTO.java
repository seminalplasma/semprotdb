package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.semprotdb.domain.Curadoria;
import org.semprotdb.domain.Gene;
import org.semprotdb.domain.Proteina;

public class CuradoriaDTO extends Curadoria implements IDTO<CuradoriaDTO> {

    @JsonIgnore
    private final Set<Proteina> proteinas = new HashSet<>();

    @JsonIgnore
    private final Set<Gene> genes = new HashSet<>();

    @JsonIgnore
    private String email;

    @JsonIgnore
    private Instant data;

    @JsonIgnore
    private String anotacoes;

    public CuradoriaDTO(Long id) {
        setId(id);
    }

    @Override
    public Path[] getConstructorArgsPath(Root<CuradoriaDTO> root) {
        return new Path[] { root.get("id") };
    }
}
