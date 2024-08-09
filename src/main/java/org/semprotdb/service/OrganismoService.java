package org.semprotdb.service;

import java.util.Optional;
import org.semprotdb.domain.Organismo;
import org.semprotdb.repository.OrganismoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.semprotdb.domain.Organismo}.
 */
@Service
@Transactional
public class OrganismoService {

    private static final Logger log = LoggerFactory.getLogger(OrganismoService.class);

    private final OrganismoRepository organismoRepository;

    public OrganismoService(OrganismoRepository organismoRepository) {
        this.organismoRepository = organismoRepository;
    }

    /**
     * Save a organismo.
     *
     * @param organismo the entity to save.
     * @return the persisted entity.
     */
    public Organismo save(Organismo organismo) {
        log.debug("Request to save Organismo : {}", organismo);
        return organismoRepository.save(organismo);
    }

    /**
     * Update a organismo.
     *
     * @param organismo the entity to save.
     * @return the persisted entity.
     */
    public Organismo update(Organismo organismo) {
        log.debug("Request to update Organismo : {}", organismo);
        return organismoRepository.save(organismo);
    }

    /**
     * Partially update a organismo.
     *
     * @param organismo the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Organismo> partialUpdate(Organismo organismo) {
        log.debug("Request to partially update Organismo : {}", organismo);

        return organismoRepository
            .findById(organismo.getId())
            .map(existingOrganismo -> {
                if (organismo.getNome() != null) {
                    existingOrganismo.setNome(organismo.getNome());
                }
                if (organismo.getSigla() != null) {
                    existingOrganismo.setSigla(organismo.getSigla());
                }
                if (organismo.getApelido() != null) {
                    existingOrganismo.setApelido(organismo.getApelido());
                }
                if (organismo.getSilhueta() != null) {
                    existingOrganismo.setSilhueta(organismo.getSilhueta());
                }
                if (organismo.getSilhuetaContentType() != null) {
                    existingOrganismo.setSilhuetaContentType(organismo.getSilhuetaContentType());
                }
                if (organismo.getIcone() != null) {
                    existingOrganismo.setIcone(organismo.getIcone());
                }
                if (organismo.getPos() != null) {
                    existingOrganismo.setPos(organismo.getPos());
                }
                if (organismo.getImagem() != null) {
                    existingOrganismo.setImagem(organismo.getImagem());
                }
                if (organismo.getDescricao() != null) {
                    existingOrganismo.setDescricao(organismo.getDescricao());
                }

                return existingOrganismo;
            })
            .map(organismoRepository::save);
    }

    /**
     * Get one organismo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Organismo> findOne(Long id) {
        log.debug("Request to get Organismo : {}", id);
        return organismoRepository.findById(id);
    }

    /**
     * Delete the organismo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Organismo : {}", id);
        organismoRepository.deleteById(id);
    }
}
