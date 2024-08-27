package org.semprotdb.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.semprotdb.domain.*;
import org.semprotdb.domain.enumeration.BioDB;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Status;
import org.semprotdb.repository.*;
import org.semprotdb.service.criteria.CargaCriteria;
import org.semprotdb.service.criteria.ProteinaCriteria;
import org.semprotdb.util.*;
import org.semprotdb.util.FileIO.Excel;
import org.semprotdb.util.FileIO.TSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service Implementation for managing {@link org.semprotdb.domain.Versao}.
 */
@Service
@Transactional
public class VersaoService {

    private static final Logger log = LoggerFactory.getLogger(VersaoService.class);

    private final VersaoRepository versaoRepository;
    private final CargaQueryService cargaQueryService;
    private final CargaRepository cargaRepository;
    private final ReferenciaRepository referenciaRepository;
    private final OrganismoRepository organismoRepository;
    private final ProteinaRepository proteinaRepository;
    private final GeneRepository geneRepository;
    private final RecursoRepository recursoRepository;
    private final ProteinaQueryService proteinaQueryService;
    private final CuradoriaRepository curadoriaRepository;
    private final DBConfigRepository dbConfigRepository;

    public VersaoService(
        VersaoRepository versaoRepository,
        CargaQueryService cargaQueryService,
        CargaRepository cargaRepository,
        ReferenciaRepository referenciaRepository,
        OrganismoRepository organismoRepository,
        ProteinaRepository proteinaRepository,
        GeneRepository geneRepository,
        RecursoRepository recursoRepository,
        ProteinaQueryService proteinaQueryService,
        CuradoriaRepository curadoriaRepository,
        DBConfigRepository dbConfigRepository
    ) {
        this.versaoRepository = versaoRepository;
        this.cargaQueryService = cargaQueryService;
        this.cargaRepository = cargaRepository;
        this.referenciaRepository = referenciaRepository;
        this.organismoRepository = organismoRepository;
        this.proteinaRepository = proteinaRepository;
        this.geneRepository = geneRepository;
        this.recursoRepository = recursoRepository;
        this.proteinaQueryService = proteinaQueryService;
        this.curadoriaRepository = curadoriaRepository;
        this.dbConfigRepository = dbConfigRepository;
    }

    /**
     * Save a versao.
     *
     * @param versao the entity to save.
     * @return the persisted entity.
     */
    public Versao save(Versao versao) {
        log.debug("Request to save Versao : {}", versao);

        versao.setStatus(Status.CRIADO);
        versao.setRelease(versao.getRelease() == null ? new Date().toInstant() : versao.getRelease());
        versao.setTexto(
            versao.getDetalhes() == null
                ? ("<p class='text-center'>Versao " + versao.getNumero() + " : <b>" + versao.getNome() + "</b><p>")
                : versao.getTexto()
        );

        return versaoRepository.save(versao);
    }

    /**
     * Update a versao.
     *
     * @param versao the entity to save.
     * @return the persisted entity.
     */
    public Versao update(Versao versao) {
        log.debug("Request to update Versao : {}", versao);
        Optional<Versao> atual = versaoRepository.findById(versao.getId());
        versao.setStatus(changeStatus(atual.orElseThrow(), versao.getStatus()));
        log.info("STATUS => {}", versao.getStatus());
        return versaoRepository.save(versao);
    }

    /**
     * Partially update a versao.
     *
     * @param versao the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Versao> partialUpdate(Versao versao) {
        log.debug("Request to partially update Versao : {}", versao);

        return versaoRepository
            .findById(versao.getId())
            .map(existingVersao -> {
                if (versao.getNome() != null) {
                    existingVersao.setNome(versao.getNome());
                }
                if (versao.getDetalhes() != null) {
                    existingVersao.setDetalhes(versao.getDetalhes());
                }
                if (versao.getRelease() != null) {
                    existingVersao.setRelease(versao.getRelease());
                }
                if (versao.getLabel() != null) {
                    existingVersao.setLabel(versao.getLabel());
                }
                if (versao.getStatus() != null) {
                    existingVersao.setStatus(changeStatus(existingVersao, versao.getStatus()));
                    log.info("NOVO STATUS => {}", versao.getStatus());
                }
                if (versao.getNumero() != null) {
                    existingVersao.setNumero(versao.getNumero());
                }
                if (versao.getLogo() != null) {
                    existingVersao.setLogo(versao.getLogo());
                }
                if (versao.getLog() != null) {
                    existingVersao.setLog(versao.getLog());
                }
                if (versao.getTexto() != null) {
                    existingVersao.setTexto(versao.getTexto());
                }
                if (versao.getImagem() != null) {
                    existingVersao.setImagem(versao.getImagem());
                }
                if (versao.getImagemContentType() != null) {
                    existingVersao.setImagemContentType(versao.getImagemContentType());
                }

                return existingVersao;
            })
            .map(versaoRepository::save);
    }

    /**
     * Get one versao by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Versao> findOne(Long id) {
        log.debug("Request to get Versao : {}", id);
        return versaoRepository.findById(id);
    }

    /**
     * Delete the versao by id.
     *
     * @param versao the entity.
     */
    @Async
    public void delete(Versao versao) {
        log.debug("Request to delete Versao : {}", versao);
        removeVersao(versao);
    }

