package org.semprotdb.service;

import jakarta.persistence.criteria.JoinType;
import java.util.Arrays;
import java.util.List;
import org.semprotdb.domain.*;
import org.semprotdb.repository.ProteinaRepository;
import org.semprotdb.service.criteria.ProteinaCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Proteina} entities in the database.
 * The main input is a {@link ProteinaCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link Proteina} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProteinaQueryService extends QueryService<Proteina> {

    private static final Logger log = LoggerFactory.getLogger(ProteinaQueryService.class);

    private final ProteinaRepository proteinaRepository;

    public ProteinaQueryService(ProteinaRepository proteinaRepository) {
        this.proteinaRepository = proteinaRepository;
    }

    /**
     * Return a {@link Page} of {@link Proteina} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Proteina> findByCriteria(ProteinaCriteria criteria, Pageable page, String qfirst, String qors) {
        if (qfirst == null) return findByCriteria(criteria, page);
        log.debug("find by criteria LIGHT: {}, page: {} order: {}", criteria, page, qfirst);
        Specification<Proteina> spec = null;
        if (qfirst != null && qfirst.contains(",")) {
            for (String q : qfirst.split(",")) {
                spec = createSpecification(criteria, qors, spec, q);
            }
        } else {
            spec = createSpecification(criteria);
        }

        return proteinaRepository.fetchBagRelationships(proteinaRepository.findAllLight(spec, page));
    }

    @Transactional(readOnly = true)
    public Page<Proteina> findByCriteria(ProteinaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {} NO FILTER", criteria, page);
        Specification<Proteina> spec = createSpecification(criteria);
        return proteinaRepository.fetchBagRelationships(proteinaRepository.findAll(spec, page));
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProteinaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Proteina> specification = createSpecification(criteria);
        return proteinaRepository.count(specification);
    }

    /**
     * Function para pegar a primeira especificacao da pesquisa
     * importante pois caso nao definir a primeira algum OR pode ficar
     * de fora na createSpecification
     */
    //    protected Specification<Proteina> firstSpecification(ProteinaCriteria criteria, String qfirst) {
    //        ProteinaCriteria pc = new ProteinaCriteria();
    //        qfirst = qfirst == null ? "" : qfirst;
    //        if (qfirst != "") log.debug("Filter " + qfirst + " => {}", criteria);
    //        switch (qfirst) {
    //            case "id" -> {
    //                pc.setId(criteria.getId());
    //                criteria.setId(null);
    //            }
    //            case "nome" -> {
    //                pc.setNome(criteria.geneNome());
    //                criteria.setNome(null);
    //            }
    //            case "tamanho" -> {
    //                pc.setTamanho(criteria.getTamanho());
    //                criteria.setTamanho(null);
    //            }
    //            case "massa" -> {
    //                pc.setMassa(criteria.getMassa());
    //                criteria.setMassa(null);
    //            }
    //            case "descricao" -> {
    //                pc.setDescricao(criteria.getDescricao());
    //                criteria.setDescricao(null);
    //            }
    //            case "curadoriaId" -> {
    //                pc.setCuradoriaId(criteria.getCuradoriaId());
    //                criteria.setCuradoriaId(null);
    //            }
    //            case "versaoId" -> {
    //                pc.setVersaoId(criteria.getVersaoId());
    //                criteria.setVersaoId(null);
    //            }
    //            case "geneId" -> {
    //                pc.setGeneId(criteria.getGeneId());
    //                criteria.setGeneId(null);
    //            }
    //            case "geneNome" -> {
    //                pc.setGeneNome(criteria.getGeneNome());
    //                criteria.setGeneNome(null);
    //            }
    //            case "organismoId" -> {
    //                pc.setOrganismoId(criteria.getOrganismoId());
    //                criteria.setOrganismoId(null);
    //            }
    //            case "organismoNome" -> {
    //                pc.setOrganismoNome(criteria.getOrganismoNome());
    //                criteria.setOrganismoNome(null);
    //            }
    //            case "organismoSigla" -> {
    //                pc.setOrganismoSigla(criteria.getOrganismoSigla());
    //                criteria.setOrganismoSigla(null);
    //            }
    //            case "referenciaId" -> {
    //                pc.setReferenciaId(criteria.getReferenciaId());
    //                criteria.setReferenciaId(null);
    //            }
    //            case "recursoId" -> {
    //                pc.setRecursoId(criteria.getRecursoId());
    //                criteria.setRecursoId(null);
    //            }
    //        }
    //        return createSpecification(pc);
    //    }

    /**
     * Function to convert {@link ProteinaCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Proteina> createSpecification(ProteinaCriteria criteria) {
        return createSpecification(criteria, null, null, null);
    }

    /**
     * Function to convert {@link ProteinaCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param qORs     nome dos campos que usam OR em minusculo, e nome do primeiro campo da pesquisa em maiusculo.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Proteina> createSpecification(
        ProteinaCriteria criteria,
        String qORs,
        Specification<Proteina> from,
        String uniq
    ) {
        List<String> qORsf = Arrays.asList(qORs == null ? new String[] {} : qORs.split(","));
        if (from == null) log.debug("FROM null");
        Specification<Proteina> specification = Specification.where(from);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                log.debug("DISTINCT Proteina");
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null && (uniq == null || "id".equals(uniq))) {
                Specification<Proteina> ps = buildRangeSpecification(criteria.getId(), Proteina_.id);
                log.debug(qORsf.contains("id") ? "OR id" : "AND id");
                specification = qORsf.contains("id") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getNome() != null && (uniq == null || "nome".equals(uniq))) {
                Specification<Proteina> ps = buildStringSpecification(criteria.getNome(), Proteina_.nome);
                log.debug(qORsf.contains("nome") ? "OR nome" : "AND nome");
                specification = qORsf.contains("nome") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getTamanho() != null && (uniq == null || "tamanho".equals(uniq))) {
                Specification<Proteina> ps = buildRangeSpecification(criteria.getTamanho(), Proteina_.tamanho);
                log.debug(qORsf.contains("tamanho") ? "OR tamanho" : "AND tamanho");
                specification = qORsf.contains("tamanho") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getMassa() != null && (uniq == null || "massa".equals(uniq))) {
                Specification<Proteina> ps = buildStringSpecification(criteria.getMassa(), Proteina_.massa);
                log.debug(qORsf.contains("massa") ? "OR massa" : "AND massa");
                specification = qORsf.contains("massa") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getDescricao() != null && (uniq == null || "descricao".equals(uniq))) {
                Specification<Proteina> ps = buildStringSpecification(criteria.getDescricao(), Proteina_.descricao);
                log.debug(qORsf.contains("descricao") ? "OR descricao" : "AND descricao");
                specification = qORsf.contains("descricao") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getCuradoriaId() != null && (uniq == null || "curadoriaId".equals(uniq))) {
                Specification<Proteina> ps = buildSpecification(
                    criteria.getCuradoriaId(),
                    root -> root.join(Proteina_.curadoria, JoinType.LEFT).get(Curadoria_.id)
                );
                log.debug(qORsf.contains("curadoriaId") ? "OR curadoriaId" : "AND curadoriaId");
                specification = qORsf.contains("curadoriaId") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getVersaoId() != null && (uniq == null || "versaoId".equals(uniq))) {
                Specification<Proteina> ps = buildSpecification(
                    criteria.getVersaoId(),
                    root -> root.join(Proteina_.versao, JoinType.LEFT).get(Versao_.id)
                );
                log.debug(qORsf.contains("") ? "OR versaoId" : "AND versaoId");
                specification = qORsf.contains("versaoId") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getGeneId() != null && (uniq == null || "geneId".equals(uniq))) {
                Specification<Proteina> ps = buildSpecification(
                    criteria.getGeneId(),
                    root -> root.join(Proteina_.gene, JoinType.LEFT).get(Gene_.id)
                );
                log.debug(qORsf.contains("") ? "OR geneId" : "AND geneId");
                specification = qORsf.contains("geneId") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getGeneNome() != null && (uniq == null || "geneNome".equals(uniq))) {
                Specification<Proteina> ps = buildSpecification(
                    criteria.geneNome(),
                    root -> root.join(Proteina_.gene, JoinType.LEFT).get(Gene_.nome)
                );
                log.debug(qORsf.contains("") ? "OR geneNome" : "AND geneNome");
                specification = qORsf.contains("geneNome") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getOrganismoId() != null && (uniq == null || "organismoId".equals(uniq))) {
                Specification<Proteina> ps = buildSpecification(
                    criteria.getOrganismoId(),
                    root -> root.join(Proteina_.gene, JoinType.LEFT).get(Gene_.organismo).get(Organismo_.id)
                );
                log.debug(qORsf.contains("") ? "OR organismoId" : "AND organismoId");
                specification = qORsf.contains("organismoId") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getOrganismoNome() != null && (uniq == null || "organismoNome".equals(uniq))) {
                Specification<Proteina> ps = buildSpecification(
                    criteria.getOrganismoNome(),
                    root -> root.join(Proteina_.gene, JoinType.LEFT).get(Gene_.organismo).get(Organismo_.nome)
                );
                log.debug(qORsf.contains("organismoNome") ? "OR organismoNome" : "AND organismoNome");
                specification = qORsf.contains("organismoNome") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getOrganismoSigla() != null && (uniq == null || "organismoSigla".equals(uniq))) {
                Specification<Proteina> ps = buildSpecification(
                    criteria.getOrganismoSigla(),
                    root -> root.join(Proteina_.gene, JoinType.LEFT).get(Gene_.organismo).get(Organismo_.sigla)
                );
                log.debug(qORsf.contains("") ? "OR organismoSigla" : "AND organismoSigla");
                specification = qORsf.contains("organismoSigla") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getReferenciaId() != null && (uniq == null || "referenciaId".equals(uniq))) {
                Specification<Proteina> ps = buildSpecification(
                    criteria.getReferenciaId(),
                    root -> root.join(Proteina_.referencias, JoinType.LEFT).get(Referencia_.id)
                );
                log.debug(qORsf.contains("referenciaId") ? "OR referenciaId" : "AND referenciaId");
                specification = qORsf.contains("referenciaId") ? specification.or(ps) : specification.and(ps);
            }
            if (criteria.getRecursoId() != null && (uniq == null || "recursoId".equals(uniq))) {
                Specification<Proteina> ps = buildSpecification(
                    criteria.getRecursoId(),
                    root -> root.join(Proteina_.recursos, JoinType.LEFT).get(Recurso_.id)
                );
                log.debug(qORsf.contains("recursoId") ? "OR recursoId" : "AND recursoId");
                specification = qORsf.contains("recursoId") ? specification.or(ps) : specification.and(ps);
            }
        }
        return specification;
    }
}
