package org.semprotdb.repository;

import java.util.List;
import java.util.Optional;
import org.semprotdb.domain.Proteina;
import org.springframework.data.domain.Page;

public interface ProteinaRepositoryWithBagRelationships {
    Optional<Proteina> fetchBagRelationships(Optional<Proteina> proteina);

    List<Proteina> fetchBagRelationships(List<Proteina> proteinas);

    Page<Proteina> fetchBagRelationships(Page<Proteina> proteinas);
}
