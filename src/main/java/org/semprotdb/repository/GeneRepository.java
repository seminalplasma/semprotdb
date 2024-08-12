package org.semprotdb.repository;

import java.util.List;
import org.semprotdb.domain.Gene;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Gene entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GeneRepository extends JpaRepository<Gene, Long>, JpaSpecificationExecutor<Gene> {
    List<Gene> findAllByProteinasIsEmpty();
}
