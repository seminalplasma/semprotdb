package org.semprotdb.service;

import java.util.Optional;
import org.semprotdb.domain.Gene;
import org.semprotdb.repository.GeneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link org.semprotdb.domain.Gene}.
 */
@Service
@Transactional
public class GeneService {

    private static final Logger log = LoggerFactory.getLogger(GeneService.class);

    private final GeneRepository geneRepository;

    public GeneService(GeneRepository geneRepository) {
        this.geneRepository = geneRepository;
    }

    /**
     * Save a gene.
     *
     * @param gene the entity to save.
     * @return the persisted entity.
     */
    public Gene save(Gene gene) {
        log.debug("Request to save Gene : {}", gene);
        return geneRepository.save(gene);
    }

    /**
     * Update a gene.
     *
     * @param gene the entity to save.
     * @return the persisted entity.
     */
    public Gene update(Gene gene) {
        log.debug("Request to update Gene : {}", gene);
        return geneRepository.save(gene);
    }

    /**
     * Partially update a gene.
     *
     * @param gene the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Gene> partialUpdate(Gene gene) {
        log.debug("Request to partially update Gene : {}", gene);

        return geneRepository
            .findById(gene.getId())
            .map(existingGene -> {
                if (gene.getNome() != null) {
                    existingGene.setNome(gene.getNome());
                }
                if (gene.getDescricao() != null) {
                    existingGene.setDescricao(gene.getDescricao());
                }

                return existingGene;
            })
            .map(geneRepository::save);
    }

    /**
     * Get one gene by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Gene> findOne(Long id) {
        log.debug("Request to get Gene : {}", id);
        return geneRepository.findById(id);
    }

    /**
     * Delete the gene by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Gene : {}", id);
        geneRepository.deleteById(id);
    }
}
