package org.semprotdb.service;

import java.util.Objects;
import java.util.Optional;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Status;
import org.semprotdb.repository.CargaRepository;
import org.semprotdb.util.DataModelTabela;
import org.semprotdb.util.DataModelUniprot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.semprotdb.domain.Carga}.
 */
@Service
@Transactional
public class CargaService {

    private static final Logger log = LoggerFactory.getLogger(CargaService.class);

    private final CargaRepository cargaRepository;

    public CargaService(CargaRepository cargaRepository) {
        this.cargaRepository = cargaRepository;
    }

    /**
     * Save a carga.
     *
     * @param carga the entity to save.
     * @return the persisted entity.
     */
    public Carga save(Carga carga) {
        log.debug("Request to save Carga : {}", carga);

        carga.setDestino(Destino.OUTRO);
        carga.setStatus("Não verificado");
        carga.setValidado(false);
        carga.setOrdem(2);

        Carga _carga = cargaRepository.save(carga);
        this.processarCarga(_carga.getId());
        return cargaRepository.save(_carga);
    }

    /**
     * Update a carga.
     *
     * @param carga the entity to save.
     * @return the persisted entity.
     */
    public Carga update(Carga carga) {
        log.debug("Request to update Carga : {}", carga);
        Carga _carga = cargaRepository.save(carga);
        this.processarCarga(_carga.getId());
        return _carga;
    }

    /**
     * Partially update a carga.
     *
     * @param carga the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Carga> partialUpdate(Carga carga) {
        log.debug("Request to partially update Carga : {}", carga);

        return cargaRepository
            .findById(carga.getId())
            .map(existingCarga -> {
                if (carga.getStatus() != null) {
                    existingCarga.setStatus(carga.getStatus());
                }
                if (carga.getOrdem() != null) {
                    existingCarga.setOrdem(carga.getOrdem());
                }
                if (carga.getPlanilha() != null) {
                    existingCarga.setPlanilha(carga.getPlanilha());
                }
                if (carga.getPlanilhaContentType() != null) {
                    existingCarga.setPlanilhaContentType(carga.getPlanilhaContentType());
                }
                if (carga.getNome() != null) {
                    existingCarga.setNome(carga.getNome());
                }
                if (carga.getCaminho() != null) {
                    existingCarga.setCaminho(carga.getCaminho());
                }
                if (carga.getValidado() != null) {
                    existingCarga.setValidado(carga.getValidado());
                }
                if (carga.getTipo() != null) {
                    existingCarga.setTipo(carga.getTipo());
                }
                if (carga.getFormato() != null) {
                    existingCarga.setFormato(carga.getFormato());
                }
                if (carga.getDestino() != null) {
                    existingCarga.setDestino(carga.getDestino());
                }
                if (carga.getLinhas() != null) {
                    existingCarga.setLinhas(carga.getLinhas());
                }
                if (carga.getChecksum() != null) {
                    existingCarga.setChecksum(carga.getChecksum());
                }

                return existingCarga;
            })
            .map(cargaRepository::save)
            .map(c -> {
                processarCarga(c.getId());
                return c;
            });
    }

    /**
     * Get one carga by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Carga> findOne(Long id) {
        log.debug("Request to get Carga : {}", id);
        return cargaRepository.findById(id);
    }

    /**
     * Delete the carga by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Carga : {}", id);
        cargaRepository.deleteById(id);
    }

    ///@Scheduled(cron = "1 * * * * ?")
    @Async
    public void processarCarga(Long cargaId) {
        Carga carga = findOne(cargaId).orElse(null);
        if (carga == null) return;

        /// ORDEM
        /// 0 - outros
        /// 1 - invalido
        //  2 - nova carga
        /// 3 - downloads
        /// 4 - upload
        /// 5 - dados, metadata
        /// 6 -
        /// 7 -
        /// 8 -
        /// 9 -

        if (null != carga.getVersao() && carga.getVersao().getStatus().ordinal() >= Status.DISPONIVEL.ordinal()) {
            log.info("Carga carga {} {} sera tratada como DOWNLOAD.", carga.getId(), carga.getNome());
            carga.setDestino(Destino.DOWNLOAD);
            carga.setValidado(true);
            carga.setOrdem(3);
            carga.setStatus("Arquivo para download.");
        }

        log.info("Verificando carga {} ", carga);

        if (carga.getValidado()) return;

        try {
            //// verificar se é carga de dados
            DataModelTabela dataModelTabela = new DataModelTabela(carga);
            if (carga.validado(dataModelTabela.validar(5) && !dataModelTabela.asProteinas().isEmpty()).getValidado()) return;

            /// verificar se é carga de mapeamento
            DataModelUniprot dataModelUniprot = new DataModelUniprot(carga);
            if (carga.validado(dataModelUniprot.validar(5) && !dataModelUniprot.asProteinas().isEmpty()).getValidado()) return;

            carga.setLinhas(0);
            carga.setDestino(Destino.OUTRO);
            carga.setValidado(false);
            carga.setOrdem(0);
            carga.setStatus("Arquivo desconhecido.");
            log.warn("Enviado CARGA de arquivo desconhecido: {}", carga);
        } catch (Exception e) {
            String msg = e.getMessage();
            carga.validado(false).setStatus(msg.length() > 200 ? msg.substring(0, 200) : msg);
            log.error("Falhou ao validar CARGA " + carga.getId(), e);
        } finally {
            cargaRepository.save(carga);
            log.info("Finalizou validaçao CARGA: {}", carga);
        }
    }
}
