package org.semprotdb.repository;

import java.util.List;
import org.semprotdb.domain.Versao;
import org.semprotdb.domain.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Versao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VersaoRepository extends JpaRepository<Versao, Long>, JpaSpecificationExecutor<Versao>, VersaoDTORepository {
    List<Versao> findAllByStatusIn(List<Status> status);
}
