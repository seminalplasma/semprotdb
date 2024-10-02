package org.semprotdb.repository;

import java.util.List;
import org.semprotdb.domain.Versao;
import org.semprotdb.domain.enumeration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Versao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VersaoRepository extends JpaRepository<Versao, Long>, JpaSpecificationExecutor<Versao>, VersaoDTORepository {
    List<Versao> findAllByStatusIn(List<Status> status);

    @Modifying
    @Query(
        value = "delete from rel_proteina__referencia " + "where proteina_id in (select id from proteina where versao_id = :versaoId)",
        nativeQuery = true
    )
    void removerVersao1(Long versaoId);

    @Modifying
    @Query(
        value = "delete from referencia where id in " +
        "(select id from referencia " +
        "left join rel_proteina__referencia on id=referencia_id where referencia_id is null)",
        nativeQuery = true
    )
    void removerVersao2();

    @Modifying
    @Query(
        value = "delete from rel_proteina__recurso where proteina_id in " + "(select id from proteina where versao_id = :versaoId)",
        nativeQuery = true
    )
    void removerVersao3(Long versaoId);

    @Modifying
    @Query(
        value = "delete from recurso where id in " +
        "(select id from recurso " +
        "left join rel_proteina__recurso on id=recurso_id where recurso_id is null)",
        nativeQuery = true
    )
    void removerVersao4(Long versaoId);

    @Modifying
    @Query(value = "delete from proteina where versao_id = :versaoId", nativeQuery = true)
    void removerVersao5(Long versaoId);

    @Modifying
    @Query(
        value = "delete from gene where id in " +
        "(select g.id from gene g " +
        "left join proteina p on g.id=p.gene_id where p.gene_id is null)",
        nativeQuery = true
    )
    void removerVersao6();

    @Modifying
    @Query(value = "delete from carga where versao_id = :versaoId;", nativeQuery = true)
    void removerVersao7(Long versaoId);

    @Modifying
    @Query(value = "delete from versao where id = :versaoId;", nativeQuery = true)
    void removerVersao8(Long versaoId);
}
