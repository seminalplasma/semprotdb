package org.semprotdb.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.semprotdb.domain.DBConfig;
import org.semprotdb.repository.DBConfigRepository;
import org.semprotdb.service.UserService;
import org.semprotdb.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.semprotdb.domain.DBConfig}.
 */
@RestController
@RequestMapping("/api/db-configs")
@Transactional
public class DBConfigResource {

    private static final Logger log = LoggerFactory.getLogger(DBConfigResource.class);

    private static final String ENTITY_NAME = "dBConfig";
    private final UserService userService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DBConfigRepository dBConfigRepository;

    public DBConfigResource(DBConfigRepository dBConfigRepository, UserService userService) {
        this.dBConfigRepository = dBConfigRepository;
        this.userService = userService;
    }

    /**
     * {@code POST  /db-configs} : Create a new dBConfig.
     *
     * @param dBConfig the dBConfig to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dBConfig, or with status {@code 400 (Bad Request)} if the dBConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DBConfig> createDBConfig(@Valid @RequestBody DBConfig dBConfig) throws URISyntaxException {
        log.debug("REST request to save DBConfig : {}", dBConfig);
        if (dBConfig.getId() != null) {
            throw new BadRequestAlertException("A new dBConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        dBConfig = dBConfigRepository.save(dBConfig);
        return ResponseEntity.created(new URI("/api/db-configs/" + dBConfig.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, dBConfig.getId().toString()))
            .body(dBConfig);
    }

    /**
     * {@code PUT  /db-configs/:id} : Updates an existing dBConfig.
     *
     * @param id the id of the dBConfig to save.
     * @param dBConfig the dBConfig to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dBConfig,
     * or with status {@code 400 (Bad Request)} if the dBConfig is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dBConfig couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DBConfig> updateDBConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DBConfig dBConfig
    ) throws URISyntaxException {
        log.debug("REST request to update DBConfig : {}, {}", id, dBConfig);
        if (dBConfig.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dBConfig.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dBConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        dBConfig = dBConfigRepository.save(dBConfig);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dBConfig.getId().toString()))
            .body(dBConfig);
    }

    /**
     * {@code PATCH  /db-configs/:id} : Partial updates given fields of an existing dBConfig, field will ignore if it is null
     *
     * @param id the id of the dBConfig to save.
     * @param dBConfig the dBConfig to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dBConfig,
     * or with status {@code 400 (Bad Request)} if the dBConfig is not valid,
     * or with status {@code 404 (Not Found)} if the dBConfig is not found,
     * or with status {@code 500 (Internal Server Error)} if the dBConfig couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DBConfig> partialUpdateDBConfig(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DBConfig dBConfig
    ) throws URISyntaxException {
        log.debug("REST request to partial update DBConfig partially : {}, {}", id, dBConfig);
        if (dBConfig.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dBConfig.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dBConfigRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DBConfig> result = dBConfigRepository
            .findById(dBConfig.getId())
            .map(existingDBConfig -> {
                if (dBConfig.getKey() != null) {
                    existingDBConfig.setKey(dBConfig.getKey());
                }
                if (dBConfig.getHabilitado() != null) {
                    existingDBConfig.setHabilitado(dBConfig.getHabilitado());
                }
                if (dBConfig.getVstring() != null) {
                    existingDBConfig.setVstring(dBConfig.getVstring());
                }
                if (dBConfig.getVbol() != null) {
                    existingDBConfig.setVbol(dBConfig.getVbol());
                }
                if (dBConfig.getVdate() != null) {
                    existingDBConfig.setVdate(dBConfig.getVdate());
                }
                if (dBConfig.getVint() != null) {
                    existingDBConfig.setVint(dBConfig.getVint());
                }
                if (dBConfig.getVtext() != null) {
                    existingDBConfig.setVtext(dBConfig.getVtext());
                }
                if (dBConfig.getVimg() != null) {
                    existingDBConfig.setVimg(dBConfig.getVimg());
                }
                if (dBConfig.getVimgContentType() != null) {
                    existingDBConfig.setVimgContentType(dBConfig.getVimgContentType());
                }

                return existingDBConfig;
            })
            .map(dBConfigRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dBConfig.getId().toString())
        );
    }

    private boolean is_pub(DBConfig dbc) {
        return dbc.getHabilitado() && dbc.getKey().contains(".pub.");
    }

    /**
     * {@code GET  /db-configs} : get all the dBConfigs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dBConfigs in body.
     */
    @GetMapping("")
    public List<DBConfig> getAllDBConfigs(boolean logs) {
        log.debug("REST request to get all DBConfigs {}", logs ? "LOGS" : "");
        List<DBConfig> list = dBConfigRepository.findAllLight(Pageable.unpaged()).toList();
        if (userService.usuarioNAOLogado()) {
            list = list.stream().filter(this::is_pub).toList();
        } else if (logs) {
            list = List.of(dBConfigRepository.findDBConfigByKey("log.tail").orElseThrow());
        }
        return list;
    }

    /**
     * {@code GET  /db-configs/:id} : get the "id" dBConfig.
     *
     * @param id the id of the dBConfig to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dBConfig, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DBConfig> getDBConfig(@PathVariable("id") Long id) {
        log.debug("REST request to get DBConfig : {}", id);
        Optional<DBConfig> dBConfig = dBConfigRepository.findById(id).filter(x -> userService.usuarioEstaLogado() || is_pub(x));
        return ResponseUtil.wrapOrNotFound(dBConfig);
    }

    /**
     * {@code DELETE  /db-configs/:id} : delete the "id" dBConfig.
     *
     * @param id the id of the dBConfig to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDBConfig(@PathVariable("id") Long id) {
        log.debug("REST request to delete DBConfig : {}", id);
        dBConfigRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
