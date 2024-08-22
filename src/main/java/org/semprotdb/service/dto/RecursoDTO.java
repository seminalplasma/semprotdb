package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.HashSet;
import java.util.Set;
import org.semprotdb.domain.Proteina;
import org.semprotdb.domain.Recurso;
import org.semprotdb.domain.enumeration.BioDB;

public class RecursoDTO extends Recurso implements IDTO<RecursoDTO> {

    @JsonIgnore
    private final Set<Proteina> proteinas = new HashSet<>();

    public RecursoDTO() {}

    public RecursoDTO(Long id, String uid, BioDB db, String link) {
        setId(id);
        setUid(uid);
        setDb(db);
        setLink(link);
    }

    @Override
    public Path[] getConstructorArgsPath(Root<RecursoDTO> root) {
        return new Path[] { root.get("id"), root.get("uid"), root.get("db"), root.get("link") };
    }
}
