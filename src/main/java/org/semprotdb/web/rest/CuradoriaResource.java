package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.semprotdb.domain.Curadoria;
import org.semprotdb.repository.CuradoriaRepository;
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
 * REST controller for managing {@link org.semprotdb.domain.Curadoria}.
 */
@RestController
@RequestMapping("/api/curadorias")
@Transactional
public class CuradoriaResource {

    private static final Logger log = LoggerFactory.getLogger(CuradoriaResource.class);

    private static final String ENTITY_NAME = "curadoria";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CuradoriaRepository curadoriaRepository;

    public CuradoriaResource(CuradoriaRepository curadoriaRepository) {
        this.curadoriaRepository = curadoriaRepository;
    }

    /**
     * {@code POST  /curadorias} : Create a new curadoria.
     *
     * @param curadoria the curadoria to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new curadoria, or with status {@code 400 (Bad Request)} if the curadoria has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Curadoria> createCuradoria(@Valid @RequestBody Curadoria curadoria) throws URISyntaxException {
        log.debug("REST request to save Curadoria : {}", curadoria);
        if (curadoria.getId() != null) {
            throw new BadRequestAlertException("A new curadoria cannot already have an ID", ENTITY_NAME, "idexists");
        }
        curadoria = curadoriaRepository.save(curadoria);
        return ResponseEntity.created(new URI("/api/curadorias/" + curadoria.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, curadoria.getId().toString()))
            .body(curadoria);
    }

    /**
     * {@code PUT  /curadorias/:id} : Updates an existing curadoria.
     *
     * @param id the id of the curadoria to save.
     * @param curadoria the curadoria to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated curadoria,
     * or with status {@code 400 (Bad Request)} if the curadoria is not valid,
     * or with status {@code 500 (Internal Server Error)} if the curadoria couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Curadoria> updateCuradoria(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Curadoria curadoria
    ) throws URISyntaxException {
        log.debug("REST request to update Curadoria : {}, {}", id, curadoria);
        if (curadoria.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, curadoria.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!curadoriaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        curadoria = curadoriaRepository.save(curadoria);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, curadoria.getId().toString()))
            .body(curadoria);
    }

    /**
     * {@code PATCH  /curadorias/:id} : Partial updates given fields of an existing curadoria, field will ignore if it is null
     *
     * @param id the id of the curadoria to save.
     * @param curadoria the curadoria to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated curadoria,
     * or with status {@code 400 (Bad Request)} if the curadoria is not valid,
     * or with status {@code 404 (Not Found)} if the curadoria is not found,
     * or with status {@code 500 (Internal Server Error)} if the curadoria couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Curadoria> partialUpdateCuradoria(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Curadoria curadoria
    ) throws URISyntaxException {
        log.debug("REST request to partial update Curadoria partially : {}, {}", id, curadoria);
        if (curadoria.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, curadoria.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!curadoriaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Curadoria> result = curadoriaRepository
            .findById(curadoria.getId())
            .map(existingCuradoria -> {
                if (curadoria.getEmail() != null) {
                    existingCuradoria.setEmail(curadoria.getEmail());
                }
                if (curadoria.getData() != null) {
                    existingCuradoria.setData(curadoria.getData());
                }
                if (curadoria.getAnotacoes() != null) {
                    existingCuradoria.setAnotacoes(curadoria.getAnotacoes());
                }

                return existingCuradoria;
            })
            .map(curadoriaRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, curadoria.getId().toString())
        );
    }

    /**
     * {@code GET  /curadorias} : get all the curadorias.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of curadorias in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Curadoria>> getAllCuradorias(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Curadorias");
        Page<Curadoria> page = curadoriaRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /curadorias/:id} : get the "id" curadoria.
     *
     * @param id the id of the curadoria to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the curadoria, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Curadoria> getCuradoria(@PathVariable("id") Long id) {
        log.debug("REST request to get Curadoria : {}", id);
        Optional<Curadoria> curadoria = curadoriaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(curadoria);
    }

    /**
     * {@code DELETE  /curadorias/:id} : delete the "id" curadoria.
     *
     * @param id the id of the curadoria to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuradoria(@PathVariable("id") Long id) {
        log.debug("REST request to delete Curadoria : {}", id);

        Curadoria curadoria = curadoriaRepository.findById(id).orElseThrow();
        int ptnas = curadoria.getProteinas().size();
        int genes = curadoria.getGenes().size();

        if (ptnas + genes > 0) {
            String msg = "Não pode remover essa CURADORIA " + "por possuir " + ptnas + " proteinas e " + genes + " genes relacionados.";
            throw new BadRequestAlertException(msg, ENTITY_NAME, msg);
        }

        curadoriaRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
