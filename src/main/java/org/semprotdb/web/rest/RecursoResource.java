package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.semprotdb.domain.Proteina;
import org.semprotdb.domain.Recurso;
import org.semprotdb.repository.ProteinaRepository;
import org.semprotdb.repository.RecursoRepository;
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
 * REST controller for managing {@link org.semprotdb.domain.Recurso}.
 */
@RestController
@RequestMapping("/api/recursos")
@Transactional
public class RecursoResource {

    private static final Logger log = LoggerFactory.getLogger(RecursoResource.class);

    private static final String ENTITY_NAME = "recurso";
    private final ProteinaRepository proteinaRepository;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecursoRepository recursoRepository;

    public RecursoResource(RecursoRepository recursoRepository, ProteinaRepository proteinaRepository) {
        this.recursoRepository = recursoRepository;
        this.proteinaRepository = proteinaRepository;
    }

    /**
     * {@code POST  /recursos} : Create a new recurso.
     *
     * @param recurso the recurso to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recurso, or with status {@code 400 (Bad Request)} if the recurso has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Recurso> createRecurso(@Valid @RequestBody Recurso recurso) throws URISyntaxException {
        log.debug("REST request to save Recurso : {}", recurso);
        if (recurso.getId() != null) {
            throw new BadRequestAlertException("A new recurso cannot already have an ID", ENTITY_NAME, "idexists");
        }
        recurso = recursoRepository.save(recurso);
        return ResponseEntity.created(new URI("/api/recursos/" + recurso.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, recurso.getId().toString()))
            .body(recurso);
    }

    /**
     * {@code PUT  /recursos/:id} : Updates an existing recurso.
     *
     * @param id the id of the recurso to save.
     * @param recurso the recurso to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recurso,
     * or with status {@code 400 (Bad Request)} if the recurso is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recurso couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Recurso> updateRecurso(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Recurso recurso
    ) throws URISyntaxException {
        log.debug("REST request to update Recurso : {}, {}", id, recurso);
        if (recurso.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recurso.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recursoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        recurso = recursoRepository.save(recurso);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recurso.getId().toString()))
            .body(recurso);
    }

    /**
     * {@code PATCH  /recursos/:id} : Partial updates given fields of an existing recurso, field will ignore if it is null
     *
     * @param id the id of the recurso to save.
     * @param recurso the recurso to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recurso,
     * or with status {@code 400 (Bad Request)} if the recurso is not valid,
     * or with status {@code 404 (Not Found)} if the recurso is not found,
     * or with status {@code 500 (Internal Server Error)} if the recurso couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Recurso> partialUpdateRecurso(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Recurso recurso
    ) throws URISyntaxException {
        log.debug("REST request to partial update Recurso partially : {}, {}", id, recurso);
        if (recurso.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recurso.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recursoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Recurso> result = recursoRepository
            .findById(recurso.getId())
            .map(existingRecurso -> {
                if (recurso.getUid() != null) {
                    existingRecurso.setUid(recurso.getUid());
                }
                if (recurso.getDb() != null) {
                    existingRecurso.setDb(recurso.getDb());
                }
                if (recurso.getLink() != null) {
                    existingRecurso.setLink(recurso.getLink());
                }

                return existingRecurso;
            })
            .map(recursoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recurso.getId().toString())
        );
    }

    /**
     * {@code GET  /recursos} : get all the recursos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recursos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Recurso>> getAllRecursos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Recursos");
        Page<Recurso> page = recursoRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /recursos/:id} : get the "id" recurso.
     *
     * @param id the id of the recurso to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recurso, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recurso> getRecurso(@PathVariable("id") Long id) {
        log.debug("REST request to get Recurso : {}", id);
        Optional<Recurso> recurso = recursoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(recurso);
    }

    /**
     * {@code DELETE  /recursos/:id} : delete the "id" recurso.
     *
     * @param id the id of the recurso to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurso(@PathVariable("id") Long id) {
        log.debug("REST request to delete Recurso : {}", id);

        recursoRepository
            .findById(id)
            .ifPresent(r -> {
                final HashSet<Proteina> proteinas = new HashSet<>(r.getProteinas());
                proteinas.forEach(p -> p.removeRecurso(r));
                proteinaRepository.saveAll(proteinas);
            });

        recursoRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
