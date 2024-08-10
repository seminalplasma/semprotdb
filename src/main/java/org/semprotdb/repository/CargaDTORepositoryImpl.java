package org.semprotdb.repository;

import java.util.HashMap;
import org.semprotdb.domain.Carga;
import org.semprotdb.service.dto.CargaDTO;
import org.semprotdb.service.dto.VersaoDTO;
import org.semprotdb.util.LigthDTORepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class CargaDTORepositoryImpl extends LigthDTORepository<Carga, CargaDTO> implements CargaDTORepository {

    public CargaDTORepositoryImpl() {
        super(Carga.class, CargaDTO.class);
    }

    @Override
    public HashMap<String, Class> getJoins() {
        HashMap<String, Class> filhos = new HashMap<>();
        filhos.put("versao", VersaoDTO.VersaoDTOmin.class);
        return filhos;
    }

    @Override
    public Page<CargaDTO> findAllDTO(Specification<Carga> specification, Pageable pageable) {
        return project_filter_paginateDTO(specification, pageable);
    }

    @Override
    public Page<Carga> findAllLight(Specification<Carga> specification, Pageable pageable) {
        return findAllDTO(specification, pageable).map(c -> c);
    }
}
