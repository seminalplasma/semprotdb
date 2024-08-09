package org.semprotdb.service;

import java.util.Optional;
import org.semprotdb.domain.Versao;
import org.semprotdb.repository.VersaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.semprotdb.domain.Versao}.
 */
@Service
@Transactional
public class VersaoService {

    private static final Logger log = LoggerFactory.getLogger(VersaoService.class);

    private final VersaoRepository versaoRepository;

    public VersaoService(VersaoRepository versaoRepository) {
        this.versaoRepository = versaoRepository;
    }

    /**
     * Save a versao.
     *
     * @param versao the entity to save.
     * @return the persisted entity.
     */
    public Versao save(Versao versao) {
        log.debug("Request to save Versao : {}", versao);
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
                    existingVersao.setStatus(versao.getStatus());
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
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Versao : {}", id);
        versaoRepository.deleteById(id);
    }
}