    private Status changeStatus(Versao atual, Status statusNovo) {
        Status statusAtual = atual.getStatus();
        log.info("Tentando atualizar STATUS : {} => {}", atual.getStatus(), statusNovo);

        /// informando que todos arquivos foram carregados
        if (statusAtual == Status.CRIADO) {
            return Status.CARREGADO;
        }

        ///deixar voltar se der erro
        if (statusAtual.ordinal() <= Status.CARREGADO.ordinal() && statusNovo == Status.CRIADO) {
            return Status.CRIADO;
        }

        /// alterando status da versao processada
        if (
            statusAtual.ordinal() >= Status.PROCESSADO.ordinal() &&
            statusNovo.ordinal() >= Status.PROCESSADO.ordinal() &&
            statusNovo.ordinal() < Status.INVALIDO.ordinal()
        ) {
            return statusNovo;
        }

        return statusAtual;
    }

    @Async
    public void processarVersaoasync(Long versaoId) {
        log.info("Processando versao => {}", versaoId);
        Versao versao = null;
        Status status = Status.INVALIDO;
        try {
            /// force async comportamento
            Thread.sleep(5000);
            versao = versaoRepository.findById(versaoId).orElseThrow();
            status = versao.getStatus();

            if (versao.getStatus() == Status.PROCESSADO) {
                versao.setStatus(Status.CARREGADO);
                versaoRepository.save(versao);
                status = Status.DISPONIVEL;
                versao.addLog("Gerando novo arquivo de DOWNLOAD da versao.");
                log.info("Gerando novo arquivo de DOWNLOAD da versao {}", versao);
                atualizarDownloadFile(versaoId);
                return;
            }

            if (versao.getStatus() != Status.CARREGADO) return;

            status = Status.CRIADO;
            log.info("Processando versao => {} MODO publicar", versao.identfy());

            CargaCriteria cargaCriteria = new CargaCriteria();
            LongFilter longFilter = new LongFilter();
            longFilter.setSpecified(true).setEquals(versaoId);
            cargaCriteria.setVersaoId(longFilter);
            List<Carga> cargas = cargaQueryService.findByCriteria(cargaCriteria, Pageable.unpaged()).getContent();

            if (cargas.isEmpty()) {
                versao.setLog("ERRO: Versao sem arquivos de cargas.");
                log.warn("Abortar processamento da VERSAO sem cargas: {}", versao);
                return;
            }

            versao.setLog("Processando versao " + versao.getNome() + " com " + cargas.size() + " cargas.");

            /// verificar se tem alguma carga invalida
            Set<Carga> cargas_inv = cargas.stream().filter(c -> !c.getValidado()).collect(Collectors.toSet());
            if (!cargas_inv.isEmpty()) {
                versao.setLog("Resolver " + cargas_inv.size() + " cargas invalidas.");
                log.info("Abortar processamento da VERSAO com cargas invalidas: {}", cargas_inv);
                return;
            }

            ///verificar se tem arquivo de restaurar
            Set<Carga> restaurar = cargas.stream().filter(c -> c.getDestino() == Destino.RESTORE).collect(Collectors.toSet());
            if (!restaurar.isEmpty()) {
                log.info("Restaurando versao com {} cargas", restaurar.size());
                for (Carga c : restaurar) {
                    c = cargaRepository.findById(c.getId()).orElseThrow(() -> new Exception("Carga invalida"));
                    log.info("Restaurando carga {}", c);
                    storePtnas(new DataModelRecover(c).asProteinas(), versao, false);
                }
                log.info("Terminou restauracao das {} cargas", restaurar.size());
                status = Status.PROCESSADO;
                log.info("Terminou restauracao VERSAO {}", versao.identfy());
                gerarXLSXorganismos(makeDownload(versao));
                versao.addLog("Versao restaurada com sucesso.");
                return;
            }

            /// verificar se tem arquivos de dados
            Set<Carga> carga_dados = cargas.stream().filter(c -> c.getDestino() == Destino.DADOS).collect(Collectors.toSet());
            if (carga_dados.isEmpty()) {
                versao.setLog("Nenhuma carga de dados encontrada.");
                log.warn("Abortar processamento da VERSAO SEM cargas de dados validas: {}", versao);
                return;
            }

            carga_dados = carga_dados
                .stream()
                .map(c -> cargaRepository.findById(c.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

            Set<Carga> carga_map = cargas.stream().filter(c -> c.getDestino() == Destino.MAPEAR).collect(Collectors.toSet());

            if (carga_map.size() < 3) {
                /// gerar aquivos de ids para mapear
                Set<Carga> carga_upload = cargas.stream().filter(c -> c.getDestino() == Destino.UPLOAD).collect(Collectors.toSet());
                if (carga_upload.size() < 4) {
                    List<Proteina> ptnas = cargas2ptnas(carga_dados, versao);
                    log.info("Gerando arquivo de UPLOAD para de IDS proteinas: {}", ptnas.size());

                    for (BioDB db : new BioDB[] { BioDB.UNIPROT, BioDB.REFSEQ, BioDB.GI, BioDB.OUTRO }) {
                        HashSet<String> ids = getIDs(ptnas, db);
                        log.info("Total {} IDS tipo encontrados tipo {}", ids.size(), db);
                        storeIDS(ids, db, versao);
                    }

                    versao.setLog("Os 4 arquivos foram criados para mapear.");
                    cargaRepository.deleteAll(carga_upload);
                    return;
                }

                versao.setLog("Carregue e valide todos 3 arquivos, apenas " + carga_map.size() + " foram encontrados.");
                return;
            } else {
                versao.setLog("Iniciando processamento da versao.");
            }

            /// processar arquivos de mapeamento
            ArrayList<Proteina> proteinas_map = new ArrayList<>();
            log.info("A integrar {} arquivos de MAPEAMENTO encontrados", carga_map.size());
            for (Carga c : carga_map) {
                c = cargaRepository.findById(c.getId()).orElseThrow();
                log.info("Abrindo carga {}", c);
                DataModelUniprot du = new DataModelUniprot(c);
                List<Proteina> ptnas = du.asProteinas();
                versao.addLog("Em " + c.getNome() + " foram encontrados " + ptnas.size() + " registros.");
                log.info("Em {} foram encontrados {} registros", c, ptnas.size());
                proteinas_map.addAll(ptnas);
            }

            log.info("A integrar {} arquivos de DADOS encontrados", carga_dados.size());
            List<Proteina> proteinas = cargas2ptnas(carga_dados, null);
            log.info("A integrar {} proteinas", proteinas.size());

            versao.addLog("A integrar " + proteinas.size() + " com " + proteinas_map.size() + " proteinas.");
            int at = RowPtn.join(proteinas, proteinas_map);

            if (at < 1) {
                log.warn("Falhou ao integrar dados de {} MAP_UNIPROT com {} DATA_PTNAS", proteinas_map.size(), proteinas.size());
                versao.addLog("ERRO: Falhou ao juntar os dados. Nenhum ID da carga corresponde a algum ID nos arquivos de mapeamento");
            } else versao.addLog("Total " + at + " proteinas atualizadas com dados do uniprot.");

            int total = storePtnas(proteinas, versao, true);
            status = Status.PROCESSADO;
            versao.addLog("Terminou o processamento das " + total + " proteinas, " + "Gerando o arquivo de download dessa nova versao.");

            gerarXLSXorganismos(makeDownload(versao));
            log.info("Terminou a integracao de dados com {} proteinas processadas, A PERSISTIR ...", total);
        } catch (Exception e) {
            if (versao != null) versao.setLog(e.toString());
            log.error("Falhou ao processar versao", e);
        } finally {
            if (versao != null) versaoRepository.save(versao.status(status));
            else log.error("Falhou ao encontrar versao: {}", versaoId);
        }
    }

    private List<Proteina> cargas2ptnas(final Set<Carga> cargas, Versao versao) throws Exception {
        ArrayList<Proteina> proteinas = new ArrayList<>();
        String cid;
        ArrayList<String> consolidado = new ArrayList<>();
        for (Carga carga : cargas) {
            cid = "[" + carga.getId() + "] " + carga.getNome();
            try {
                DataModelTabela tabela = new DataModelTabela(carga);
                List<Proteina> ptnas = tabela.asProteinas();
                proteinas.addAll(ptnas);
                if (versao != null) ptnas.forEach(
                    p ->
                        p
                            .getReferencias()
                            .forEach(
                                r ->
                                    consolidado.add(
                                        String.join(
                                            "\t",
                                            new String[] {
                                                carga.getNome(),
                                                r.getCitacao(),
                                                p.getGene().getOrganismo().getNome(),
                                                p.getGene().getNome(),
                                                p.getNome(),
                                                p.getDescricao(),
                                            }
                                        )
                                    )
                            )
                );
            } catch (Exception e) {
                log.warn("ERRO ao transformar carga {} em tabela", carga);
                throw new Exception("Erro na carga " + cid + " => " + e.getMessage());
            }
        }
        if (versao != null) {
            Carga file = new TSV("Consolidado.tsv", consolidado).toCarga(versao).status("OK").ordem(7).destino(Destino.DOWNLOAD);
            log.info("Persistindo CONSOLIDADO {}", file);
            cargaRepository.save(file);
        }
        return proteinas;
    }

    private HashSet<String> getIDs(final List<Proteina> proteinas, BioDB db) {
        HashSet<String> ids = new HashSet<>();
        proteinas
            .stream()
            .map(p -> p.getRecursos().iterator().next())
            .filter(r -> r.getUid() != null && r.getDb() == db)
            .map(Recurso::getUid)
            .forEach(ids::add);
        return ids;
    }

    private void storeIDS(HashSet<String> ids, BioDB db, Versao versao) throws Exception {
        log.info("Gerando TSV de {} {} IDS do tipo {}", versao.getId(), versao.getNome(), db);
        String fname = "V" + versao.getId() + "_MAPEAR_" + db + ".txt";
        Carga file = new TSV(fname, ids.stream().sorted().toList()).toCarga(versao).status("OK").ordem(4).destino(Destino.UPLOAD);
        log.info("Persistindo IDS {}", file);
        cargaRepository.save(file);
        versao.setLog("Total " + ids.size() + " ids tipo " + db + " exportados.");
    }

    private int storePtnas(List<Proteina> ptns, Versao versao, boolean extend) throws Exception {
        log.info("Iniciou persistir {} proteinas da versao {} {}", ptns.size(), versao.getId(), versao.getNome());

        HashMap<String, Referencia> R = new HashMap<>();
        HashMap<String, Organismo> O = new HashMap<>();
        HashMap<String, Gene> G = new HashMap<>();
        HashMap<String, Recurso> X = new HashMap<>();
        HashMap<String, Proteina> P = new HashMap<>();
        HashMap<String, Curadoria> C = new HashMap<>();

        //        As referencias, organismos e recursos de cada versao re-usados
        referenciaRepository.findAll().forEach(r -> R.put(r.getCitacao(), r));
        log.info("Total {} referencias serao reusadas de versoes anteriores", R.size());

        organismoRepository.findAll().forEach(o -> O.put(o.getNome(), o));
        log.info("Total {} organismos serao reusadas de versoes anteriores", O.size());

        recursoRepository.findAllLight(Pageable.unpaged()).getContent().forEach(x -> X.put(x.getUid(), x));
        log.info("Total {} recursos serao reusadas de versoes anteriores", X.size());

        AtomicInteger keg = new AtomicInteger();
        AtomicInteger stg = new AtomicInteger();
        int invalidas = 0;
        int puladas = 0;

        for (Proteina p : ptns) {
            String p_id = p.getNome();

            if (p_id == null || p_id.isEmpty() || p_id.equals(BioDBParser.NO_ID)) {
                log.warn("Total {} Proteina com ID invalido {} => {} ", ++invalidas, p_id, p.getDescricao());
                continue;
            }

            if (
                p.getCuradoria() != null &&
                p.getCuradoria().getEmail() != null &&
                p.getCuradoria().getEmail().trim().isEmpty() &&
                !C.containsKey(p.getCuradoria().getEmail())
            ) {
                String curador = versao.getNome() + "_" + p.getCuradoria().getEmail().trim();
                Curadoria c = curadoriaRepository.save(new Curadoria().email(curador).data(new Date().toInstant()));
                C.put(p.getCuradoria().getEmail(), c);
                log.info("NOVO curador {}", c.getEmail());
            }

            Proteina PROTEINA = P.get(p_id);
            if (PROTEINA == null) {
                P.put(
                    p_id,
                    proteinaRepository.save(
                        new Proteina()
                            .nome(p.getNome())
                            .massa(p.getMassa())
                            .tamanho(p.getTamanho())
                            .descricao(p.getDescricao())
                            .versao(versao)
                            .curadoria(
                                p.getCuradoria() != null && C.get(p.getCuradoria().getEmail()) != null
                                    ? C.get(p.getCuradoria().getEmail())
                                    : null
                            )
                    )
                );
                log.debug("{} > NOVA proteina {}", P.size(), p_id);
            } else {
                /// ja tem essa ptna
                /// nome deve ser o menor
                PROTEINA.setDescricao(
                    ((p.getDescricao().length() > 1) && (p.getDescricao().length() < PROTEINA.getDescricao().length()))
                        ? p.getDescricao()
                        : PROTEINA.getDescricao()
                );
                log.debug("{} descricao: {}", p_id, p.getDescricao());
                /// referencia deve ser add
                handleREF(p, R, PROTEINA);
                proteinaRepository.save(PROTEINA);
                puladas++;

                /// links devem ser add
                handleRecs(p, extend, X, keg, stg, PROTEINA);
                continue;
            }

            PROTEINA = P.get(p_id);
            handleREF(p, R, PROTEINA);

            Organismo o = p.getGene().getOrganismo();
            if (!O.containsKey(o.getNome())) {
                String s = String.join("", Arrays.stream(o.getNome().toUpperCase().split(" ")).map(x -> x.substring(0, 1)).toList());
                o = organismoRepository.save(
                    new Organismo()
                        .nome(o.getNome())
                        .apelido(o.getNome().toUpperCase().charAt(0) + o.getNome().toLowerCase().substring(1))
                        .sigla(
                            s
                                .replace("CH", "CHX")
                                .replace("EA", "EAI")
                                .replace("BT", "BTA")
                                .replace("SS", "SSC")
                                .replace("CL", "CFA")
                                .replace("EC", "ECB")
                                .replace("OA", "OAS")
                            ////Bubalos bubalis => not found
                            /// https://www.genome.jp/dbget-bin/www_bfind_sub?mode=bfind&max_hit=1000&locale=en&serv=gn&dbkey=alldb&keywords=Bubalos+bubalis&page=1
                        )
                );
                O.put(o.getNome(), o);
                log.info("NOVO organismo {}", o);
            }
            o = O.get(o.getNome());

            if (PROTEINA.getGene() == null) {
                if (p.getGene() == null) throw new Exception("Proteina " + p + " sem gene.");
                Gene g = p.getGene();
                String g_id = g.getOrganismo().getNome() + " > " + g.getNome();
                if (!G.containsKey(g_id)) {
                    String _c = null;
                    if (g.getCuradoria() != null) {
                        String curador = g.getCuradoria().getEmail();
                        curador = curador == null ? "" : curador.trim();
                        if (curador.length() > 2) {
                            _c = versao.getNome() + "_" + curador;
                            if (!C.containsKey(curador)) {
                                C.put(curador, curadoriaRepository.save(new Curadoria().email(_c).data(new Date().toInstant())));
                                log.info("NOVO curador {} para os genes", g.getCuradoria().getEmail());
                            }
                            _c = curador;
                        }
                    }
                    g = geneRepository.save(new Gene().nome(g.getNome()).organismo(o).curadoria(C.get(_c)));
                    G.put(g_id, g);
                } else {
                    g = G.get(g_id);
                }
                PROTEINA.setGene(g);
            } else if (!PROTEINA.getGene().getNome().equals(p.getGene().getNome())) {
                log.warn(
                    "Proteina {} com GENE {} diferente {} na proteina {}",
                    PROTEINA,
                    PROTEINA.getGene().getNome(),
                    p.getGene().getNome(),
                    p
                );
            }

            handleRecs(p, extend, X, keg, stg, PROTEINA);

            if ((P.size() < 100) || ((P.size() % 100) == 0)) log.debug(
                "Status {} REFS > {} ORGS > {} GENS > {} PTNAS > {} RECS > {} CURATORS ",
                R.size(),
                O.size(),
                G.size(),
                P.size(),
                X.size(),
                C.size()
            );

            proteinaRepository.save(PROTEINA);
        }

        log.info(
            "Terminou com {} REFS > {} ORGS > {} GENS > {} PTNAS > {} RECS > {} CURATORS ",
            R.size(),
            O.size(),
            G.size(),
            P.size(),
            X.size(),
            C.size()
        );
        log.info("Total {} Proteinas redundantes", puladas);
        log.info("{} RECS: {} => STRING {} KEGG {} encontrados", extend ? "Extend" : "", X.size(), stg.get(), keg.get());

        versao.addLog(
            "Tentando persistir " +
            R.size() +
            " referencias " +
            O.size() +
            " organismos " +
            G.size() +
            " genes " +
            X.size() +
            " recursos " +
            P.size() +
            " proteinas" +
            C.size() +
            " curadorias."
        );
        return P.size();
    }

    private void handleRecs(
        Proteina p,
        boolean extend,
        HashMap<String, Recurso> X,
        AtomicInteger keg,
        AtomicInteger stg,
        Proteina PROTEINA
    ) {
        p.setGene(PROTEINA.getGene());
        Set<Recurso> recursos = p.getRecursos();

        if (extend) recursos.addAll(Arrays.stream(BioDBParser.recursos2recursos(p)).toList());

        for (Recurso recurso : recursos) {
            if (recurso != null) {
                if (recurso.getDb() != BioDB.OUTRO) {
                    Recurso _r = X.get(recurso.getUid());
                    if (_r == null) {
                        _r = new Recurso()
                            .uid(recurso.getUid())
                            .db(recurso.getDb())
                            .link(
                                recurso.getLink() == null
                                    ? BioDBParser.recurso2link(
                                        new Recurso()
                                            .uid(recurso.getUid().contains(":") ? recurso.getUid().split(":")[1] : recurso.getUid())
                                            .db(recurso.getDb()),
                                        p.getGene().getOrganismo().getSigla() == null
                                            ? null
                                            : p.getGene().getOrganismo().getSigla().toLowerCase()
                                    )
                                    : recurso.getLink()
                            );
                        _r = recursoRepository.save(_r);
                        X.put(recurso.getUid(), _r);
                        if (_r.getDb() == BioDB.KEGG) keg.getAndIncrement();
                        else if (_r.getDb() == BioDB.STRINGDB) stg.getAndIncrement();
                    }
                    PROTEINA.addRecurso(_r);
                }
            }
        }
    }

    private void handleREF(Proteina p, HashMap<String, Referencia> R, Proteina PROTEINA) throws Exception {
        if (p.getReferencias().isEmpty()) throw new Exception("Proteina " + p + " sem referencias.");
        Referencia r = p.getReferencias().iterator().next();
        if (R.containsKey(r.getCitacao())) {
            if (PROTEINA.getReferencias().stream().noneMatch(_r -> _r.getCitacao().equals(r.getCitacao()))) PROTEINA.addReferencia(
                R.get(r.getCitacao())
            );
        } else {
            Referencia _r = referenciaRepository.save(new Referencia().citacao(r.getCitacao()).link(r.getLink()));
            R.put(r.getCitacao(), _r);
            PROTEINA.addReferencia(_r);
            log.info("NOVA referencia {}", _r);
        }
    }

    private Carga makeDownload(Versao versao) throws IOException {
        log.info("Gerando DOWNLOAD para disponibilzar versao {}", versao.getNome());

        ProteinaCriteria proteinaCriteria = new ProteinaCriteria();
        LongFilter longFilter = new LongFilter();
        longFilter.setSpecified(true).setEquals(versao.getId());
        proteinaCriteria.setVersaoId(longFilter);

        ArrayList<String> linhas = new ArrayList<>();
        linhas.add("#\tID\tORGANISM\tGENE\tPROTEIN\tLENGTH\tMASS\tREFERENCE\tENTRY\tCURATOR");
        int cont = 0;
        int page = 0;
        String vid = "." + versao.getNumero();
        Page<Proteina> pg;
        do {
            pg = proteinaQueryService.findByCriteria(proteinaCriteria, Pageable.ofSize(10000).withPage(page++));
            List<Proteina> proteinas = pg.getContent();
            log.info("[" + "[{}/{}]Gerando TSV para {} proteinas de {}", page, pg.getTotalPages(), proteinas.size(), pg.getTotalElements());

            for (Proteina P : proteinas) {
                String proteina = P.getDescricao() == null ? P.getNome() : P.getDescricao();
                String tamanho = P.getTamanho() == null ? "0" : P.getTamanho().toString();
                String massa = P.getMassa() == null ? "0" : P.getMassa();
                String referencias = String.join(" & ", P.getReferencias().stream().sorted().map(Referencia::getCitacao).toList());
                String organismo = P.getGene().getOrganismo().getApelido() == null
                    ? P.getGene().getOrganismo().getNome()
                    : P.getGene().getOrganismo().getApelido();
                String gene = P.getGene().getDescricao() == null ? P.getGene().getNome() : P.getGene().getDescricao();
                String recursos = String.join(";", P.getRecursos().stream().sorted().map(Recurso::getUid).toList());
                String curador = P.getCuradoria() == null ? "X" : P.getCuradoria().getId().toString();
                curador += P.getGene().getCuradoria() == null ? "" : ("G" + P.getGene().getCuradoria().getId().toString());
                linhas.add(
                    ++cont +
                    "\t" +
                    P.getId() +
                    vid +
                    "\t" +
                    organismo.replaceAll("\t", " ") +
                    "\t" +
                    gene.replaceAll("\t", " ") +
                    "\t" +
                    proteina.replaceAll("\t", " ") +
                    "\t" +
                    tamanho.replaceAll("\t", " ") +
                    "\t" +
                    massa.replaceAll("\t", " ") +
                    "\t" +
                    referencias.replaceAll("\t", " ") +
                    "\t" +
                    recursos.replaceAll("\t", " ") +
                    "\t" +
                    curador
                );
                if ((linhas.size() < 100) || ((linhas.size() % 100) == 0)) log.debug(
                    "TOTAL {} registros processados de {} proteinas para o TSV.",
                    linhas.size() - 1,
                    pg.getTotalElements()
                );
            }
        } while (pg.hasNext());

        String f_name = fname(versao, "DEFAULT", "tsv");
        Carga file = new TSV(f_name, linhas).zip().toCarga(versao).ordem(3);
        cargaRepository.save(file);
        versao.addLog("Total " + cont + " registros exportados.");
        log.info("Arquivo TSV ZIP {} gerado para versao {} ! versao sera publicada com {} registros. ", file, versao.getNome(), cont);
        return file;
    }

    private String fname(Versao versao, String tipo, String sfx) {
        String dt = new SimpleDateFormat("yyMMdd").format(new Date());
        return "semprotdb_" + tipo + "_V" + versao.getNumero() + "_" + dt + "." + sfx;
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void atualizarDownloadFileBackup() {
        log.info("Atualizando BACKUP dos arquivos de DOWNLOAD");
        atualizarDownloadFile(null);
    }

    public void gerarXLSXorganismos(Carga c) throws Exception {
        log.info("Gerar arquivos de DOWNLOAD por organismo para carga {}", c);

        List<Proteina> proteinas = new DataModelRecover(c).asProteinas();
        HashMap<String, DataSet> sheets = new HashMap<>();
        String[] cols = new String[] { "GENE", "PROTEIN", "MASS", "LENGTH", "REFERENCE", "LINKS", "CURATOR" };
        int[] sizes = new int[] { Excel.W_PQ, Excel.W_XLG, Excel.W_PQ, Excel.W_PQ, Excel.W_MD, Excel.W_MD, Excel.W_PQ };

        for (Proteina P : proteinas) {
            String org = P.getGene().getOrganismo().getApelido();
            if (!sheets.containsKey(org)) sheets.put(org, new DataSet(cols, new ArrayList<>()));
            sheets
                .get(org)
                .getLinhas()
                .add(
                    new String[] {
                        P.getGene().getNome(),
                        P.getDescricao(),
                        P.getMassa(),
                        P.getTamanho() == null ? "0" : P.getTamanho().toString(),
                        String.join(" & ", P.getReferencias().stream().map(Referencia::getCitacao).toList()),
                        String.join(" ; ", P.getRecursos().stream().map(Recurso::getUid).toList()),
                        (P.getCuradoria() == null ? (P.getGene().getCuradoria() == null ? "X" : "") : P.getCuradoria().getEmail()) +
                        (P.getGene().getCuradoria() == null ? "" : ("G" + P.getGene().getCuradoria().getEmail())),
                    }
                );
        }
        log.debug("Persistir arquivos de DOWNLOAD por ORGANISMO para carga {}", c);
        Carga byo = new Excel(fname(c.getVersao(), "byOrganism", "xlsx"), sheets, sizes).toCarga(c.getVersao()).ordem(8);
        cargaRepository.save(byo);
    }

    public void atualizarDownloadFile(Long vid) {
        log.info("Atualizando arquivos de DOWNLOAD");

        HashMap<Long, Carga> tsvs = new HashMap<>();
        String v = "?";
        int totalc = 0;
        try {
            Set<Versao> vs = versaoRepository
                .findAll()
                .stream()
                .filter(_v -> vid == null ? (_v.getStatus().ordinal() >= Status.PROCESSADO.ordinal()) : _v.getId().equals(vid))
                .collect(Collectors.toSet());
            for (Versao versao : vs) {
                totalc += (int) versao.getCargas().stream().filter(c -> c.getDestino() == Destino.DOWNLOAD).count();
                v = versao.identfy();
                log.info("Gerar arquivos para versao {} de {} versoes", v, vs.size());
                Carga c = makeDownload(versao);
                tsvs.put(c.getId(), c);
            }
        } catch (Exception e) {
            log.error("ERRO ao tentar gerar DOWNLOAD para versao " + v, e.getCause());
        }

        List<Carga> cargas = cargaRepository
            .findAllByVersaoStatusIsInAndDestino(Collections.singletonList(Status.DISPONIVEL), Destino.DOWNLOAD, Pageable.unpaged())
            .stream()
            .toList();

        log.info("Total de cargas antes {} e depois {}. ", totalc, cargas.size());

        ArrayList<Carga> apagar = new ArrayList<>();
        for (Carga carga1 : cargas) {
            for (Carga carga2 : cargas) {
                if (
                    carga1.getId().equals(carga2.getId()) ||
                    !carga1.getVersao().getId().equals(carga2.getVersao().getId()) ||
                    carga1.getNome() == null ||
                    carga2.getNome() == null ||
                    carga1.getChecksum() == null ||
                    !carga1.getChecksum().contains("|") ||
                    carga2.getChecksum() == null ||
                    !carga2.getStatus().contains("|")
                ) continue;
                String c1MD5 = carga1.getChecksum().split("\\|")[0];
                String c2MD5 = carga2.getChecksum().split("\\|")[0];
                if (!c1MD5.equals(c2MD5)) continue; /// MD5 == MD5
                // duas cargas com conteudo igual
                // apagar a que possui o maior nome
                if (carga1.getNome().compareTo(carga2.getNome()) > 0) {
                    apagar.add(carga1);
                    tsvs.get(carga1.getId()).validado(false);
                }
            }
        }

        if (!apagar.isEmpty()) {
            log.info("Apagando {} cargas duplicadas: {}", apagar.size(), apagar);
            cargaRepository.deleteAll(apagar);
        }

        log.info("Salvar em disco.");
        dbConfigRepository
            .findDBConfigByKey("backup.path")
            .ifPresentOrElse(
                backp -> {
                    if (backp.getHabilitado()) {
                        String D = "/home/semprodb/" + backp.getVstring();
                        File dir = new File(D);
                        if (!dir.exists()) dir.mkdir();
                        if (!dir.isDirectory()) log.error("ERRO ao gerar backup {} is NOT dir!!!", D);
                        else {
                            boolean clear = Objects.requireNonNull(dir.list()).length < 1;
                            tsvs.forEach((i, c) -> {
                                try {
                                    if (clear || c.getValidado()) {
                                        if (clear) log.info("Criando backup em {}/{} {}", D, backp.getVstring(), c);
                                        File f = new File(D + "/" + c.getNome());
                                        FileUtils.writeByteArrayToFile(f, c.getPlanilha());
                                        gerarXLSXorganismos(c);
                                    }
                                } catch (Exception e) {
                                    log.error("ERRO AO SALVAR BACKUP " + c.toString(), e);
                                }
                            });
                        }
                    } else {
                        log.warn("CONFIGURAR LOCAL DO BACKUP !!!");
                    }
                },
                () -> dbConfigRepository.save(new DBConfig().key("backup.path").habilitado(false))
            );
        log.info("Finalizado processamento.");
    }

    public void removeVersao(Versao versao) {
        try {
            log.warn("REMOVENDO versao {}", versao);
            Thread.sleep(5000);

            Optional<Versao> _v = versaoRepository.findById(versao.getId());
            if (_v.isEmpty()) return;
            versao = _v.orElseThrow();

            int ptnas = versao.getProteinas().size();
            log.info("Preparando para remover versao {} com {} proteinas", versao.identfy(), ptnas);

            Set<Proteina> proteinas = new HashSet<>(versao.getProteinas());

            HashSet<Recurso> recs = new HashSet<>();
            for (Proteina p : proteinas) {
                final HashSet<Recurso> rs = new HashSet<>(p.getRecursos());
                rs.forEach(r -> r.removeProteina(p));
                recs.addAll(rs);
            }
            recursoRepository.saveAll(recs);

            log.info("ATUALIZANDO {} relacoes", recs.size());
            proteinas.forEach(
                p -> p.recursos(Collections.emptySet()).gene(null).referencias(Collections.emptySet()).curadoria(null).versao(null)
            );
            versaoRepository.save(versao.proteinas(Collections.emptySet()));

            List<Recurso> rec_vazio_ids = recursoRepository.findAllByProteinasIsEmpty();
            log.info("REMOVENDO {} recursos", rec_vazio_ids.size());
            recursoRepository.deleteAll(rec_vazio_ids);

            log.info("REMOVENDO {} proteinas", proteinas.size());
            proteinaRepository.saveAll(proteinas);
            proteinaRepository.deleteAll(proteinas);

            log.info("REMOVENDO versao {}", versao.identfy());
            versaoRepository.delete(versao);

            /// remover genes nulos
            List<Gene> genes = geneRepository.findAllByProteinasIsEmpty();
            log.info("REMOVENDO {} genes", genes.size());
            geneRepository.deleteAll(genes);

            /// remover organismo nulos
            List<Organismo> organismos_vazios = organismoRepository.findAllByGenesIsEmpty();
            log.info("REMOVENDO {} organismos", organismos_vazios.size());
            organismoRepository.deleteAll(organismos_vazios);

            /// remover referencias nulos
            List<Referencia> referencias_vazios = referenciaRepository.findAllByProteinasIsEmpty();
            log.info("REMOVENDO {} referencias", referencias_vazios.size());
            referenciaRepository.deleteAll(referencias_vazios);
        } catch (Exception e) {
            log.error("FALHA REMOVENDO {} {} ", versao.identfy(), e.toString());
        } finally {
            log.info("Terminou remover versao {}", versao.identfy());
        }
    }
}
