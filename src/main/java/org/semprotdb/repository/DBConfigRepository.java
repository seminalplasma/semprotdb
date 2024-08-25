package org.semprotdb.repository;

import java.util.List;
import java.util.Optional;
import org.semprotdb.domain.DBConfig;
import org.semprotdb.service.dto.DBConfigDTO;
import org.semprotdb.service.dto.FeedbackDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DBConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DBConfigRepository extends JpaRepository<DBConfig, Long>, DBConfigDTORepository {
    Optional<DBConfig> findDBConfigByKey(String key);

    List<FeedbackDTO> findFirst100ByHabilitadoIsTrueAndAndKeyIsOrderByVdateDesc(String key);
}
