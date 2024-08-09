package org.semprotdb.repository;

import org.semprotdb.domain.DBConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DBConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DBConfigRepository extends JpaRepository<DBConfig, Long> {}
