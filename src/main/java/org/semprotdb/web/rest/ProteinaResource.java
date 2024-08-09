package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.semprotdb.domain.Proteina;
import org.semprotdb.repository.ProteinaRepository;
import org.semprotdb.service.ProteinaQueryService;
import org.semprotdb.service.ProteinaService;
import org.semprotdb.service.criteria.ProteinaCriteria;
import org.semprotdb.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.semprotdb.domain.Proteina}.
 */
@RestController
@RequestMapping("/api/proteinas")
public class ProteinaResource {

    private static final Logger log = LoggerFactory.getLogger(ProteinaResource.class);

    private static final String ENTITY_NAME = "proteina";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProteinaService proteinaService;

    private final ProteinaRepository proteinaRepository;

    private final ProteinaQueryService proteinaQueryService;

    public ProteinaResource(
        ProteinaService proteinaService,
        ProteinaRepository proteinaRepository,
        ProteinaQueryService proteinaQueryService
    ) {
        this.proteinaService = proteinaService;
        this.proteinaRepository = proteinaRepository;
        this.proteinaQueryService = proteinaQueryService;
    }

    /**
     * {@code POST  /proteinas} : Create a new proteina.
     *
     * @param proteina the proteina to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new proteina, or with status {@code 400 (Bad Request)} if the proteina has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Proteina> createProteina(@Valid @RequestBody Proteina proteina) throws URISyntaxException {
        log.debug("REST request to save Proteina : {}", proteina);
        if (proteina.getId() != null) {
            throw new BadRequestAlertException("A new proteina cannot already have an ID", ENTITY_NAME, "idexists");
        }
        proteina = proteinaService.save(proteina);
        return ResponseEntity.created(new URI("/api/proteinas/" + proteina.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, proteina.getId().toString()))
            .body(proteina);
    }

    /**
     * {@code PUT  /proteinas/:id} : Updates an existing proteina.
     *
     * @param id the id of the proteina to save.
     * @param proteina the proteina to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proteina,
     * or with status {@code 400 (Bad Request)} if the proteina is not valid,
     * or with status {@code 500 (Internal Server Error)} if the proteina couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Proteina> updateProteina(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Proteina proteina
    ) throws URISyntaxException {
        log.debug("REST request to update Proteina : {}, {}", id, proteina);
        if (proteina.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proteina.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proteinaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        proteina = proteinaService.update(proteina);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proteina.getId().toString()))
            .body(proteina);
    }

    /**
     * {@code PATCH  /proteinas/:id} : Partial updates given fields of an existing proteina, field will ignore if it is null
     *
     * @param id the id of the proteina to save.
     * @param proteina the proteina to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proteina,
     * or with status {@code 400 (Bad Request)} if the proteina is not valid,
     * or with status {@code 404 (Not Found)} if the proteina is not found,
     * or with status {@code 500 (Internal Server Error)} if the proteina couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Proteina> partialUpdateProteina(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Proteina proteina
    ) throws URISyntaxException {
        log.debug("REST request to partial update Proteina partially : {}, {}", id, proteina);
        if (proteina.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proteina.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proteinaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Proteina> result = proteinaService.partialUpdate(proteina);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proteina.getId().toString())
        );
    }

    /**
     * {@code GET  /proteinas} : get all the proteinas.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of proteinas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Proteina>> getAllProteinas(
        ProteinaCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Proteinas by criteria: {}", criteria);

        Page<Proteina> page = proteinaQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /proteinas/count} : count all the proteinas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProteinas(ProteinaCriteria criteria) {
        log.debug("REST request to count Proteinas by criteria: {}", criteria);
        return ResponseEntity.ok().body(proteinaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /proteinas/:id} : get the "id" proteina.
     *
     * @param id the id of the proteina to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the proteina, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Proteina> getProteina(@PathVariable("id") Long id) {
        log.debug("REST request to get Proteina : {}", id);
        Optional<Proteina> proteina = proteinaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(proteina);
    }

    /**
     * {@code DELETE  /proteinas/:id} : delete the "id" proteina.
     *
     * @param id the id of the proteina to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProteina(@PathVariable("id") Long id) {
        log.debug("REST request to delete Proteina : {}", id);
        proteinaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
