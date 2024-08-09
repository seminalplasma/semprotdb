package org.semprotdb.repository;

import java.util.List;
import java.util.Optional;
import org.semprotdb.domain.Proteina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Proteina entity.
 *
 * When extending this class, extend ProteinaRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ProteinaRepository
    extends ProteinaRepositoryWithBagRelationships, JpaRepository<Proteina, Long>, JpaSpecificationExecutor<Proteina> {
    default Optional<Proteina> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Proteina> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Proteina> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
