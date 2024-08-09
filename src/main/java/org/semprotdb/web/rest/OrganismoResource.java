package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.semprotdb.domain.Organismo;
import org.semprotdb.repository.OrganismoRepository;
import org.semprotdb.service.OrganismoQueryService;
import org.semprotdb.service.OrganismoService;
import org.semprotdb.service.criteria.OrganismoCriteria;
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
 * REST controller for managing {@link org.semprotdb.domain.Organismo}.
 */
@RestController
@RequestMapping("/api/organismos")
public class OrganismoResource {

    private static final Logger log = LoggerFactory.getLogger(OrganismoResource.class);

    private static final String ENTITY_NAME = "organismo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrganismoService organismoService;

    private final OrganismoRepository organismoRepository;

    private final OrganismoQueryService organismoQueryService;

    public OrganismoResource(
        OrganismoService organismoService,
        OrganismoRepository organismoRepository,
        OrganismoQueryService organismoQueryService
    ) {
        this.organismoService = organismoService;
        this.organismoRepository = organismoRepository;
        this.organismoQueryService = organismoQueryService;
    }

    /**
     * {@code POST  /organismos} : Create a new organismo.
     *
     * @param organismo the organismo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organismo, or with status {@code 400 (Bad Request)} if the organismo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Organismo> createOrganismo(@Valid @RequestBody Organismo organismo) throws URISyntaxException {
        log.debug("REST request to save Organismo : {}", organismo);
        if (organismo.getId() != null) {
            throw new BadRequestAlertException("A new organismo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        organismo = organismoService.save(organismo);
        return ResponseEntity.created(new URI("/api/organismos/" + organismo.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, organismo.getId().toString()))
            .body(organismo);
    }

    /**
     * {@code PUT  /organismos/:id} : Updates an existing organismo.
     *
     * @param id the id of the organismo to save.
     * @param organismo the organismo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organismo,
     * or with status {@code 400 (Bad Request)} if the organismo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the organismo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Organismo> updateOrganismo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Organismo organismo
    ) throws URISyntaxException {
        log.debug("REST request to update Organismo : {}, {}", id, organismo);
        if (organismo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organismo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organismoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        organismo = organismoService.update(organismo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organismo.getId().toString()))
            .body(organismo);
    }

    /**
     * {@code PATCH  /organismos/:id} : Partial updates given fields of an existing organismo, field will ignore if it is null
     *
     * @param id the id of the organismo to save.
     * @param organismo the organismo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organismo,
     * or with status {@code 400 (Bad Request)} if the organismo is not valid,
     * or with status {@code 404 (Not Found)} if the organismo is not found,
     * or with status {@code 500 (Internal Server Error)} if the organismo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Organismo> partialUpdateOrganismo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Organismo organismo
    ) throws URISyntaxException {
        log.debug("REST request to partial update Organismo partially : {}, {}", id, organismo);
        if (organismo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organismo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organismoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Organismo> result = organismoService.partialUpdate(organismo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organismo.getId().toString())
        );
    }

    /**
     * {@code GET  /organismos} : get all the organismos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of organismos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Organismo>> getAllOrganismos(
        OrganismoCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Organismos by criteria: {}", criteria);

        Page<Organismo> page = organismoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /organismos/count} : count all the organismos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOrganismos(OrganismoCriteria criteria) {
        log.debug("REST request to count Organismos by criteria: {}", criteria);
        return ResponseEntity.ok().body(organismoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /organismos/:id} : get the "id" organismo.
     *
     * @param id the id of the organismo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organismo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Organismo> getOrganismo(@PathVariable("id") Long id) {
        log.debug("REST request to get Organismo : {}", id);
        Optional<Organismo> organismo = organismoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(organismo);
    }

    /**
     * {@code DELETE  /organismos/:id} : delete the "id" organismo.
     *
     * @param id the id of the organismo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganismo(@PathVariable("id") Long id) {
        log.debug("REST request to delete Organismo : {}", id);
        organismoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
