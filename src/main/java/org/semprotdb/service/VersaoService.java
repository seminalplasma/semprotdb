package org.semprotdb.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.semprotdb.domain.*;
import org.semprotdb.domain.enumeration.BioDB;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Status;
import org.semprotdb.repository.*;
import org.semprotdb.service.criteria.CargaCriteria;
import org.semprotdb.service.criteria.ProteinaCriteria;
import org.semprotdb.util.BioDBParser;
import org.semprotdb.util.DataModelTabela;
import org.semprotdb.util.DataModelUniprot;
import org.semprotdb.util.FileIO.TSV;
import org.semprotdb.util.RowPtn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
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

    public VersaoService(
        VersaoRepository versaoRepository,
        CargaQueryService cargaQueryService,
        CargaRepository cargaRepository,
        ReferenciaRepository referenciaRepository,
        OrganismoRepository organismoRepository,
        ProteinaRepository proteinaRepository,
        GeneRepository geneRepository,
        RecursoRepository recursoRepository,
        ProteinaQueryService proteinaQueryService
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
        versao.setTexto(versao.getDetalhes() == null ? versao.toString() : versao.getTexto());

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

        /// informando que todos arquivos foram carregados
        if (statusAtual == Status.CRIADO) {
            return Status.CARREGADO;
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
        Versao versao = null;
        Status status = Status.INVALIDO;
        try {
            /// force async comportamento
            Thread.sleep(5000);
            versao = versaoRepository.findById(versaoId).orElseThrow();
            status = versao.getStatus();

            if (versao.getStatus() == Status.PROCESSADO) {
                status = Status.PROCESSADO;
                versao.addLog("Gerando novo arquivo de DOWNLOAD da versao.");
                log.info("Gerando novo arquivo de DOWNLOAD da versao {}", versao);
                atualizarDownloadFile();
                return;
            }

            if (versao.getStatus() != Status.CARREGADO) return;

            status = Status.CRIADO;

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
                    List<Proteina> ptnas = cargas2ptnas(carga_dados);
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
            List<Proteina> proteinas = cargas2ptnas(carga_dados);
            log.info("A integrar {} proteinas", proteinas.size());

            versao.addLog("A integrar " + proteinas.size() + " com " + proteinas_map.size() + " proteinas.");
            int at = RowPtn.join(proteinas, proteinas_map);

            if (at < 1) {
                log.warn("Falhou ao integrar dados de {} MAP_UNIPROT com {} DATA_PTNAS", proteinas_map.size(), proteinas.size());
                versao.addLog("ERRO: Falhou ao juntar os dados.");
            }

            versao.addLog("Total " + at + " proteinas atualizadas com dados do uniprot.");
            int total = storePtnas(proteinas, versao);
            status = Status.PROCESSADO;
            versao.addLog("Terminou o processamento das " + total + " proteinas, " + "Gerando o arquivo de download dessa nova versao.");

            makeDownload(versao);
            log.info("Terminou a integracao de dados com {} proteinas processadas, A PERSISTIR ...", total);
        } catch (Exception e) {
            if (versao != null) versao.setLog(e.toString());
            else log.error("Falhou ao processar versao", e);
        } finally {
            if (versao != null) versaoRepository.save(versao.status(status));
            else log.error("Falhou ao encontrar versao: {}", versaoId);
        }
    }

    private List<Proteina> cargas2ptnas(final Set<Carga> cargas) throws Exception {
        ArrayList<Proteina> proteinas = new ArrayList<>();
        String cid;
        for (Carga carga : cargas) {
            cid = "[" + carga.getId() + "] " + carga.getNome();
            try {
                DataModelTabela tabela = new DataModelTabela(carga);
                proteinas.addAll(tabela.asProteinas());
            } catch (Exception e) {
                log.warn("ERRO ao transformar carga {} em tabela", carga);
                throw new Exception("Erro na carga " + cid + " => " + e.getMessage());
            }
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

    private int storePtnas(List<Proteina> ptns, Versao versao) {
        log.info("Iniciou persistir {} proteinas da versao {} {}", ptns.size(), versao.getId(), versao.getNome());

        HashMap<String, Referencia> R = new HashMap<>();
        HashMap<String, Organismo> O = new HashMap<>();
        HashMap<String, Gene> G = new HashMap<>();
        HashMap<String, Recurso> X = new HashMap<>();
        HashMap<String, Proteina> P = new HashMap<>();

        //        As referencias, organismos e recursos de cada versao re-usados
        referenciaRepository.findAll().forEach(r -> R.put(r.getCitacao(), r));
        log.info("Total {} referencias serao reusadas de versoes anteriores", R.size());

        organismoRepository.findAll().forEach(o -> O.put(o.getNome(), o));
        log.info("Total {} organismos serao reusadas de versoes anteriores", O.size());

        recursoRepository.findAll().forEach(x -> X.put(x.getUid(), x));
        log.info("Total {} recursos serao reusadas de versoes anteriores", X.size());

        AtomicInteger keg = new AtomicInteger();
        AtomicInteger stg = new AtomicInteger();

        for (Proteina p : ptns) {
            String p_id = p.getGene().getOrganismo().getNome() + " > " + p.getGene().getNome() + " > " + p.getNome();

            if (!P.containsKey(p_id)) P.put(
                p_id,
                proteinaRepository.save(
                    new Proteina().nome(p.getNome()).massa(p.getMassa()).tamanho(p.getTamanho()).descricao(p.getDescricao()).versao(versao)
                )
            );
            else log.info("Proteina REDUNDANTE encontrada nas cargas {}", p);

            Proteina PROTEINA = P.get(p_id);

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

            Organismo o = p.getGene().getOrganismo();
            if (!O.containsKey(o.getNome())) {
                o = organismoRepository.save(
                    new Organismo()
                        .nome(o.getNome())
                        .apelido(o.getNome().toUpperCase().charAt(0) + o.getNome().toLowerCase().substring(1))
                        .sigla(String.join("", Arrays.stream(o.getNome().toUpperCase().split(" ")).map(x -> x.substring(0, 1)).toList()))
                );
                O.put(o.getNome(), o);
                log.info("NOVO organismo {}", o);
            }
            o = O.get(o.getNome());

            if (PROTEINA.getGene() == null) {
                Gene g = p.getGene();
                String g_id = g.getOrganismo().getNome() + " > " + g.getNome();
                if (!G.containsKey(g_id)) {
                    g = geneRepository.save(new Gene().nome(g.getNome()).organismo(o));
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

            proteinaRepository.save(PROTEINA);

            Set<Recurso> recursos = p.getRecursos();
            recursos.addAll(Arrays.stream(BioDBParser.recursos2recursos(p)).toList());
            recursos
                .stream()
                .filter(Objects::nonNull)
                .forEach(recurso -> {
                    if (recurso.getDb() != BioDB.OUTRO) {
                        Recurso _r = X.get(recurso.getUid());
                        if (_r == null) {
                            _r = new Recurso()
                                .uid(recurso.getUid())
                                .db(recurso.getDb())
                                .addProteina(PROTEINA)
                                .link(recurso.getLink() == null ? BioDBParser.recurso2link(recurso) : recurso.getLink());
                            _r = recursoRepository.save(_r);
                            X.put(recurso.getUid(), _r);
                            if (_r.getDb() == BioDB.KEGG) keg.getAndIncrement();
                            else if (_r.getDb() == BioDB.STRINGDB) stg.getAndIncrement();
                        } else {
                            _r.addProteina(PROTEINA);
                            recursoRepository.save(_r);
                        }
                    }
                });

            if ((P.size() < 100) || ((P.size() % 100) == 0)) log.info(
                "Status {} REFS > {} ORGS > {} GENS > {} PTNAS > {} RECS ",
                R.size(),
                O.size(),
                G.size(),
                P.size(),
                X.size()
            );
        }

        log.info("Terminou com {} REFS > {} ORGS > {} GENS > {} PTNAS > {} RECS ", R.size(), O.size(), G.size(), P.size(), X.size());

        log.info("Estendeu RECS: {} => STRING {} KEGG {} encontrados", X.size(), stg.get(), keg.get());

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
            " proteinas."
        );
        return P.size();
    }

    private void makeDownload(Versao versao) throws IOException {
        log.info("Gerando DOWNLOAD para disponibilzar versao {}", versao.getNome());

        ProteinaCriteria proteinaCriteria = new ProteinaCriteria();
        LongFilter longFilter = new LongFilter();
        longFilter.setSpecified(true).setEquals(versao.getId());
        proteinaCriteria.setVersaoId(longFilter);

        List<Proteina> proteinas = proteinaQueryService.findByCriteria(proteinaCriteria, Pageable.unpaged()).stream().toList();
        log.info("Gerando TSV para {} proteinas", proteinas.size());

        ArrayList<String> linhas = new ArrayList<>();
        linhas.add("#\tID\tORGANISM\tGENE\tPROTEIN\tLENGTH\tMASS\tREFERENCE\tENTRY\tCURATOR");
        int cont = 0;
        String vid = "." + versao.getNumero();
        for (Proteina P : proteinas) {
            String proteina = P.getNome();
            String tamanho = P.getTamanho() == null ? "0" : P.getTamanho().toString();
            String massa = P.getMassa() == null ? "0" : P.getMassa();
            String referencias = String.join(" & ", P.getReferencias().stream().sorted().map(Referencia::getCitacao).toList());
            String organismo = P.getGene().getOrganismo().getNome();
            String gene = P.getGene().getNome();
            String recursos = String.join(";", P.getRecursos().stream().sorted().map(Recurso::getUid).toList());
            String curador = P.getCuradoria() == null ? "X" : P.getCuradoria().getId().toString();
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
            if ((linhas.size() < 100) || ((linhas.size() % 100) == 0)) log.info(
                "TOTAL {} registros processados de {} proteinas para o TSV.",
                linhas.size(),
                proteinas.size()
            );
        }

        String dt = new SimpleDateFormat("yyMMdd").format(new Date());
        String f_name = "semprotdb_DEFAULT_V" + versao.getNumero() + "_" + dt + ".tsv";
        Carga file = new TSV(f_name, linhas).zip().toCarga(versao).destino(Destino.DOWNLOAD).ordem(3);
        cargaRepository.save(file);
        versao.addLog("Total " + cont + " registros exportados.");
        log.info("Arquivo TSV ZIP {} gerado para versao {} ! versao sera publicada com {} registros. ", file, versao.getNome(), cont);
    }

    public void atualizarDownloadFile() {
        log.info("Atualizando arquivos de DOWNLOAD");

        String v = "?";
        int totalc = 0;
        try {
            Set<Versao> vs = versaoRepository
                .findAll()
                .stream()
                .filter(_v -> _v.getStatus() == Status.PROCESSADO)
                .collect(Collectors.toSet());
            for (Versao versao : vs) {
                totalc += (int) versao.getCargas().stream().filter(c -> c.getDestino() == Destino.DOWNLOAD).count();
                v = versao.identfy();
                log.info("Gerar arquivos para versao {} de {} versoes", v, vs.size());
                makeDownload(versao);
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
                    carga1.getStatus() == null ||
                    !carga1.getChecksum().contains("|") ||
                    carga2.getStatus() == null ||
                    !carga2.getStatus().contains("|")
                ) continue;
                String c1MD5 = carga1.getStatus().split("\\|")[0];
                String c2MD5 = carga2.getStatus().split("\\|")[0];
                if (!c1MD5.equals(c2MD5)) continue; /// MD5 == MD5
                // duas cargas com conteudo igual
                // apagar a que possui o maior nome
                if (carga1.getNome().compareTo(carga2.getNome()) > 0) {
                    apagar.add(carga1);
                }
            }
        }

        if (!apagar.isEmpty()) {
            log.info("Apagando {} cargas duplicadas: {}", apagar.size(), apagar);
            cargaRepository.deleteAll(apagar);
        }

        log.info("Finalizado processamento.");
    }

    public void removeVersao(Versao versao) {
        try {
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

            Set<Long> rec_vazio_ids = recursoRepository
                .findAllByProteinasIsEmpty()
                .stream()
                .map(Recurso::getId)
                .collect(Collectors.toSet());
            log.info("REMOVENDO {} recursos", rec_vazio_ids.size());
            recursoRepository.deleteAllByIdInBatch(rec_vazio_ids);

            log.info("REMOVENDO {} proteinas", proteinas.size());
            proteinaRepository.saveAll(proteinas);
            proteinaRepository.deleteAll(proteinas);

            log.info("REMOVENDO versao {}", versao.identfy());
            versaoRepository.delete(versao);

            /// remover genes nulos
            Set<Long> gene_ids = geneRepository.findAllByProteinasIsEmpty().stream().map(Gene::getId).collect(Collectors.toSet());
            log.info("REMOVENDO {} genes", gene_ids.size());
            geneRepository.deleteAllByIdInBatch(gene_ids);

            /// remover organismo nulos
            Set<Long> organismos_vazios = organismoRepository
                .findAllByGenesIsEmpty()
                .stream()
                .map(Organismo::getId)
                .collect(Collectors.toSet());
            log.info("REMOVENDO {} organismos", organismos_vazios.size());
            organismoRepository.deleteAllByIdInBatch(organismos_vazios);

            /// remover referencias nulos
            Set<Long> referencias_vazios = referenciaRepository
                .findAllByProteinasIsEmpty()
                .stream()
                .map(Referencia::getId)
                .collect(Collectors.toSet());
            log.info("REMOVENDO {} referencias", referencias_vazios.size());
            referenciaRepository.deleteAllByIdInBatch(referencias_vazios);
        } catch (Exception e) {
            log.error("FALHA REMOVENDO {} {} ", versao.identfy(), e.toString());
        } finally {
            log.info("Terminou remover versao {}", versao.identfy());
        }
    }
}
