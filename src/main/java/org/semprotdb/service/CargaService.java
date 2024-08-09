package org.semprotdb.service;

import java.util.Optional;
import org.semprotdb.domain.Carga;
import org.semprotdb.repository.CargaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        return cargaRepository.save(carga);
    }

    /**
     * Update a carga.
     *
     * @param carga the entity to save.
     * @return the persisted entity.
     */
    public Carga update(Carga carga) {
        log.debug("Request to update Carga : {}", carga);
        return cargaRepository.save(carga);
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
            .map(cargaRepository::save);
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
}
