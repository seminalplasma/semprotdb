package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.semprotdb.domain.Versao;
import org.semprotdb.domain.enumeration.Status;
import org.semprotdb.repository.VersaoRepository;
import org.semprotdb.service.UserService;
import org.semprotdb.service.VersaoQueryService;
import org.semprotdb.service.VersaoService;
import org.semprotdb.service.criteria.VersaoCriteria;
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
import tech.jhipster.service.filter.Filter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.semprotdb.domain.Versao}.
 */
@RestController
@RequestMapping("/api/versaos")
public class VersaoResource {

    private static final Logger log = LoggerFactory.getLogger(VersaoResource.class);

    private static final String ENTITY_NAME = "versao";
    private final UserService userService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VersaoService versaoService;

    private final VersaoRepository versaoRepository;

    private final VersaoQueryService versaoQueryService;

    private final VersaoCriteria.StatusFilter STATUS_PUBLIC_FILTER;

    public VersaoResource(
        VersaoService versaoService,
        VersaoRepository versaoRepository,
        VersaoQueryService versaoQueryService,
        UserService userService
    ) {
        this.versaoService = versaoService;
        this.versaoRepository = versaoRepository;
        this.versaoQueryService = versaoQueryService;
        this.STATUS_PUBLIC_FILTER = new VersaoCriteria.StatusFilter();
        this.STATUS_PUBLIC_FILTER.setSpecified(true).setIn(Arrays.asList(Status.DISPONIVEL, Status.OCULTO));
        this.userService = userService;
    }

    /**
     * {@code POST  /versaos} : Create a new versao.
     *
     * @param versao the versao to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new versao, or with status {@code 400 (Bad Request)} if the versao has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Versao> createVersao(@Valid @RequestBody Versao versao) throws URISyntaxException {
        log.debug("REST request to save Versao : {}", versao);
        if (versao.getId() != null) {
            throw new BadRequestAlertException("A new versao cannot already have an ID", ENTITY_NAME, "idexists");
        }
        versao.setStatus(Status.CRIADO);
        versao.setRelease(versao.getRelease() == null ? new Date().toInstant() : versao.getRelease());
        versao.setTexto(versao.getDetalhes() == null ? versao.toString() : versao.getTexto());
        versao = versaoService.save(versao);
        return ResponseEntity.created(new URI("/api/versaos/" + versao.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, versao.getId().toString()))
            .body(versao);
    }

    /**
     * {@code PUT  /versaos/:id} : Updates an existing versao.
     *
     * @param id the id of the versao to save.
     * @param versao the versao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated versao,
     * or with status {@code 400 (Bad Request)} if the versao is not valid,
     * or with status {@code 500 (Internal Server Error)} if the versao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Versao> updateVersao(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Versao versao
    ) throws URISyntaxException {
        log.debug("REST request to update Versao : {}, {}", id, versao);
        if (versao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, versao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!versaoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        versao = versaoService.update(versao);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, versao.getId().toString()))
            .body(versao);
    }

    /**
     * {@code PATCH  /versaos/:id} : Partial updates given fields of an existing versao, field will ignore if it is null
     *
     * @param id the id of the versao to save.
     * @param versao the versao to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated versao,
     * or with status {@code 400 (Bad Request)} if the versao is not valid,
     * or with status {@code 404 (Not Found)} if the versao is not found,
     * or with status {@code 500 (Internal Server Error)} if the versao couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Versao> partialUpdateVersao(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Versao versao
    ) throws URISyntaxException {
        log.debug("REST request to partial update Versao partially : {}, {}", id, versao);
        if (versao.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, versao.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!versaoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Versao> result = versaoService.partialUpdate(versao);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, versao.getId().toString())
        );
    }

    /**
     * {@code GET  /versaos} : get all the versaos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of versaos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Versao>> getAllVersaos(
        VersaoCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        if (userService.usuarioNAOLogado()) {
            criteria.setStatus(STATUS_PUBLIC_FILTER);
        }

        log.debug("REST request to get Versaos by criteria: {}", criteria);

        Page<Versao> page = versaoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /versaos/count} : count all the versaos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countVersaos(VersaoCriteria criteria) {
        log.debug("REST request to count Versaos by criteria: {}", criteria);
        return ResponseEntity.ok().body(versaoQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /versaos/:id} : get the "id" versao.
     *
     * @param id the id of the versao to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the versao, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Versao> getVersao(@PathVariable("id") Long id) {
        log.debug("REST request to get Versao : {}", id);
        Optional<Versao> versao = versaoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(versao);
    }

    /**
     * {@code DELETE  /versaos/:id} : delete the "id" versao.
     *
     * @param id the id of the versao to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVersao(@PathVariable("id") Long id) {
        log.debug("REST request to delete Versao : {}", id);
        versaoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
