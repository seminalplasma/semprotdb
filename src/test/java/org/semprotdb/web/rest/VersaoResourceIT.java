package org.semprotdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.semprotdb.domain.VersaoAsserts.*;
import static org.semprotdb.web.rest.TestUtil.createUpdateProxyForBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semprotdb.IntegrationTest;
import org.semprotdb.domain.Versao;
import org.semprotdb.domain.enumeration.Status;
import org.semprotdb.repository.VersaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VersaoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VersaoResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_DETALHES = "AAAAAAAAAA";
    private static final String UPDATED_DETALHES = "BBBBBBBBBB";

    private static final Instant DEFAULT_RELEASE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RELEASE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.CRIADO;
    private static final Status UPDATED_STATUS = Status.CARREGADO;

    private static final Integer DEFAULT_NUMERO = 1;
    private static final Integer UPDATED_NUMERO = 2;
    private static final Integer SMALLER_NUMERO = 1 - 1;

    private static final String DEFAULT_LOGO = "AAAAAAAAAA";
    private static final String UPDATED_LOGO = "BBBBBBBBBB";

    private static final String DEFAULT_LOG = "AAAAAAAAAA";
    private static final String UPDATED_LOG = "BBBBBBBBBB";

    private static final String DEFAULT_TEXTO = "AAAAAAAAAA";
    private static final String UPDATED_TEXTO = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGEM = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGEM = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGEM_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGEM_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/versaos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VersaoRepository versaoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVersaoMockMvc;

    private Versao versao;

    private Versao insertedVersao;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Versao createEntity(EntityManager em) {
        Versao versao = new Versao()
            .nome(DEFAULT_NOME)
            .detalhes(DEFAULT_DETALHES)
            .release(DEFAULT_RELEASE)
            .label(DEFAULT_LABEL)
            .status(DEFAULT_STATUS)
            .numero(DEFAULT_NUMERO)
            .logo(DEFAULT_LOGO)
            .log(DEFAULT_LOG)
            .texto(DEFAULT_TEXTO)
            .imagem(DEFAULT_IMAGEM)
            .imagemContentType(DEFAULT_IMAGEM_CONTENT_TYPE);
        return versao;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Versao createUpdatedEntity(EntityManager em) {
        Versao versao = new Versao()
            .nome(UPDATED_NOME)
            .detalhes(UPDATED_DETALHES)
            .release(UPDATED_RELEASE)
            .label(UPDATED_LABEL)
            .status(UPDATED_STATUS)
            .numero(UPDATED_NUMERO)
            .logo(UPDATED_LOGO)
            .log(UPDATED_LOG)
            .texto(UPDATED_TEXTO)
            .imagem(UPDATED_IMAGEM)
            .imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE);
        return versao;
    }

    @BeforeEach
    public void initTest() {
        versao = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedVersao != null) {
            versaoRepository.delete(insertedVersao);
            insertedVersao = null;
        }
    }

    @Test
    @Transactional
    void createVersao() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Versao
        var returnedVersao = om.readValue(
            restVersaoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(versao)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Versao.class
        );

        // Validate the Versao in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertVersaoUpdatableFieldsEquals(returnedVersao, getPersistedVersao(returnedVersao));

        insertedVersao = returnedVersao;
    }

    @Test
    @Transactional
    void createVersaoWithExistingId() throws Exception {
        // Create the Versao with an existing ID
        versao.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVersaoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(versao)))
            .andExpect(status().isBadRequest());

        // Validate the Versao in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        versao.setNome(null);

        // Create the Versao, which fails.

        restVersaoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(versao)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        versao.setStatus(null);

        // Create the Versao, which fails.

        restVersaoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(versao)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumeroIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        versao.setNumero(null);

        // Create the Versao, which fails.

        restVersaoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(versao)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVersaos() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList
        restVersaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(versao.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].detalhes").value(hasItem(DEFAULT_DETALHES)))
            .andExpect(jsonPath("$.[*].release").value(hasItem(DEFAULT_RELEASE.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(DEFAULT_LOGO)))
            .andExpect(jsonPath("$.[*].log").value(hasItem(DEFAULT_LOG.toString())))
            .andExpect(jsonPath("$.[*].texto").value(hasItem(DEFAULT_TEXTO.toString())))
            .andExpect(jsonPath("$.[*].imagemContentType").value(hasItem(DEFAULT_IMAGEM_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imagem").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGEM))));
    }

    @Test
    @Transactional
    void getVersao() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get the versao
        restVersaoMockMvc
            .perform(get(ENTITY_API_URL_ID, versao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(versao.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.detalhes").value(DEFAULT_DETALHES))
            .andExpect(jsonPath("$.release").value(DEFAULT_RELEASE.toString()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.logo").value(DEFAULT_LOGO))
            .andExpect(jsonPath("$.log").value(DEFAULT_LOG.toString()))
            .andExpect(jsonPath("$.texto").value(DEFAULT_TEXTO.toString()))
            .andExpect(jsonPath("$.imagemContentType").value(DEFAULT_IMAGEM_CONTENT_TYPE))
            .andExpect(jsonPath("$.imagem").value(Base64.getEncoder().encodeToString(DEFAULT_IMAGEM)));
    }

    @Test
    @Transactional
    void getVersaosByIdFiltering() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        Long id = versao.getId();

        defaultVersaoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultVersaoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultVersaoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVersaosByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where nome equals to
        defaultVersaoFiltering("nome.equals=" + DEFAULT_NOME, "nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllVersaosByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where nome in
        defaultVersaoFiltering("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME, "nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllVersaosByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where nome is not null
        defaultVersaoFiltering("nome.specified=true", "nome.specified=false");
    }

    @Test
    @Transactional
    void getAllVersaosByNomeContainsSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where nome contains
        defaultVersaoFiltering("nome.contains=" + DEFAULT_NOME, "nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllVersaosByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where nome does not contain
        defaultVersaoFiltering("nome.doesNotContain=" + UPDATED_NOME, "nome.doesNotContain=" + DEFAULT_NOME);
    }

    @Test
    @Transactional
    void getAllVersaosByDetalhesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where detalhes equals to
        defaultVersaoFiltering("detalhes.equals=" + DEFAULT_DETALHES, "detalhes.equals=" + UPDATED_DETALHES);
    }

    @Test
    @Transactional
    void getAllVersaosByDetalhesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where detalhes in
        defaultVersaoFiltering("detalhes.in=" + DEFAULT_DETALHES + "," + UPDATED_DETALHES, "detalhes.in=" + UPDATED_DETALHES);
    }

    @Test
    @Transactional
    void getAllVersaosByDetalhesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where detalhes is not null
        defaultVersaoFiltering("detalhes.specified=true", "detalhes.specified=false");
    }

    @Test
    @Transactional
    void getAllVersaosByDetalhesContainsSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where detalhes contains
        defaultVersaoFiltering("detalhes.contains=" + DEFAULT_DETALHES, "detalhes.contains=" + UPDATED_DETALHES);
    }

    @Test
    @Transactional
    void getAllVersaosByDetalhesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where detalhes does not contain
        defaultVersaoFiltering("detalhes.doesNotContain=" + UPDATED_DETALHES, "detalhes.doesNotContain=" + DEFAULT_DETALHES);
    }

    @Test
    @Transactional
    void getAllVersaosByReleaseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where release equals to
        defaultVersaoFiltering("release.equals=" + DEFAULT_RELEASE, "release.equals=" + UPDATED_RELEASE);
    }

    @Test
    @Transactional
    void getAllVersaosByReleaseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where release in
        defaultVersaoFiltering("release.in=" + DEFAULT_RELEASE + "," + UPDATED_RELEASE, "release.in=" + UPDATED_RELEASE);
    }

    @Test
    @Transactional
    void getAllVersaosByReleaseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where release is not null
        defaultVersaoFiltering("release.specified=true", "release.specified=false");
    }

    @Test
    @Transactional
    void getAllVersaosByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where label equals to
        defaultVersaoFiltering("label.equals=" + DEFAULT_LABEL, "label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllVersaosByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where label in
        defaultVersaoFiltering("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL, "label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllVersaosByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where label is not null
        defaultVersaoFiltering("label.specified=true", "label.specified=false");
    }

    @Test
    @Transactional
    void getAllVersaosByLabelContainsSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where label contains
        defaultVersaoFiltering("label.contains=" + DEFAULT_LABEL, "label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllVersaosByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where label does not contain
        defaultVersaoFiltering("label.doesNotContain=" + UPDATED_LABEL, "label.doesNotContain=" + DEFAULT_LABEL);
    }

    @Test
    @Transactional
    void getAllVersaosByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where status equals to
        defaultVersaoFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllVersaosByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where status in
        defaultVersaoFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllVersaosByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where status is not null
        defaultVersaoFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllVersaosByNumeroIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where numero equals to
        defaultVersaoFiltering("numero.equals=" + DEFAULT_NUMERO, "numero.equals=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllVersaosByNumeroIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where numero in
        defaultVersaoFiltering("numero.in=" + DEFAULT_NUMERO + "," + UPDATED_NUMERO, "numero.in=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllVersaosByNumeroIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where numero is not null
        defaultVersaoFiltering("numero.specified=true", "numero.specified=false");
    }

    @Test
    @Transactional
    void getAllVersaosByNumeroIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where numero is greater than or equal to
        defaultVersaoFiltering("numero.greaterThanOrEqual=" + DEFAULT_NUMERO, "numero.greaterThanOrEqual=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllVersaosByNumeroIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where numero is less than or equal to
        defaultVersaoFiltering("numero.lessThanOrEqual=" + DEFAULT_NUMERO, "numero.lessThanOrEqual=" + SMALLER_NUMERO);
    }

    @Test
    @Transactional
    void getAllVersaosByNumeroIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where numero is less than
        defaultVersaoFiltering("numero.lessThan=" + UPDATED_NUMERO, "numero.lessThan=" + DEFAULT_NUMERO);
    }

    @Test
    @Transactional
    void getAllVersaosByNumeroIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where numero is greater than
        defaultVersaoFiltering("numero.greaterThan=" + SMALLER_NUMERO, "numero.greaterThan=" + DEFAULT_NUMERO);
    }

    @Test
    @Transactional
    void getAllVersaosByLogoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where logo equals to
        defaultVersaoFiltering("logo.equals=" + DEFAULT_LOGO, "logo.equals=" + UPDATED_LOGO);
    }

    @Test
    @Transactional
    void getAllVersaosByLogoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where logo in
        defaultVersaoFiltering("logo.in=" + DEFAULT_LOGO + "," + UPDATED_LOGO, "logo.in=" + UPDATED_LOGO);
    }

    @Test
    @Transactional
    void getAllVersaosByLogoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where logo is not null
        defaultVersaoFiltering("logo.specified=true", "logo.specified=false");
    }

    @Test
    @Transactional
    void getAllVersaosByLogoContainsSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where logo contains
        defaultVersaoFiltering("logo.contains=" + DEFAULT_LOGO, "logo.contains=" + UPDATED_LOGO);
    }

    @Test
    @Transactional
    void getAllVersaosByLogoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        // Get all the versaoList where logo does not contain
        defaultVersaoFiltering("logo.doesNotContain=" + UPDATED_LOGO, "logo.doesNotContain=" + DEFAULT_LOGO);
    }

    private void defaultVersaoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultVersaoShouldBeFound(shouldBeFound);
        defaultVersaoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVersaoShouldBeFound(String filter) throws Exception {
        restVersaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(versao.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].detalhes").value(hasItem(DEFAULT_DETALHES)))
            .andExpect(jsonPath("$.[*].release").value(hasItem(DEFAULT_RELEASE.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(DEFAULT_LOGO)))
            .andExpect(jsonPath("$.[*].log").value(hasItem(DEFAULT_LOG.toString())))
            .andExpect(jsonPath("$.[*].texto").value(hasItem(DEFAULT_TEXTO.toString())))
            .andExpect(jsonPath("$.[*].imagemContentType").value(hasItem(DEFAULT_IMAGEM_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].imagem").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_IMAGEM))));

        // Check, that the count call also returns 1
        restVersaoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVersaoShouldNotBeFound(String filter) throws Exception {
        restVersaoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVersaoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVersao() throws Exception {
        // Get the versao
        restVersaoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVersao() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the versao
        Versao updatedVersao = versaoRepository.findById(versao.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVersao are not directly saved in db
        em.detach(updatedVersao);
        updatedVersao
            .nome(UPDATED_NOME)
            .detalhes(UPDATED_DETALHES)
            .release(UPDATED_RELEASE)
            .label(UPDATED_LABEL)
            .status(UPDATED_STATUS)
            .numero(UPDATED_NUMERO)
            .logo(UPDATED_LOGO)
            .log(UPDATED_LOG)
            .texto(UPDATED_TEXTO)
            .imagem(UPDATED_IMAGEM)
            .imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE);

        restVersaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVersao.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedVersao))
            )
            .andExpect(status().isOk());

        // Validate the Versao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVersaoToMatchAllProperties(updatedVersao);
    }

    @Test
    @Transactional
    void putNonExistingVersao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        versao.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVersaoMockMvc
            .perform(put(ENTITY_API_URL_ID, versao.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(versao)))
            .andExpect(status().isBadRequest());

        // Validate the Versao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVersao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        versao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersaoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(versao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Versao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVersao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        versao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersaoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(versao)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Versao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVersaoWithPatch() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the versao using partial update
        Versao partialUpdatedVersao = new Versao();
        partialUpdatedVersao.setId(versao.getId());

        partialUpdatedVersao.release(UPDATED_RELEASE).imagem(UPDATED_IMAGEM).imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE);

        restVersaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVersao.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVersao))
            )
            .andExpect(status().isOk());

        // Validate the Versao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVersaoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVersao, versao), getPersistedVersao(versao));
    }

    @Test
    @Transactional
    void fullUpdateVersaoWithPatch() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the versao using partial update
        Versao partialUpdatedVersao = new Versao();
        partialUpdatedVersao.setId(versao.getId());

        partialUpdatedVersao
            .nome(UPDATED_NOME)
            .detalhes(UPDATED_DETALHES)
            .release(UPDATED_RELEASE)
            .label(UPDATED_LABEL)
            .status(UPDATED_STATUS)
            .numero(UPDATED_NUMERO)
            .logo(UPDATED_LOGO)
            .log(UPDATED_LOG)
            .texto(UPDATED_TEXTO)
            .imagem(UPDATED_IMAGEM)
            .imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE);

        restVersaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVersao.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVersao))
            )
            .andExpect(status().isOk());

        // Validate the Versao in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVersaoUpdatableFieldsEquals(partialUpdatedVersao, getPersistedVersao(partialUpdatedVersao));
    }

    @Test
    @Transactional
    void patchNonExistingVersao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        versao.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVersaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, versao.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(versao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Versao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVersao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        versao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersaoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(versao))
            )
            .andExpect(status().isBadRequest());

        // Validate the Versao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVersao() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        versao.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVersaoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(versao)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Versao in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVersao() throws Exception {
        // Initialize the database
        insertedVersao = versaoRepository.saveAndFlush(versao);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the versao
        restVersaoMockMvc
            .perform(delete(ENTITY_API_URL_ID, versao.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return versaoRepository.count();
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

    protected Versao getPersistedVersao(Versao versao) {
        return versaoRepository.findById(versao.getId()).orElseThrow();
    }

    protected void assertPersistedVersaoToMatchAllProperties(Versao expectedVersao) {
        assertVersaoAllPropertiesEquals(expectedVersao, getPersistedVersao(expectedVersao));
    }

    protected void assertPersistedVersaoToMatchUpdatableProperties(Versao expectedVersao) {
        assertVersaoAllUpdatablePropertiesEquals(expectedVersao, getPersistedVersao(expectedVersao));
    }
}
