package org.semprotdb.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import org.semprotdb.domain.Proteina;
import org.semprotdb.domain.Recurso;
import org.semprotdb.repository.ProteinaRepository;
import org.semprotdb.repository.RecursoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.semprotdb.domain.Proteina}.
 */
@Service
@Transactional
public class ProteinaService {

    private static final Logger log = LoggerFactory.getLogger(ProteinaService.class);

    private final ProteinaRepository proteinaRepository;
    private final RecursoRepository recursoRepository;

    public ProteinaService(ProteinaRepository proteinaRepository, RecursoRepository recursoRepository) {
        this.proteinaRepository = proteinaRepository;
        this.recursoRepository = recursoRepository;
    }

    /**
     * Save a proteina.
     *
     * @param proteina the entity to save.
     * @return the persisted entity.
     */
    public Proteina save(Proteina proteina) {
        log.debug("Request to save Proteina : {}", proteina);
        return proteinaRepository.save(proteina);
    }

    /**
     * Update a proteina.
     *
     * @param proteina the entity to save.
     * @return the persisted entity.
     */
    public Proteina update(Proteina proteina) {
        log.debug("Request to update Proteina : {}", proteina);
        return proteinaRepository.save(proteina);
    }

    /**
     * Partially update a proteina.
     *
     * @param proteina the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Proteina> partialUpdate(Proteina proteina) {
        log.debug("Request to partially update Proteina : {}", proteina);

        return proteinaRepository
            .findById(proteina.getId())
            .map(existingProteina -> {
                if (proteina.getNome() != null) {
                    existingProteina.setNome(proteina.getNome());
                }
                if (proteina.getTamanho() != null) {
                    existingProteina.setTamanho(proteina.getTamanho());
                }
                if (proteina.getMassa() != null) {
                    existingProteina.setMassa(proteina.getMassa());
                }
                if (proteina.getDescricao() != null) {
                    existingProteina.setDescricao(proteina.getDescricao());
                }

                return existingProteina;
            })
            .map(proteinaRepository::save);
    }

    /**
     * Get all the proteinas with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Proteina> findAllWithEagerRelationships(Pageable pageable) {
        return proteinaRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one proteina by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Proteina> findOne(Long id) {
        log.debug("Request to get Proteina : {}", id);
        return proteinaRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the proteina by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Proteina : {}", id);
        proteinaRepository
            .findById(id)
            .ifPresent(p -> {
                final HashSet<Recurso> recursos = new HashSet<>(p.getRecursos());
                recursos.forEach(r -> r.removeProteina(p));
                recursoRepository.saveAll(recursos);
            });
        proteinaRepository.deleteById(id);
    }
}
