package org.semprotdb.repository;

import java.util.List;
import org.semprotdb.domain.Organismo;
import org.semprotdb.service.dto.OrganismoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Organismo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganismoRepository extends JpaRepository<Organismo, Long>, JpaSpecificationExecutor<Organismo>, OrganismoDTORepository {
    List<Organismo> findAllByGenesIsEmpty();

    @Query(
        value = "SELECT DISTINCT " +
        "NEW org.semprotdb.service.dto.OrganismoDTO(o.id, o.apelido, o.sigla) " +
        "FROM Proteina p JOIN p.gene g JOIN p.versao v JOIN p.gene.organismo o " +
        "WHERE v.status = 'DISPONIVEL'",
        countQuery = "SELECT count(distinct o.id) " +
        "FROM Proteina p JOIN p.gene g JOIN p.versao v JOIN p.gene.organismo o " +
        "WHERE v.status = 'DISPONIVEL'"
    )
    Page<OrganismoDTO> findAllLightPublic(Pageable page);
}
