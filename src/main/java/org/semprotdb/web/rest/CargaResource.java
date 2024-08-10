package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.repository.CargaRepository;
import org.semprotdb.service.CargaQueryService;
import org.semprotdb.service.CargaService;
import org.semprotdb.service.criteria.CargaCriteria;
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
 * REST controller for managing {@link org.semprotdb.domain.Carga}.
 */
@RestController
@RequestMapping("/api/cargas")
public class CargaResource {

    private static final Logger log = LoggerFactory.getLogger(CargaResource.class);

    private static final String ENTITY_NAME = "carga";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CargaService cargaService;

    private final CargaRepository cargaRepository;

    private final CargaQueryService cargaQueryService;

    public CargaResource(CargaService cargaService, CargaRepository cargaRepository, CargaQueryService cargaQueryService) {
        this.cargaService = cargaService;
        this.cargaRepository = cargaRepository;
        this.cargaQueryService = cargaQueryService;
    }

    /**
     * {@code POST  /cargas} : Create a new carga.
     *
     * @param carga the carga to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carga, or with status {@code 400 (Bad Request)} if the carga has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Carga> createCarga(@Valid @RequestBody Carga carga) throws URISyntaxException {
        log.debug("REST request to save Carga : {}", carga);
        if (carga.getId() != null) {
            throw new BadRequestAlertException("A new carga cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carga = cargaService.save(carga);
        return ResponseEntity.created(new URI("/api/cargas/" + carga.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carga.getId().toString()))
            .body(carga);
    }

    /**
     * {@code PUT  /cargas/:id} : Updates an existing carga.
     *
     * @param id the id of the carga to save.
     * @param carga the carga to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carga,
     * or with status {@code 400 (Bad Request)} if the carga is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carga couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Carga> updateCarga(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Carga carga)
        throws URISyntaxException {
        log.debug("REST request to update Carga : {}, {}", id, carga);
        if (carga.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carga.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cargaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carga = cargaService.update(carga);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carga.getId().toString()))
            .body(carga);
    }

    /**
     * {@code PATCH  /cargas/:id} : Partial updates given fields of an existing carga, field will ignore if it is null
     *
     * @param id the id of the carga to save.
     * @param carga the carga to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carga,
     * or with status {@code 400 (Bad Request)} if the carga is not valid,
     * or with status {@code 404 (Not Found)} if the carga is not found,
     * or with status {@code 500 (Internal Server Error)} if the carga couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Carga> partialUpdateCarga(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Carga carga
    ) throws URISyntaxException {
        log.debug("REST request to partial update Carga partially : {}, {}", id, carga);
        if (carga.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carga.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cargaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Carga> result = cargaService.partialUpdate(carga);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carga.getId().toString())
        );
    }

    /**
     * {@code GET  /cargas} : get all the cargas.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cargas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Carga>> getAllCargas(
        CargaCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Cargas by criteria: {}", criteria);

        Page<Carga> page = cargaQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cargas/count} : count all the cargas.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCargas(CargaCriteria criteria) {
        log.debug("REST request to count Cargas by criteria: {}", criteria);
        return ResponseEntity.ok().body(cargaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /cargas/:id} : get the "id" carga.
     *
     * @param id the id of the carga to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carga, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Carga> getCarga(@PathVariable("id") Long id) {
        log.debug("REST request to get Carga : {}", id);
        Optional<Carga> carga = cargaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carga);
    }

    /**
     * {@code DELETE  /cargas/:id} : delete the "id" carga.
     *
     * @param id the id of the carga to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarga(@PathVariable("id") Long id) {
        log.debug("REST request to delete Carga : {}", id);
        cargaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
