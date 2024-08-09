package org.semprotdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.semprotdb.domain.CargaAsserts.*;
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
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.Versao;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Formato;
import org.semprotdb.domain.enumeration.Tipo;
import org.semprotdb.repository.CargaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CargaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CargaResourceIT {

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDEM = 1;
    private static final Integer UPDATED_ORDEM = 2;
    private static final Integer SMALLER_ORDEM = 1 - 1;

    private static final byte[] DEFAULT_PLANILHA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PLANILHA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PLANILHA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PLANILHA_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_CAMINHO = "AAAAAAAAAA";
    private static final String UPDATED_CAMINHO = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VALIDADO = false;
    private static final Boolean UPDATED_VALIDADO = true;

    private static final Tipo DEFAULT_TIPO = Tipo.ARQUIVO;
    private static final Tipo UPDATED_TIPO = Tipo.CAMINHO;

    private static final Formato DEFAULT_FORMATO = Formato.TSV;
    private static final Formato UPDATED_FORMATO = Formato.XLSX;

    private static final Destino DEFAULT_DESTINO = Destino.DADOS;
    private static final Destino UPDATED_DESTINO = Destino.METADADOS;

    private static final Integer DEFAULT_LINHAS = 1;
    private static final Integer UPDATED_LINHAS = 2;
    private static final Integer SMALLER_LINHAS = 1 - 1;

    private static final String DEFAULT_CHECKSUM = "AAAAAAAAAA";
    private static final String UPDATED_CHECKSUM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cargas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CargaRepository cargaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCargaMockMvc;

    private Carga carga;

    private Carga insertedCarga;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carga createEntity(EntityManager em) {
        Carga carga = new Carga()
            .status(DEFAULT_STATUS)
            .ordem(DEFAULT_ORDEM)
            .planilha(DEFAULT_PLANILHA)
            .planilhaContentType(DEFAULT_PLANILHA_CONTENT_TYPE)
            .nome(DEFAULT_NOME)
            .caminho(DEFAULT_CAMINHO)
            .validado(DEFAULT_VALIDADO)
            .tipo(DEFAULT_TIPO)
            .formato(DEFAULT_FORMATO)
            .destino(DEFAULT_DESTINO)
            .linhas(DEFAULT_LINHAS)
            .checksum(DEFAULT_CHECKSUM);
        return carga;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Carga createUpdatedEntity(EntityManager em) {
        Carga carga = new Carga()
            .status(UPDATED_STATUS)
            .ordem(UPDATED_ORDEM)
            .planilha(UPDATED_PLANILHA)
            .planilhaContentType(UPDATED_PLANILHA_CONTENT_TYPE)
            .nome(UPDATED_NOME)
            .caminho(UPDATED_CAMINHO)
            .validado(UPDATED_VALIDADO)
            .tipo(UPDATED_TIPO)
            .formato(UPDATED_FORMATO)
            .destino(UPDATED_DESTINO)
            .linhas(UPDATED_LINHAS)
            .checksum(UPDATED_CHECKSUM);
        return carga;
    }

    @BeforeEach
    public void initTest() {
        carga = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedCarga != null) {
            cargaRepository.delete(insertedCarga);
            insertedCarga = null;
        }
    }

    @Test
    @Transactional
    void createCarga() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Carga
        var returnedCarga = om.readValue(
            restCargaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carga)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Carga.class
        );

        // Validate the Carga in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCargaUpdatableFieldsEquals(returnedCarga, getPersistedCarga(returnedCarga));

        insertedCarga = returnedCarga;
    }

    @Test
    @Transactional
    void createCargaWithExistingId() throws Exception {
        // Create the Carga with an existing ID
        carga.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCargaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carga)))
            .andExpect(status().isBadRequest());

        // Validate the Carga in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carga.setNome(null);

        // Create the Carga, which fails.

        restCargaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carga)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTipoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carga.setTipo(null);

        // Create the Carga, which fails.

        restCargaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carga)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFormatoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carga.setFormato(null);

        // Create the Carga, which fails.

        restCargaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carga)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCargas() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList
        restCargaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carga.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].ordem").value(hasItem(DEFAULT_ORDEM)))
            .andExpect(jsonPath("$.[*].planilhaContentType").value(hasItem(DEFAULT_PLANILHA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].planilha").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PLANILHA))))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].caminho").value(hasItem(DEFAULT_CAMINHO)))
            .andExpect(jsonPath("$.[*].validado").value(hasItem(DEFAULT_VALIDADO.booleanValue())))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())))
            .andExpect(jsonPath("$.[*].formato").value(hasItem(DEFAULT_FORMATO.toString())))
            .andExpect(jsonPath("$.[*].destino").value(hasItem(DEFAULT_DESTINO.toString())))
            .andExpect(jsonPath("$.[*].linhas").value(hasItem(DEFAULT_LINHAS)))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)));
    }

    @Test
    @Transactional
    void getCarga() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get the carga
        restCargaMockMvc
            .perform(get(ENTITY_API_URL_ID, carga.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carga.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.ordem").value(DEFAULT_ORDEM))
            .andExpect(jsonPath("$.planilhaContentType").value(DEFAULT_PLANILHA_CONTENT_TYPE))
            .andExpect(jsonPath("$.planilha").value(Base64.getEncoder().encodeToString(DEFAULT_PLANILHA)))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.caminho").value(DEFAULT_CAMINHO))
            .andExpect(jsonPath("$.validado").value(DEFAULT_VALIDADO.booleanValue()))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO.toString()))
            .andExpect(jsonPath("$.formato").value(DEFAULT_FORMATO.toString()))
            .andExpect(jsonPath("$.destino").value(DEFAULT_DESTINO.toString()))
            .andExpect(jsonPath("$.linhas").value(DEFAULT_LINHAS))
            .andExpect(jsonPath("$.checksum").value(DEFAULT_CHECKSUM));
    }

    @Test
    @Transactional
    void getCargasByIdFiltering() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        Long id = carga.getId();

        defaultCargaFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCargaFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCargaFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCargasByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where status equals to
        defaultCargaFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCargasByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where status in
        defaultCargaFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCargasByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where status is not null
        defaultCargaFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByStatusContainsSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where status contains
        defaultCargaFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllCargasByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where status does not contain
        defaultCargaFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllCargasByOrdemIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where ordem equals to
        defaultCargaFiltering("ordem.equals=" + DEFAULT_ORDEM, "ordem.equals=" + UPDATED_ORDEM);
    }

    @Test
    @Transactional
    void getAllCargasByOrdemIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where ordem in
        defaultCargaFiltering("ordem.in=" + DEFAULT_ORDEM + "," + UPDATED_ORDEM, "ordem.in=" + UPDATED_ORDEM);
    }

    @Test
    @Transactional
    void getAllCargasByOrdemIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where ordem is not null
        defaultCargaFiltering("ordem.specified=true", "ordem.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByOrdemIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where ordem is greater than or equal to
        defaultCargaFiltering("ordem.greaterThanOrEqual=" + DEFAULT_ORDEM, "ordem.greaterThanOrEqual=" + UPDATED_ORDEM);
    }

    @Test
    @Transactional
    void getAllCargasByOrdemIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where ordem is less than or equal to
        defaultCargaFiltering("ordem.lessThanOrEqual=" + DEFAULT_ORDEM, "ordem.lessThanOrEqual=" + SMALLER_ORDEM);
    }

    @Test
    @Transactional
    void getAllCargasByOrdemIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where ordem is less than
        defaultCargaFiltering("ordem.lessThan=" + UPDATED_ORDEM, "ordem.lessThan=" + DEFAULT_ORDEM);
    }

    @Test
    @Transactional
    void getAllCargasByOrdemIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where ordem is greater than
        defaultCargaFiltering("ordem.greaterThan=" + SMALLER_ORDEM, "ordem.greaterThan=" + DEFAULT_ORDEM);
    }

    @Test
    @Transactional
    void getAllCargasByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where nome equals to
        defaultCargaFiltering("nome.equals=" + DEFAULT_NOME, "nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllCargasByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where nome in
        defaultCargaFiltering("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME, "nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllCargasByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where nome is not null
        defaultCargaFiltering("nome.specified=true", "nome.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByNomeContainsSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where nome contains
        defaultCargaFiltering("nome.contains=" + DEFAULT_NOME, "nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllCargasByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where nome does not contain
        defaultCargaFiltering("nome.doesNotContain=" + UPDATED_NOME, "nome.doesNotContain=" + DEFAULT_NOME);
    }

    @Test
    @Transactional
    void getAllCargasByCaminhoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where caminho equals to
        defaultCargaFiltering("caminho.equals=" + DEFAULT_CAMINHO, "caminho.equals=" + UPDATED_CAMINHO);
    }

    @Test
    @Transactional
    void getAllCargasByCaminhoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where caminho in
        defaultCargaFiltering("caminho.in=" + DEFAULT_CAMINHO + "," + UPDATED_CAMINHO, "caminho.in=" + UPDATED_CAMINHO);
    }

    @Test
    @Transactional
    void getAllCargasByCaminhoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where caminho is not null
        defaultCargaFiltering("caminho.specified=true", "caminho.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByCaminhoContainsSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where caminho contains
        defaultCargaFiltering("caminho.contains=" + DEFAULT_CAMINHO, "caminho.contains=" + UPDATED_CAMINHO);
    }

    @Test
    @Transactional
    void getAllCargasByCaminhoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where caminho does not contain
        defaultCargaFiltering("caminho.doesNotContain=" + UPDATED_CAMINHO, "caminho.doesNotContain=" + DEFAULT_CAMINHO);
    }

    @Test
    @Transactional
    void getAllCargasByValidadoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where validado equals to
        defaultCargaFiltering("validado.equals=" + DEFAULT_VALIDADO, "validado.equals=" + UPDATED_VALIDADO);
    }

    @Test
    @Transactional
    void getAllCargasByValidadoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where validado in
        defaultCargaFiltering("validado.in=" + DEFAULT_VALIDADO + "," + UPDATED_VALIDADO, "validado.in=" + UPDATED_VALIDADO);
    }

    @Test
    @Transactional
    void getAllCargasByValidadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where validado is not null
        defaultCargaFiltering("validado.specified=true", "validado.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByTipoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where tipo equals to
        defaultCargaFiltering("tipo.equals=" + DEFAULT_TIPO, "tipo.equals=" + UPDATED_TIPO);
    }

    @Test
    @Transactional
    void getAllCargasByTipoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where tipo in
        defaultCargaFiltering("tipo.in=" + DEFAULT_TIPO + "," + UPDATED_TIPO, "tipo.in=" + UPDATED_TIPO);
    }

    @Test
    @Transactional
    void getAllCargasByTipoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where tipo is not null
        defaultCargaFiltering("tipo.specified=true", "tipo.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByFormatoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where formato equals to
        defaultCargaFiltering("formato.equals=" + DEFAULT_FORMATO, "formato.equals=" + UPDATED_FORMATO);
    }

    @Test
    @Transactional
    void getAllCargasByFormatoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where formato in
        defaultCargaFiltering("formato.in=" + DEFAULT_FORMATO + "," + UPDATED_FORMATO, "formato.in=" + UPDATED_FORMATO);
    }

    @Test
    @Transactional
    void getAllCargasByFormatoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where formato is not null
        defaultCargaFiltering("formato.specified=true", "formato.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByDestinoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where destino equals to
        defaultCargaFiltering("destino.equals=" + DEFAULT_DESTINO, "destino.equals=" + UPDATED_DESTINO);
    }

    @Test
    @Transactional
    void getAllCargasByDestinoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where destino in
        defaultCargaFiltering("destino.in=" + DEFAULT_DESTINO + "," + UPDATED_DESTINO, "destino.in=" + UPDATED_DESTINO);
    }

    @Test
    @Transactional
    void getAllCargasByDestinoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where destino is not null
        defaultCargaFiltering("destino.specified=true", "destino.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByLinhasIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where linhas equals to
        defaultCargaFiltering("linhas.equals=" + DEFAULT_LINHAS, "linhas.equals=" + UPDATED_LINHAS);
    }

    @Test
    @Transactional
    void getAllCargasByLinhasIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where linhas in
        defaultCargaFiltering("linhas.in=" + DEFAULT_LINHAS + "," + UPDATED_LINHAS, "linhas.in=" + UPDATED_LINHAS);
    }

    @Test
    @Transactional
    void getAllCargasByLinhasIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where linhas is not null
        defaultCargaFiltering("linhas.specified=true", "linhas.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByLinhasIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where linhas is greater than or equal to
        defaultCargaFiltering("linhas.greaterThanOrEqual=" + DEFAULT_LINHAS, "linhas.greaterThanOrEqual=" + UPDATED_LINHAS);
    }

    @Test
    @Transactional
    void getAllCargasByLinhasIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where linhas is less than or equal to
        defaultCargaFiltering("linhas.lessThanOrEqual=" + DEFAULT_LINHAS, "linhas.lessThanOrEqual=" + SMALLER_LINHAS);
    }

    @Test
    @Transactional
    void getAllCargasByLinhasIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where linhas is less than
        defaultCargaFiltering("linhas.lessThan=" + UPDATED_LINHAS, "linhas.lessThan=" + DEFAULT_LINHAS);
    }

    @Test
    @Transactional
    void getAllCargasByLinhasIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where linhas is greater than
        defaultCargaFiltering("linhas.greaterThan=" + SMALLER_LINHAS, "linhas.greaterThan=" + DEFAULT_LINHAS);
    }

    @Test
    @Transactional
    void getAllCargasByChecksumIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where checksum equals to
        defaultCargaFiltering("checksum.equals=" + DEFAULT_CHECKSUM, "checksum.equals=" + UPDATED_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllCargasByChecksumIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where checksum in
        defaultCargaFiltering("checksum.in=" + DEFAULT_CHECKSUM + "," + UPDATED_CHECKSUM, "checksum.in=" + UPDATED_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllCargasByChecksumIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where checksum is not null
        defaultCargaFiltering("checksum.specified=true", "checksum.specified=false");
    }

    @Test
    @Transactional
    void getAllCargasByChecksumContainsSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where checksum contains
        defaultCargaFiltering("checksum.contains=" + DEFAULT_CHECKSUM, "checksum.contains=" + UPDATED_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllCargasByChecksumNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        // Get all the cargaList where checksum does not contain
        defaultCargaFiltering("checksum.doesNotContain=" + UPDATED_CHECKSUM, "checksum.doesNotContain=" + DEFAULT_CHECKSUM);
    }

    @Test
    @Transactional
    void getAllCargasByVersaoIsEqualToSomething() throws Exception {
        Versao versao;
        if (TestUtil.findAll(em, Versao.class).isEmpty()) {
            cargaRepository.saveAndFlush(carga);
            versao = VersaoResourceIT.createEntity(em);
        } else {
            versao = TestUtil.findAll(em, Versao.class).get(0);
        }
        em.persist(versao);
        em.flush();
        carga.setVersao(versao);
        cargaRepository.saveAndFlush(carga);
        Long versaoId = versao.getId();
        // Get all the cargaList where versao equals to versaoId
        defaultCargaShouldBeFound("versaoId.equals=" + versaoId);

        // Get all the cargaList where versao equals to (versaoId + 1)
        defaultCargaShouldNotBeFound("versaoId.equals=" + (versaoId + 1));
    }

    private void defaultCargaFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCargaShouldBeFound(shouldBeFound);
        defaultCargaShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCargaShouldBeFound(String filter) throws Exception {
        restCargaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carga.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].ordem").value(hasItem(DEFAULT_ORDEM)))
            .andExpect(jsonPath("$.[*].planilhaContentType").value(hasItem(DEFAULT_PLANILHA_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].planilha").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PLANILHA))))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].caminho").value(hasItem(DEFAULT_CAMINHO)))
            .andExpect(jsonPath("$.[*].validado").value(hasItem(DEFAULT_VALIDADO.booleanValue())))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())))
            .andExpect(jsonPath("$.[*].formato").value(hasItem(DEFAULT_FORMATO.toString())))
            .andExpect(jsonPath("$.[*].destino").value(hasItem(DEFAULT_DESTINO.toString())))
            .andExpect(jsonPath("$.[*].linhas").value(hasItem(DEFAULT_LINHAS)))
            .andExpect(jsonPath("$.[*].checksum").value(hasItem(DEFAULT_CHECKSUM)));

        // Check, that the count call also returns 1
        restCargaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCargaShouldNotBeFound(String filter) throws Exception {
        restCargaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCargaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCarga() throws Exception {
        // Get the carga
        restCargaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarga() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carga
        Carga updatedCarga = cargaRepository.findById(carga.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarga are not directly saved in db
        em.detach(updatedCarga);
        updatedCarga
            .status(UPDATED_STATUS)
            .ordem(UPDATED_ORDEM)
            .planilha(UPDATED_PLANILHA)
            .planilhaContentType(UPDATED_PLANILHA_CONTENT_TYPE)
            .nome(UPDATED_NOME)
            .caminho(UPDATED_CAMINHO)
            .validado(UPDATED_VALIDADO)
            .tipo(UPDATED_TIPO)
            .formato(UPDATED_FORMATO)
            .destino(UPDATED_DESTINO)
            .linhas(UPDATED_LINHAS)
            .checksum(UPDATED_CHECKSUM);

        restCargaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCarga.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCarga))
            )
            .andExpect(status().isOk());

        // Validate the Carga in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCargaToMatchAllProperties(updatedCarga);
    }

    @Test
    @Transactional
    void putNonExistingCarga() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carga.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCargaMockMvc
            .perform(put(ENTITY_API_URL_ID, carga.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carga)))
            .andExpect(status().isBadRequest());

        // Validate the Carga in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarga() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carga.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCargaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carga))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carga in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarga() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carga.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCargaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carga)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carga in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCargaWithPatch() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carga using partial update
        Carga partialUpdatedCarga = new Carga();
        partialUpdatedCarga.setId(carga.getId());

        partialUpdatedCarga
            .caminho(UPDATED_CAMINHO)
            .validado(UPDATED_VALIDADO)
            .formato(UPDATED_FORMATO)
            .destino(UPDATED_DESTINO)
            .linhas(UPDATED_LINHAS);

        restCargaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarga.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarga))
            )
            .andExpect(status().isOk());

        // Validate the Carga in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCargaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCarga, carga), getPersistedCarga(carga));
    }

    @Test
    @Transactional
    void fullUpdateCargaWithPatch() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carga using partial update
        Carga partialUpdatedCarga = new Carga();
        partialUpdatedCarga.setId(carga.getId());

        partialUpdatedCarga
            .status(UPDATED_STATUS)
            .ordem(UPDATED_ORDEM)
            .planilha(UPDATED_PLANILHA)
            .planilhaContentType(UPDATED_PLANILHA_CONTENT_TYPE)
            .nome(UPDATED_NOME)
            .caminho(UPDATED_CAMINHO)
            .validado(UPDATED_VALIDADO)
            .tipo(UPDATED_TIPO)
            .formato(UPDATED_FORMATO)
            .destino(UPDATED_DESTINO)
            .linhas(UPDATED_LINHAS)
            .checksum(UPDATED_CHECKSUM);

        restCargaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarga.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarga))
            )
            .andExpect(status().isOk());

        // Validate the Carga in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCargaUpdatableFieldsEquals(partialUpdatedCarga, getPersistedCarga(partialUpdatedCarga));
    }

    @Test
    @Transactional
    void patchNonExistingCarga() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carga.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCargaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carga.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carga))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carga in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarga() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carga.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCargaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carga))
            )
            .andExpect(status().isBadRequest());

        // Validate the Carga in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarga() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carga.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCargaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carga)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Carga in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCarga() throws Exception {
        // Initialize the database
        insertedCarga = cargaRepository.saveAndFlush(carga);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the carga
        restCargaMockMvc
            .perform(delete(ENTITY_API_URL_ID, carga.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cargaRepository.count();
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

    protected Carga getPersistedCarga(Carga carga) {
        return cargaRepository.findById(carga.getId()).orElseThrow();
    }

    protected void assertPersistedCargaToMatchAllProperties(Carga expectedCarga) {
        assertCargaAllPropertiesEquals(expectedCarga, getPersistedCarga(expectedCarga));
    }

    protected void assertPersistedCargaToMatchUpdatableProperties(Carga expectedCarga) {
        assertCargaAllUpdatablePropertiesEquals(expectedCarga, getPersistedCarga(expectedCarga));
    }
}
