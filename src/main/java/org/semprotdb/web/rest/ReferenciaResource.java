package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.semprotdb.domain.Referencia;
import org.semprotdb.repository.ReferenciaRepository;
import org.semprotdb.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.semprotdb.domain.Referencia}.
 */
@RestController
@RequestMapping("/api/referencias")
@Transactional
public class ReferenciaResource {

    private static final Logger log = LoggerFactory.getLogger(ReferenciaResource.class);

    private static final String ENTITY_NAME = "referencia";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReferenciaRepository referenciaRepository;

    public ReferenciaResource(ReferenciaRepository referenciaRepository) {
        this.referenciaRepository = referenciaRepository;
    }

    /**
     * {@code POST  /referencias} : Create a new referencia.
     *
     * @param referencia the referencia to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new referencia, or with status {@code 400 (Bad Request)} if the referencia has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Referencia> createReferencia(@Valid @RequestBody Referencia referencia) throws URISyntaxException {
        log.debug("REST request to save Referencia : {}", referencia);
        if (referencia.getId() != null) {
            throw new BadRequestAlertException("A new referencia cannot already have an ID", ENTITY_NAME, "idexists");
        }
        referencia = referenciaRepository.save(referencia);
        return ResponseEntity.created(new URI("/api/referencias/" + referencia.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, referencia.getId().toString()))
            .body(referencia);
    }

    /**
     * {@code PUT  /referencias/:id} : Updates an existing referencia.
     *
     * @param id the id of the referencia to save.
     * @param referencia the referencia to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated referencia,
     * or with status {@code 400 (Bad Request)} if the referencia is not valid,
     * or with status {@code 500 (Internal Server Error)} if the referencia couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Referencia> updateReferencia(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Referencia referencia
    ) throws URISyntaxException {
        log.debug("REST request to update Referencia : {}, {}", id, referencia);
        if (referencia.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, referencia.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referenciaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        referencia = referenciaRepository.save(referencia);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, referencia.getId().toString()))
            .body(referencia);
    }

    /**
     * {@code PATCH  /referencias/:id} : Partial updates given fields of an existing referencia, field will ignore if it is null
     *
     * @param id the id of the referencia to save.
     * @param referencia the referencia to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated referencia,
     * or with status {@code 400 (Bad Request)} if the referencia is not valid,
     * or with status {@code 404 (Not Found)} if the referencia is not found,
     * or with status {@code 500 (Internal Server Error)} if the referencia couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Referencia> partialUpdateReferencia(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Referencia referencia
    ) throws URISyntaxException {
        log.debug("REST request to partial update Referencia partially : {}, {}", id, referencia);
        if (referencia.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, referencia.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!referenciaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Referencia> result = referenciaRepository
            .findById(referencia.getId())
            .map(existingReferencia -> {
                if (referencia.getCitacao() != null) {
                    existingReferencia.setCitacao(referencia.getCitacao());
                }
                if (referencia.getLink() != null) {
                    existingReferencia.setLink(referencia.getLink());
                }
                if (referencia.getAno() != null) {
                    existingReferencia.setAno(referencia.getAno());
                }
                if (referencia.getAutores() != null) {
                    existingReferencia.setAutores(referencia.getAutores());
                }

                return existingReferencia;
            })
            .map(referenciaRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, referencia.getId().toString())
        );
    }

    /**
     * {@code GET  /referencias} : get all the referencias.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of referencias in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Referencia>> getAllReferencias(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Referencias");
        Page<Referencia> page = referenciaRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /referencias/:id} : get the "id" referencia.
     *
     * @param id the id of the referencia to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the referencia, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Referencia> getReferencia(@PathVariable("id") Long id) {
        log.debug("REST request to get Referencia : {}", id);
        Optional<Referencia> referencia = referenciaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(referencia);
    }

    /**
     * {@code DELETE  /referencias/:id} : delete the "id" referencia.
     *
     * @param id the id of the referencia to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReferencia(@PathVariable("id") Long id) {
        log.debug("REST request to delete Referencia : {}", id);
        referenciaRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
