package org.semprotdb.repository;

import org.semprotdb.domain.DBConfig;
import org.semprotdb.service.dto.DBConfigDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DBConfigDTORepository {
    Page<DBConfigDTO> findAllDTO(Pageable pageable);

    Page<DBConfig> findAllLight(Pageable pageable);
}
