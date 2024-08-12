package org.semprotdb.repository;

import java.util.List;
import java.util.Optional;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Carga entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CargaRepository extends JpaRepository<Carga, Long>, JpaSpecificationExecutor<Carga>, CargaDTORepository {
    Page<Carga> findAllByVersaoStatusIsInAndDestino(List<Status> status, Destino destino, Pageable page);

    Optional<Carga> findOneByIdAndVersaoStatusIsInAndDestino(Long id, List<Status> status, Destino destino);
}
