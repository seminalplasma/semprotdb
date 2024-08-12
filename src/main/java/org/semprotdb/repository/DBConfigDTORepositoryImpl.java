package org.semprotdb.repository;

import org.semprotdb.domain.DBConfig;
import org.semprotdb.service.dto.DBConfigDTO;
import org.semprotdb.util.LigthDTORepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class DBConfigDTORepositoryImpl extends LigthDTORepository<DBConfig, DBConfigDTO> implements DBConfigDTORepository {

    public DBConfigDTORepositoryImpl() {
        super(DBConfig.class, new DBConfigDTO());
    }

    @Override
    public Page<DBConfigDTO> findAllDTO(Pageable pageable) {
        return project_paginateDTO(pageable);
    }

    @Override
    public Page<DBConfig> findAllLight(Pageable pageable) {
        return findAllDTO(pageable).map(d -> d);
    }
}
