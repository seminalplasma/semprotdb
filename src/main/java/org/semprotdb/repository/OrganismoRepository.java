package org.semprotdb.repository;

import org.semprotdb.domain.Organismo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Organismo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganismoRepository extends JpaRepository<Organismo, Long>, JpaSpecificationExecutor<Organismo> {}
