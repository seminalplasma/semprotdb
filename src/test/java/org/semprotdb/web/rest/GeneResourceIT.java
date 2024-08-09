package org.semprotdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.semprotdb.domain.GeneAsserts.*;
import static org.semprotdb.web.rest.TestUtil.createUpdateProxyForBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semprotdb.IntegrationTest;
import org.semprotdb.domain.Curadoria;
import org.semprotdb.domain.Gene;
import org.semprotdb.domain.Organismo;
import org.semprotdb.repository.GeneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GeneResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GeneResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/genes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GeneRepository geneRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGeneMockMvc;

    private Gene gene;

    private Gene insertedGene;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gene createEntity(EntityManager em) {
        Gene gene = new Gene().nome(DEFAULT_NOME).descricao(DEFAULT_DESCRICAO);
        return gene;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gene createUpdatedEntity(EntityManager em) {
        Gene gene = new Gene().nome(UPDATED_NOME).descricao(UPDATED_DESCRICAO);
        return gene;
    }

    @BeforeEach
    public void initTest() {
        gene = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedGene != null) {
            geneRepository.delete(insertedGene);
            insertedGene = null;
        }
    }

    @Test
    @Transactional
    void createGene() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Gene
        var returnedGene = om.readValue(
            restGeneMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gene)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Gene.class
        );

        // Validate the Gene in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertGeneUpdatableFieldsEquals(returnedGene, getPersistedGene(returnedGene));

        insertedGene = returnedGene;
    }

    @Test
    @Transactional
    void createGeneWithExistingId() throws Exception {
        // Create the Gene with an existing ID
        gene.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGeneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gene)))
            .andExpect(status().isBadRequest());

        // Validate the Gene in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gene.setNome(null);

        // Create the Gene, which fails.

        restGeneMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gene)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGenes() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList
        restGeneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gene.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO)));
    }

    @Test
    @Transactional
    void getGene() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get the gene
        restGeneMockMvc
            .perform(get(ENTITY_API_URL_ID, gene.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gene.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO));
    }

    @Test
    @Transactional
    void getGenesByIdFiltering() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        Long id = gene.getId();

        defaultGeneFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultGeneFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultGeneFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGenesByNomeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where nome equals to
        defaultGeneFiltering("nome.equals=" + DEFAULT_NOME, "nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllGenesByNomeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where nome in
        defaultGeneFiltering("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME, "nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllGenesByNomeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where nome is not null
        defaultGeneFiltering("nome.specified=true", "nome.specified=false");
    }

    @Test
    @Transactional
    void getAllGenesByNomeContainsSomething() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where nome contains
        defaultGeneFiltering("nome.contains=" + DEFAULT_NOME, "nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllGenesByNomeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where nome does not contain
        defaultGeneFiltering("nome.doesNotContain=" + UPDATED_NOME, "nome.doesNotContain=" + DEFAULT_NOME);
    }

    @Test
    @Transactional
    void getAllGenesByDescricaoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where descricao equals to
        defaultGeneFiltering("descricao.equals=" + DEFAULT_DESCRICAO, "descricao.equals=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllGenesByDescricaoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where descricao in
        defaultGeneFiltering("descricao.in=" + DEFAULT_DESCRICAO + "," + UPDATED_DESCRICAO, "descricao.in=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllGenesByDescricaoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where descricao is not null
        defaultGeneFiltering("descricao.specified=true", "descricao.specified=false");
    }

    @Test
    @Transactional
    void getAllGenesByDescricaoContainsSomething() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where descricao contains
        defaultGeneFiltering("descricao.contains=" + DEFAULT_DESCRICAO, "descricao.contains=" + UPDATED_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllGenesByDescricaoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        // Get all the geneList where descricao does not contain
        defaultGeneFiltering("descricao.doesNotContain=" + UPDATED_DESCRICAO, "descricao.doesNotContain=" + DEFAULT_DESCRICAO);
    }

    @Test
    @Transactional
    void getAllGenesByCuradoriaIsEqualToSomething() throws Exception {
        Curadoria curadoria;
        if (TestUtil.findAll(em, Curadoria.class).isEmpty()) {
            geneRepository.saveAndFlush(gene);
            curadoria = CuradoriaResourceIT.createEntity(em);
        } else {
            curadoria = TestUtil.findAll(em, Curadoria.class).get(0);
        }
        em.persist(curadoria);
        em.flush();
        gene.setCuradoria(curadoria);
        geneRepository.saveAndFlush(gene);
        Long curadoriaId = curadoria.getId();
        // Get all the geneList where curadoria equals to curadoriaId
        defaultGeneShouldBeFound("curadoriaId.equals=" + curadoriaId);

        // Get all the geneList where curadoria equals to (curadoriaId + 1)
        defaultGeneShouldNotBeFound("curadoriaId.equals=" + (curadoriaId + 1));
    }

    @Test
    @Transactional
    void getAllGenesByOrganismoIsEqualToSomething() throws Exception {
        Organismo organismo;
        if (TestUtil.findAll(em, Organismo.class).isEmpty()) {
            geneRepository.saveAndFlush(gene);
            organismo = OrganismoResourceIT.createEntity(em);
        } else {
            organismo = TestUtil.findAll(em, Organismo.class).get(0);
        }
        em.persist(organismo);
        em.flush();
        gene.setOrganismo(organismo);
        geneRepository.saveAndFlush(gene);
        Long organismoId = organismo.getId();
        // Get all the geneList where organismo equals to organismoId
        defaultGeneShouldBeFound("organismoId.equals=" + organismoId);

        // Get all the geneList where organismo equals to (organismoId + 1)
        defaultGeneShouldNotBeFound("organismoId.equals=" + (organismoId + 1));
    }

    private void defaultGeneFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultGeneShouldBeFound(shouldBeFound);
        defaultGeneShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGeneShouldBeFound(String filter) throws Exception {
        restGeneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gene.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO)));

        // Check, that the count call also returns 1
        restGeneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGeneShouldNotBeFound(String filter) throws Exception {
        restGeneMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGeneMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGene() throws Exception {
        // Get the gene
        restGeneMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGene() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gene
        Gene updatedGene = geneRepository.findById(gene.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGene are not directly saved in db
        em.detach(updatedGene);
        updatedGene.nome(UPDATED_NOME).descricao(UPDATED_DESCRICAO);

        restGeneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGene.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedGene))
            )
            .andExpect(status().isOk());

        // Validate the Gene in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGeneToMatchAllProperties(updatedGene);
    }

    @Test
    @Transactional
    void putNonExistingGene() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gene.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGeneMockMvc
            .perform(put(ENTITY_API_URL_ID, gene.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gene)))
            .andExpect(status().isBadRequest());

        // Validate the Gene in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGene() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gene.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGeneMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gene))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gene in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGene() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gene.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGeneMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gene)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Gene in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGeneWithPatch() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gene using partial update
        Gene partialUpdatedGene = new Gene();
        partialUpdatedGene.setId(gene.getId());

        partialUpdatedGene.nome(UPDATED_NOME);

        restGeneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGene.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGene))
            )
            .andExpect(status().isOk());

        // Validate the Gene in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGeneUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGene, gene), getPersistedGene(gene));
    }

    @Test
    @Transactional
    void fullUpdateGeneWithPatch() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gene using partial update
        Gene partialUpdatedGene = new Gene();
        partialUpdatedGene.setId(gene.getId());

        partialUpdatedGene.nome(UPDATED_NOME).descricao(UPDATED_DESCRICAO);

        restGeneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGene.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGene))
            )
            .andExpect(status().isOk());

        // Validate the Gene in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGeneUpdatableFieldsEquals(partialUpdatedGene, getPersistedGene(partialUpdatedGene));
    }

    @Test
    @Transactional
    void patchNonExistingGene() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gene.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGeneMockMvc
            .perform(patch(ENTITY_API_URL_ID, gene.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(gene)))
            .andExpect(status().isBadRequest());

        // Validate the Gene in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGene() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gene.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGeneMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gene))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gene in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGene() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gene.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGeneMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(gene)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Gene in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGene() throws Exception {
        // Initialize the database
        insertedGene = geneRepository.saveAndFlush(gene);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the gene
        restGeneMockMvc
            .perform(delete(ENTITY_API_URL_ID, gene.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return geneRepository.count();
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

    protected Gene getPersistedGene(Gene gene) {
        return geneRepository.findById(gene.getId()).orElseThrow();
    }

    protected void assertPersistedGeneToMatchAllProperties(Gene expectedGene) {
        assertGeneAllPropertiesEquals(expectedGene, getPersistedGene(expectedGene));
    }

    protected void assertPersistedGeneToMatchUpdatableProperties(Gene expectedGene) {
        assertGeneAllUpdatablePropertiesEquals(expectedGene, getPersistedGene(expectedGene));
    }
}
