package org.semprotdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.semprotdb.domain.ProteinaAsserts.*;
import static org.semprotdb.web.rest.TestUtil.createUpdateProxyForBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.semprotdb.IntegrationTest;
import org.semprotdb.domain.Curadoria;
import org.semprotdb.domain.Gene;
import org.semprotdb.domain.Proteina;
import org.semprotdb.domain.Recurso;
import org.semprotdb.domain.Referencia;
import org.semprotdb.domain.Versao;
import org.semprotdb.repository.ProteinaRepository;
import org.semprotdb.service.ProteinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProteinaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProteinaResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final Integer DEFAULT_TAMANHO = 1;
    private static final Integer UPDATED_TAMANHO = 2;
    private static final Integer SMALLER_TAMANHO = 1 - 1;

    private static final String DEFAULT_MASSA = "AAAAAAAAAA";
    private static final String UPDATED_MASSA = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/proteinas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProteinaRepository proteinaRepository;

    @Mock
    private ProteinaRepository proteinaRepositoryMock;

    @Mock
    private ProteinaService proteinaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProteinaMockMvc;

    private Proteina proteina;

    private Proteina insertedProteina;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Proteina createEntity(EntityManager em) {
        Proteina proteina = new Proteina().nome(DEFAULT_NOME).tamanho(DEFAULT_TAMANHO).massa(DEFAULT_MASSA).descricao(DEFAULT_DESCRICAO);
        return proteina;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Proteina createUpdatedEntity(EntityManager em) {
        Proteina proteina = new Proteina().nome(UPDATED_NOME).tamanho(UPDATED_TAMANHO).massa(UPDATED_MASSA).descricao(UPDATED_DESCRICAO);
        return proteina;
    }

    @BeforeEach
    public void initTest() {
        proteina = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProteina != null) {
            proteinaRepository.delete(insertedProteina);
            insertedProteina = null;
        }
    }

    @Test
    @Transactional
    void createProteina() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Proteina
        var returnedProteina = om.readValue(
            restProteinaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proteina)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Proteina.class
        );

        // Validate the Proteina in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProteinaUpdatableFieldsEquals(returnedProteina, getPersistedProteina(returnedProteina));

        insertedProteina = returnedProteina;
    }

    @Test
    @Transactional
    void createProteinaWithExistingId() throws Exception {
        // Create the Proteina with an existing ID
        proteina.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProteinaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proteina)))
            .andExpect(status().isBadRequest());

        // Validate the Proteina in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        proteina.setNome(null);

        // Create the Proteina, which fails.

        restProteinaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proteina)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProteinas() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList
        restProteinaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proteina.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].tamanho").value(hasItem(DEFAULT_TAMANHO)))
            .andExpect(jsonPath("$.[*].massa").value(hasItem(DEFAULT_MASSA)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProteinasWithEagerRelationshipsIsEnabled() throws Exception {
        when(proteinaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProteinaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(proteinaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProteinasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(proteinaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProteinaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(proteinaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProteina() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get the proteina
        restProteinaMockMvc
            .perform(get(ENTITY_API_URL_ID, proteina.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(proteina.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.tamanho").value(DEFAULT_TAMANHO))
            .andExpect(jsonPath("$.massa").value(DEFAULT_MASSA))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO));
    }

    @Test
    @Transactional
    void getProteinasByIdFiltering() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        Long id = proteina.getId();

        defaultProteinaFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProteinaFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProteinaFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProteinasByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where nome equals to
        defaultProteinaFiltering("nome.equals=" + DEFAULT_NOME, "nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllProteinasByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where nome in
        defaultProteinaFiltering("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME, "nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllProteinasByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where nome is not null
        defaultProteinaFiltering("nome.specified=true", "nome.specified=false");
    }

    @Test
    @Transactional
    void getAllProteinasByNomeContainsSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where nome contains
        defaultProteinaFiltering("nome.contains=" + DEFAULT_NOME, "nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllProteinasByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where nome does not contain
        defaultProteinaFiltering("nome.doesNotContain=" + UPDATED_NOME, "nome.doesNotContain=" + DEFAULT_NOME);
    }

    @Test
    @Transactional
    void getAllProteinasByTamanhoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where tamanho equals to
        defaultProteinaFiltering("tamanho.equals=" + DEFAULT_TAMANHO, "tamanho.equals=" + UPDATED_TAMANHO);
    }

    @Test
    @Transactional
    void getAllProteinasByTamanhoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where tamanho in
        defaultProteinaFiltering("tamanho.in=" + DEFAULT_TAMANHO + "," + UPDATED_TAMANHO, "tamanho.in=" + UPDATED_TAMANHO);
    }

    @Test
    @Transactional
    void getAllProteinasByTamanhoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where tamanho is not null
        defaultProteinaFiltering("tamanho.specified=true", "tamanho.specified=false");
    }

    @Test
    @Transactional
    void getAllProteinasByTamanhoIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where tamanho is greater than or equal to
        defaultProteinaFiltering("tamanho.greaterThanOrEqual=" + DEFAULT_TAMANHO, "tamanho.greaterThanOrEqual=" + UPDATED_TAMANHO);
    }

    @Test
    @Transactional
    void getAllProteinasByTamanhoIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where tamanho is less than or equal to
        defaultProteinaFiltering("tamanho.lessThanOrEqual=" + DEFAULT_TAMANHO, "tamanho.lessThanOrEqual=" + SMALLER_TAMANHO);
    }

    @Test
    @Transactional
    void getAllProteinasByTamanhoIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where tamanho is less than
        defaultProteinaFiltering("tamanho.lessThan=" + UPDATED_TAMANHO, "tamanho.lessThan=" + DEFAULT_TAMANHO);
    }

    @Test
    @Transactional
    void getAllProteinasByTamanhoIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where tamanho is greater than
        defaultProteinaFiltering("tamanho.greaterThan=" + SMALLER_TAMANHO, "tamanho.greaterThan=" + DEFAULT_TAMANHO);
    }

    @Test
    @Transactional
    void getAllProteinasByMassaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where massa equals to
        defaultProteinaFiltering("massa.equals=" + DEFAULT_MASSA, "massa.equals=" + UPDATED_MASSA);
    }

    @Test
    @Transactional
    void getAllProteinasByMassaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where massa in
        defaultProteinaFiltering("massa.in=" + DEFAULT_MASSA + "," + UPDATED_MASSA, "massa.in=" + UPDATED_MASSA);
    }

    @Test
    @Transactional
    void getAllProteinasByMassaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where massa is not null
        defaultProteinaFiltering("massa.specified=true", "massa.specified=false");
    }

    @Test
    @Transactional
    void getAllProteinasByMassaContainsSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where massa contains
        defaultProteinaFiltering("massa.contains=" + DEFAULT_MASSA, "massa.contains=" + UPDATED_MASSA);
    }

    @Test
    @Transactional
    void getAllProteinasByMassaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where massa does not contain
        defaultProteinaFiltering("massa.doesNotContain=" + UPDATED_MASSA, "massa.doesNotContain=" + DEFAULT_MASSA);
    }

    @Test
    @Transactional
    void getAllProteinasByDescricaoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where descricao equals to
        defaultProteinaFiltering("descricao.equals=" + DEFAULT_DESCRICAO, "descricao.equals=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllProteinasByDescricaoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where descricao in
        defaultProteinaFiltering("descricao.in=" + DEFAULT_DESCRICAO + "," + UPDATED_DESCRICAO, "descricao.in=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllProteinasByDescricaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where descricao is not null
        defaultProteinaFiltering("descricao.specified=true", "descricao.specified=false");
    }

    @Test
    @Transactional
    void getAllProteinasByDescricaoContainsSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where descricao contains
        defaultProteinaFiltering("descricao.contains=" + DEFAULT_DESCRICAO, "descricao.contains=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllProteinasByDescricaoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        // Get all the proteinaList where descricao does not contain
        defaultProteinaFiltering("descricao.doesNotContain=" + UPDATED_DESCRICAO, "descricao.doesNotContain=" + DEFAULT_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllProteinasByCuradoriaIsEqualToSomething() throws Exception {
        Curadoria curadoria;
        if (TestUtil.findAll(em, Curadoria.class).isEmpty()) {
            proteinaRepository.saveAndFlush(proteina);
            curadoria = CuradoriaResourceIT.createEntity(em);
        } else {
            curadoria = TestUtil.findAll(em, Curadoria.class).get(0);
        }
        em.persist(curadoria);
        em.flush();
        proteina.setCuradoria(curadoria);
        proteinaRepository.saveAndFlush(proteina);
        Long curadoriaId = curadoria.getId();
        // Get all the proteinaList where curadoria equals to curadoriaId
        defaultProteinaShouldBeFound("curadoriaId.equals=" + curadoriaId);

        // Get all the proteinaList where curadoria equals to (curadoriaId + 1)
        defaultProteinaShouldNotBeFound("curadoriaId.equals=" + (curadoriaId + 1));
    }

    @Test
    @Transactional
    void getAllProteinasByVersaoIsEqualToSomething() throws Exception {
        Versao versao;
        if (TestUtil.findAll(em, Versao.class).isEmpty()) {
            proteinaRepository.saveAndFlush(proteina);
            versao = VersaoResourceIT.createEntity(em);
        } else {
            versao = TestUtil.findAll(em, Versao.class).get(0);
        }
        em.persist(versao);
        em.flush();
        proteina.setVersao(versao);
        proteinaRepository.saveAndFlush(proteina);
        Long versaoId = versao.getId();
        // Get all the proteinaList where versao equals to versaoId
        defaultProteinaShouldBeFound("versaoId.equals=" + versaoId);

        // Get all the proteinaList where versao equals to (versaoId + 1)
        defaultProteinaShouldNotBeFound("versaoId.equals=" + (versaoId + 1));
    }

    @Test
    @Transactional
    void getAllProteinasByGeneIsEqualToSomething() throws Exception {
        Gene gene;
        if (TestUtil.findAll(em, Gene.class).isEmpty()) {
            proteinaRepository.saveAndFlush(proteina);
            gene = GeneResourceIT.createEntity(em);
        } else {
            gene = TestUtil.findAll(em, Gene.class).get(0);
        }
        em.persist(gene);
        em.flush();
        proteina.setGene(gene);
        proteinaRepository.saveAndFlush(proteina);
        Long geneId = gene.getId();
        // Get all the proteinaList where gene equals to geneId
        defaultProteinaShouldBeFound("geneId.equals=" + geneId);

        // Get all the proteinaList where gene equals to (geneId + 1)
        defaultProteinaShouldNotBeFound("geneId.equals=" + (geneId + 1));
    }

    @Test
    @Transactional
    void getAllProteinasByReferenciaIsEqualToSomething() throws Exception {
        Referencia referencia;
        if (TestUtil.findAll(em, Referencia.class).isEmpty()) {
            proteinaRepository.saveAndFlush(proteina);
            referencia = ReferenciaResourceIT.createEntity(em);
        } else {
            referencia = TestUtil.findAll(em, Referencia.class).get(0);
        }
        em.persist(referencia);
        em.flush();
        proteina.addReferencia(referencia);
        proteinaRepository.saveAndFlush(proteina);
        Long referenciaId = referencia.getId();
        // Get all the proteinaList where referencia equals to referenciaId
        defaultProteinaShouldBeFound("referenciaId.equals=" + referenciaId);

        // Get all the proteinaList where referencia equals to (referenciaId + 1)
        defaultProteinaShouldNotBeFound("referenciaId.equals=" + (referenciaId + 1));
    }

    @Test
    @Transactional
    void getAllProteinasByRecursoIsEqualToSomething() throws Exception {
        Recurso recurso;
        if (TestUtil.findAll(em, Recurso.class).isEmpty()) {
            proteinaRepository.saveAndFlush(proteina);
            recurso = RecursoResourceIT.createEntity(em);
        } else {
            recurso = TestUtil.findAll(em, Recurso.class).get(0);
        }
        em.persist(recurso);
        em.flush();
        proteina.addRecurso(recurso);
        proteinaRepository.saveAndFlush(proteina);
        Long recursoId = recurso.getId();
        // Get all the proteinaList where recurso equals to recursoId
        defaultProteinaShouldBeFound("recursoId.equals=" + recursoId);

        // Get all the proteinaList where recurso equals to (recursoId + 1)
        defaultProteinaShouldNotBeFound("recursoId.equals=" + (recursoId + 1));
    }

    private void defaultProteinaFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProteinaShouldBeFound(shouldBeFound);
        defaultProteinaShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProteinaShouldBeFound(String filter) throws Exception {
        restProteinaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proteina.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].tamanho").value(hasItem(DEFAULT_TAMANHO)))
            .andExpect(jsonPath("$.[*].massa").value(hasItem(DEFAULT_MASSA)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO)));

        // Check, that the count call also returns 1
        restProteinaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProteinaShouldNotBeFound(String filter) throws Exception {
        restProteinaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProteinaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProteina() throws Exception {
        // Get the proteina
        restProteinaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProteina() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proteina
        Proteina updatedProteina = proteinaRepository.findById(proteina.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProteina are not directly saved in db
        em.detach(updatedProteina);
        updatedProteina.nome(UPDATED_NOME).tamanho(UPDATED_TAMANHO).massa(UPDATED_MASSA).descricao(UPDATED_DESCRICAO);

        restProteinaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProteina.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProteina))
            )
            .andExpect(status().isOk());

        // Validate the Proteina in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProteinaToMatchAllProperties(updatedProteina);
    }

    @Test
    @Transactional
    void putNonExistingProteina() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proteina.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProteinaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, proteina.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proteina))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proteina in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProteina() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proteina.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProteinaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(proteina))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proteina in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProteina() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proteina.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProteinaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(proteina)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Proteina in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProteinaWithPatch() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proteina using partial update
        Proteina partialUpdatedProteina = new Proteina();
        partialUpdatedProteina.setId(proteina.getId());

        partialUpdatedProteina.nome(UPDATED_NOME).tamanho(UPDATED_TAMANHO).descricao(UPDATED_DESCRICAO);

        restProteinaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProteina.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProteina))
            )
            .andExpect(status().isOk());

        // Validate the Proteina in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProteinaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProteina, proteina), getPersistedProteina(proteina));
    }

    @Test
    @Transactional
    void fullUpdateProteinaWithPatch() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the proteina using partial update
        Proteina partialUpdatedProteina = new Proteina();
        partialUpdatedProteina.setId(proteina.getId());

        partialUpdatedProteina.nome(UPDATED_NOME).tamanho(UPDATED_TAMANHO).massa(UPDATED_MASSA).descricao(UPDATED_DESCRICAO);

        restProteinaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProteina.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProteina))
            )
            .andExpect(status().isOk());

        // Validate the Proteina in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProteinaUpdatableFieldsEquals(partialUpdatedProteina, getPersistedProteina(partialUpdatedProteina));
    }

    @Test
    @Transactional
    void patchNonExistingProteina() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proteina.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProteinaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, proteina.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(proteina))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proteina in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProteina() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proteina.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProteinaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(proteina))
            )
            .andExpect(status().isBadRequest());

        // Validate the Proteina in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProteina() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        proteina.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProteinaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(proteina)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Proteina in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProteina() throws Exception {
        // Initialize the database
        insertedProteina = proteinaRepository.saveAndFlush(proteina);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the proteina
        restProteinaMockMvc
            .perform(delete(ENTITY_API_URL_ID, proteina.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return proteinaRepository.count();
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

    protected Proteina getPersistedProteina(Proteina proteina) {
        return proteinaRepository.findById(proteina.getId()).orElseThrow();
    }

    protected void assertPersistedProteinaToMatchAllProperties(Proteina expectedProteina) {
        assertProteinaAllPropertiesEquals(expectedProteina, getPersistedProteina(expectedProteina));
    }

    protected void assertPersistedProteinaToMatchUpdatableProperties(Proteina expectedProteina) {
        assertProteinaAllUpdatablePropertiesEquals(expectedProteina, getPersistedProteina(expectedProteina));
    }
}
