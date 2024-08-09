package org.semprotdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.semprotdb.domain.OrganismoAsserts.*;
import static org.semprotdb.web.rest.TestUtil.createUpdateProxyForBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semprotdb.IntegrationTest;
import org.semprotdb.domain.Organismo;
import org.semprotdb.repository.OrganismoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrganismoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrganismoResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_SIGLA = "AAAAAAAAAA";
    private static final String UPDATED_SIGLA = "BBBBBBBBBB";

    private static final String DEFAULT_APELIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELIDO = "BBBBBBBBBB";

    private static final byte[] DEFAULT_SILHUETA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_SILHUETA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_SILHUETA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_SILHUETA_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_ICONE = "AAAAAAAAAA";
    private static final String UPDATED_ICONE = "BBBBBBBBBB";

    private static final String DEFAULT_POS = "AAAAAAAAAA";
    private static final String UPDATED_POS = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGEM = "AAAAAAAAAA";
    private static final String UPDATED_IMAGEM = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/organismos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrganismoRepository organismoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrganismoMockMvc;

    private Organismo organismo;

    private Organismo insertedOrganismo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organismo createEntity(EntityManager em) {
        Organismo organismo = new Organismo()
            .nome(DEFAULT_NOME)
            .sigla(DEFAULT_SIGLA)
            .apelido(DEFAULT_APELIDO)
            .silhueta(DEFAULT_SILHUETA)
            .silhuetaContentType(DEFAULT_SILHUETA_CONTENT_TYPE)
            .icone(DEFAULT_ICONE)
            .pos(DEFAULT_POS)
            .imagem(DEFAULT_IMAGEM)
            .descricao(DEFAULT_DESCRICAO);
        return organismo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organismo createUpdatedEntity(EntityManager em) {
        Organismo organismo = new Organismo()
            .nome(UPDATED_NOME)
            .sigla(UPDATED_SIGLA)
            .apelido(UPDATED_APELIDO)
            .silhueta(UPDATED_SILHUETA)
            .silhuetaContentType(UPDATED_SILHUETA_CONTENT_TYPE)
            .icone(UPDATED_ICONE)
            .pos(UPDATED_POS)
            .imagem(UPDATED_IMAGEM)
            .descricao(UPDATED_DESCRICAO);
        return organismo;
    }

    @BeforeEach
    public void initTest() {
        organismo = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedOrganismo != null) {
            organismoRepository.delete(insertedOrganismo);
            insertedOrganismo = null;
        }
    }

    @Test
    @Transactional
    void createOrganismo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Organismo
        var returnedOrganismo = om.readValue(
            restOrganismoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organismo)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Organismo.class
        );

        // Validate the Organismo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertOrganismoUpdatableFieldsEquals(returnedOrganismo, getPersistedOrganismo(returnedOrganismo));

        insertedOrganismo = returnedOrganismo;
    }

    @Test
    @Transactional
    void createOrganismoWithExistingId() throws Exception {
        // Create the Organismo with an existing ID
        organismo.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrganismoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organismo)))
            .andExpect(status().isBadRequest());

        // Validate the Organismo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        organismo.setNome(null);

        // Create the Organismo, which fails.

        restOrganismoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organismo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrganismos() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList
        restOrganismoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organismo.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].sigla").value(hasItem(DEFAULT_SIGLA)))
            .andExpect(jsonPath("$.[*].apelido").value(hasItem(DEFAULT_APELIDO)))
            .andExpect(jsonPath("$.[*].silhuetaContentType").value(hasItem(DEFAULT_SILHUETA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].silhueta").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_SILHUETA))))
            .andExpect(jsonPath("$.[*].icone").value(hasItem(DEFAULT_ICONE)))
            .andExpect(jsonPath("$.[*].pos").value(hasItem(DEFAULT_POS)))
            .andExpect(jsonPath("$.[*].imagem").value(hasItem(DEFAULT_IMAGEM)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO)));
    }

    @Test
    @Transactional
    void getOrganismo() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get the organismo
        restOrganismoMockMvc
            .perform(get(ENTITY_API_URL_ID, organismo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(organismo.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.sigla").value(DEFAULT_SIGLA))
            .andExpect(jsonPath("$.apelido").value(DEFAULT_APELIDO))
            .andExpect(jsonPath("$.silhuetaContentType").value(DEFAULT_SILHUETA_CONTENT_TYPE))
            .andExpect(jsonPath("$.silhueta").value(Base64.getEncoder().encodeToString(DEFAULT_SILHUETA)))
            .andExpect(jsonPath("$.icone").value(DEFAULT_ICONE))
            .andExpect(jsonPath("$.pos").value(DEFAULT_POS))
            .andExpect(jsonPath("$.imagem").value(DEFAULT_IMAGEM))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO));
    }

    @Test
    @Transactional
    void getOrganismosByIdFiltering() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        Long id = organismo.getId();

        defaultOrganismoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOrganismoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOrganismoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrganismosByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where nome equals to
        defaultOrganismoFiltering("nome.equals=" + DEFAULT_NOME, "nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllOrganismosByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where nome in
        defaultOrganismoFiltering("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME, "nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllOrganismosByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where nome is not null
        defaultOrganismoFiltering("nome.specified=true", "nome.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganismosByNomeContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where nome contains
        defaultOrganismoFiltering("nome.contains=" + DEFAULT_NOME, "nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllOrganismosByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where nome does not contain
        defaultOrganismoFiltering("nome.doesNotContain=" + UPDATED_NOME, "nome.doesNotContain=" + DEFAULT_NOME);
    }

    @Test
    @Transactional
    void getAllOrganismosBySiglaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where sigla equals to
        defaultOrganismoFiltering("sigla.equals=" + DEFAULT_SIGLA, "sigla.equals=" + UPDATED_SIGLA);
    }

    @Test
    @Transactional
    void getAllOrganismosBySiglaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where sigla in
        defaultOrganismoFiltering("sigla.in=" + DEFAULT_SIGLA + "," + UPDATED_SIGLA, "sigla.in=" + UPDATED_SIGLA);
    }

    @Test
    @Transactional
    void getAllOrganismosBySiglaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where sigla is not null
        defaultOrganismoFiltering("sigla.specified=true", "sigla.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganismosBySiglaContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where sigla contains
        defaultOrganismoFiltering("sigla.contains=" + DEFAULT_SIGLA, "sigla.contains=" + UPDATED_SIGLA);
    }

    @Test
    @Transactional
    void getAllOrganismosBySiglaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where sigla does not contain
        defaultOrganismoFiltering("sigla.doesNotContain=" + UPDATED_SIGLA, "sigla.doesNotContain=" + DEFAULT_SIGLA);
    }

    @Test
    @Transactional
    void getAllOrganismosByApelidoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where apelido equals to
        defaultOrganismoFiltering("apelido.equals=" + DEFAULT_APELIDO, "apelido.equals=" + UPDATED_APELIDO);
    }

    @Test
    @Transactional
    void getAllOrganismosByApelidoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where apelido in
        defaultOrganismoFiltering("apelido.in=" + DEFAULT_APELIDO + "," + UPDATED_APELIDO, "apelido.in=" + UPDATED_APELIDO);
    }

    @Test
    @Transactional
    void getAllOrganismosByApelidoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where apelido is not null
        defaultOrganismoFiltering("apelido.specified=true", "apelido.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganismosByApelidoContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where apelido contains
        defaultOrganismoFiltering("apelido.contains=" + DEFAULT_APELIDO, "apelido.contains=" + UPDATED_APELIDO);
    }

    @Test
    @Transactional
    void getAllOrganismosByApelidoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where apelido does not contain
        defaultOrganismoFiltering("apelido.doesNotContain=" + UPDATED_APELIDO, "apelido.doesNotContain=" + DEFAULT_APELIDO);
    }

    @Test
    @Transactional
    void getAllOrganismosByIconeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where icone equals to
        defaultOrganismoFiltering("icone.equals=" + DEFAULT_ICONE, "icone.equals=" + UPDATED_ICONE);
    }

    @Test
    @Transactional
    void getAllOrganismosByIconeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where icone in
        defaultOrganismoFiltering("icone.in=" + DEFAULT_ICONE + "," + UPDATED_ICONE, "icone.in=" + UPDATED_ICONE);
    }

    @Test
    @Transactional
    void getAllOrganismosByIconeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where icone is not null
        defaultOrganismoFiltering("icone.specified=true", "icone.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganismosByIconeContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where icone contains
        defaultOrganismoFiltering("icone.contains=" + DEFAULT_ICONE, "icone.contains=" + UPDATED_ICONE);
    }

    @Test
    @Transactional
    void getAllOrganismosByIconeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where icone does not contain
        defaultOrganismoFiltering("icone.doesNotContain=" + UPDATED_ICONE, "icone.doesNotContain=" + DEFAULT_ICONE);
    }

    @Test
    @Transactional
    void getAllOrganismosByPosIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where pos equals to
        defaultOrganismoFiltering("pos.equals=" + DEFAULT_POS, "pos.equals=" + UPDATED_POS);
    }

    @Test
    @Transactional
    void getAllOrganismosByPosIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where pos in
        defaultOrganismoFiltering("pos.in=" + DEFAULT_POS + "," + UPDATED_POS, "pos.in=" + UPDATED_POS);
    }

    @Test
    @Transactional
    void getAllOrganismosByPosIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where pos is not null
        defaultOrganismoFiltering("pos.specified=true", "pos.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganismosByPosContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where pos contains
        defaultOrganismoFiltering("pos.contains=" + DEFAULT_POS, "pos.contains=" + UPDATED_POS);
    }

    @Test
    @Transactional
    void getAllOrganismosByPosNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where pos does not contain
        defaultOrganismoFiltering("pos.doesNotContain=" + UPDATED_POS, "pos.doesNotContain=" + DEFAULT_POS);
    }

    @Test
    @Transactional
    void getAllOrganismosByImagemIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where imagem equals to
        defaultOrganismoFiltering("imagem.equals=" + DEFAULT_IMAGEM, "imagem.equals=" + UPDATED_IMAGEM);
    }

    @Test
    @Transactional
    void getAllOrganismosByImagemIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where imagem in
        defaultOrganismoFiltering("imagem.in=" + DEFAULT_IMAGEM + "," + UPDATED_IMAGEM, "imagem.in=" + UPDATED_IMAGEM);
    }

    @Test
    @Transactional
    void getAllOrganismosByImagemIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where imagem is not null
        defaultOrganismoFiltering("imagem.specified=true", "imagem.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganismosByImagemContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where imagem contains
        defaultOrganismoFiltering("imagem.contains=" + DEFAULT_IMAGEM, "imagem.contains=" + UPDATED_IMAGEM);
    }

    @Test
    @Transactional
    void getAllOrganismosByImagemNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where imagem does not contain
        defaultOrganismoFiltering("imagem.doesNotContain=" + UPDATED_IMAGEM, "imagem.doesNotContain=" + DEFAULT_IMAGEM);
    }

    @Test
    @Transactional
    void getAllOrganismosByDescricaoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where descricao equals to
        defaultOrganismoFiltering("descricao.equals=" + DEFAULT_DESCRICAO, "descricao.equals=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllOrganismosByDescricaoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where descricao in
        defaultOrganismoFiltering("descricao.in=" + DEFAULT_DESCRICAO + "," + UPDATED_DESCRICAO, "descricao.in=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllOrganismosByDescricaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where descricao is not null
        defaultOrganismoFiltering("descricao.specified=true", "descricao.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganismosByDescricaoContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where descricao contains
        defaultOrganismoFiltering("descricao.contains=" + DEFAULT_DESCRICAO, "descricao.contains=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllOrganismosByDescricaoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        // Get all the organismoList where descricao does not contain
        defaultOrganismoFiltering("descricao.doesNotContain=" + UPDATED_DESCRICAO, "descricao.doesNotContain=" + DEFAULT_DESCRICAO);
    }

    private void defaultOrganismoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOrganismoShouldBeFound(shouldBeFound);
        defaultOrganismoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrganismoShouldBeFound(String filter) throws Exception {
        restOrganismoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organismo.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].sigla").value(hasItem(DEFAULT_SIGLA)))
            .andExpect(jsonPath("$.[*].apelido").value(hasItem(DEFAULT_APELIDO)))
            .andExpect(jsonPath("$.[*].silhuetaContentType").value(hasItem(DEFAULT_SILHUETA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].silhueta").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_SILHUETA))))
            .andExpect(jsonPath("$.[*].icone").value(hasItem(DEFAULT_ICONE)))
            .andExpect(jsonPath("$.[*].pos").value(hasItem(DEFAULT_POS)))
            .andExpect(jsonPath("$.[*].imagem").value(hasItem(DEFAULT_IMAGEM)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO)));

        // Check, that the count call also returns 1
        restOrganismoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrganismoShouldNotBeFound(String filter) throws Exception {
        restOrganismoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrganismoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrganismo() throws Exception {
        // Get the organismo
        restOrganismoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrganismo() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organismo
        Organismo updatedOrganismo = organismoRepository.findById(organismo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOrganismo are not directly saved in db
        em.detach(updatedOrganismo);
        updatedOrganismo
            .nome(UPDATED_NOME)
            .sigla(UPDATED_SIGLA)
            .apelido(UPDATED_APELIDO)
            .silhueta(UPDATED_SILHUETA)
            .silhuetaContentType(UPDATED_SILHUETA_CONTENT_TYPE)
            .icone(UPDATED_ICONE)
            .pos(UPDATED_POS)
            .imagem(UPDATED_IMAGEM)
            .descricao(UPDATED_DESCRICAO);

        restOrganismoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrganismo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedOrganismo))
            )
            .andExpect(status().isOk());

        // Validate the Organismo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOrganismoToMatchAllProperties(updatedOrganismo);
    }

    @Test
    @Transactional
    void putNonExistingOrganismo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organismo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganismoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, organismo.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organismo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organismo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrganismo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organismo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganismoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(organismo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organismo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrganismo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organismo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganismoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(organismo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organismo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrganismoWithPatch() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organismo using partial update
        Organismo partialUpdatedOrganismo = new Organismo();
        partialUpdatedOrganismo.setId(organismo.getId());

        partialUpdatedOrganismo
            .nome(UPDATED_NOME)
            .sigla(UPDATED_SIGLA)
            .silhueta(UPDATED_SILHUETA)
            .silhuetaContentType(UPDATED_SILHUETA_CONTENT_TYPE)
            .icone(UPDATED_ICONE)
            .pos(UPDATED_POS)
            .imagem(UPDATED_IMAGEM);

        restOrganismoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganismo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrganismo))
            )
            .andExpect(status().isOk());

        // Validate the Organismo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganismoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOrganismo, organismo),
            getPersistedOrganismo(organismo)
        );
    }

    @Test
    @Transactional
    void fullUpdateOrganismoWithPatch() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the organismo using partial update
        Organismo partialUpdatedOrganismo = new Organismo();
        partialUpdatedOrganismo.setId(organismo.getId());

        partialUpdatedOrganismo
            .nome(UPDATED_NOME)
            .sigla(UPDATED_SIGLA)
            .apelido(UPDATED_APELIDO)
            .silhueta(UPDATED_SILHUETA)
            .silhuetaContentType(UPDATED_SILHUETA_CONTENT_TYPE)
            .icone(UPDATED_ICONE)
            .pos(UPDATED_POS)
            .imagem(UPDATED_IMAGEM)
            .descricao(UPDATED_DESCRICAO);

        restOrganismoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganismo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOrganismo))
            )
            .andExpect(status().isOk());

        // Validate the Organismo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOrganismoUpdatableFieldsEquals(partialUpdatedOrganismo, getPersistedOrganismo(partialUpdatedOrganismo));
    }

    @Test
    @Transactional
    void patchNonExistingOrganismo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organismo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganismoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, organismo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(organismo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organismo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrganismo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organismo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganismoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(organismo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organismo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrganismo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        organismo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganismoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(organismo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organismo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrganismo() throws Exception {
        // Initialize the database
        insertedOrganismo = organismoRepository.saveAndFlush(organismo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the organismo
        restOrganismoMockMvc
            .perform(delete(ENTITY_API_URL_ID, organismo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return organismoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Organismo getPersistedOrganismo(Organismo organismo) {
        return organismoRepository.findById(organismo.getId()).orElseThrow();
    }

    protected void assertPersistedOrganismoToMatchAllProperties(Organismo expectedOrganismo) {
        assertOrganismoAllPropertiesEquals(expectedOrganismo, getPersistedOrganismo(expectedOrganismo));
    }

    protected void assertPersistedOrganismoToMatchUpdatableProperties(Organismo expectedOrganismo) {
        assertOrganismoAllUpdatablePropertiesEquals(expectedOrganismo, getPersistedOrganismo(expectedOrganismo));
    }
}
