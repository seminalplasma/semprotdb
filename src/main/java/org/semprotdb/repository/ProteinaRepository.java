package org.semprotdb.repository;

import java.util.List;
import java.util.Optional;
import org.semprotdb.domain.Proteina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Proteina entity.
 * <p>
 * When extending this class, extend ProteinaRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ProteinaRepository
    extends
        ProteinaRepositoryWithBagRelationships, JpaRepository<Proteina, Long>, JpaSpecificationExecutor<Proteina>, ProteinaDTORepository {
    default Optional<Proteina> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Proteina> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Proteina> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllLight(Specification.where(null), pageable);
    }
}
