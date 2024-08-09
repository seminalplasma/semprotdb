package org.semprotdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.semprotdb.domain.ReferenciaAsserts.*;
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
import org.semprotdb.domain.Referencia;
import org.semprotdb.repository.ReferenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ReferenciaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReferenciaResourceIT {

    private static final String DEFAULT_CITACAO = "AAAAAAAAAA";
    private static final String UPDATED_CITACAO = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final Integer DEFAULT_ANO = 1;
    private static final Integer UPDATED_ANO = 2;

    private static final String DEFAULT_AUTORES = "AAAAAAAAAA";
    private static final String UPDATED_AUTORES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/referencias";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReferenciaRepository referenciaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReferenciaMockMvc;

    private Referencia referencia;

    private Referencia insertedReferencia;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Referencia createEntity(EntityManager em) {
        Referencia referencia = new Referencia().citacao(DEFAULT_CITACAO).link(DEFAULT_LINK).ano(DEFAULT_ANO).autores(DEFAULT_AUTORES);
        return referencia;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Referencia createUpdatedEntity(EntityManager em) {
        Referencia referencia = new Referencia().citacao(UPDATED_CITACAO).link(UPDATED_LINK).ano(UPDATED_ANO).autores(UPDATED_AUTORES);
        return referencia;
    }

    @BeforeEach
    public void initTest() {
        referencia = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedReferencia != null) {
            referenciaRepository.delete(insertedReferencia);
            insertedReferencia = null;
        }
    }

    @Test
    @Transactional
    void createReferencia() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Referencia
        var returnedReferencia = om.readValue(
            restReferenciaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referencia)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Referencia.class
        );

        // Validate the Referencia in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertReferenciaUpdatableFieldsEquals(returnedReferencia, getPersistedReferencia(returnedReferencia));

        insertedReferencia = returnedReferencia;
    }

    @Test
    @Transactional
    void createReferenciaWithExistingId() throws Exception {
        // Create the Referencia with an existing ID
        referencia.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReferenciaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referencia)))
            .andExpect(status().isBadRequest());

        // Validate the Referencia in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCitacaoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        referencia.setCitacao(null);

        // Create the Referencia, which fails.

        restReferenciaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referencia)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReferencias() throws Exception {
        // Initialize the database
        insertedReferencia = referenciaRepository.saveAndFlush(referencia);

        // Get all the referenciaList
        restReferenciaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(referencia.getId().intValue())))
            .andExpect(jsonPath("$.[*].citacao").value(hasItem(DEFAULT_CITACAO)))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)))
            .andExpect(jsonPath("$.[*].ano").value(hasItem(DEFAULT_ANO)))
            .andExpect(jsonPath("$.[*].autores").value(hasItem(DEFAULT_AUTORES)));
    }

    @Test
    @Transactional
    void getReferencia() throws Exception {
        // Initialize the database
        insertedReferencia = referenciaRepository.saveAndFlush(referencia);

        // Get the referencia
        restReferenciaMockMvc
            .perform(get(ENTITY_API_URL_ID, referencia.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(referencia.getId().intValue()))
            .andExpect(jsonPath("$.citacao").value(DEFAULT_CITACAO))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK))
            .andExpect(jsonPath("$.ano").value(DEFAULT_ANO))
            .andExpect(jsonPath("$.autores").value(DEFAULT_AUTORES));
    }

    @Test
    @Transactional
    void getNonExistingReferencia() throws Exception {
        // Get the referencia
        restReferenciaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReferencia() throws Exception {
        // Initialize the database
        insertedReferencia = referenciaRepository.saveAndFlush(referencia);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the referencia
        Referencia updatedReferencia = referenciaRepository.findById(referencia.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReferencia are not directly saved in db
        em.detach(updatedReferencia);
        updatedReferencia.citacao(UPDATED_CITACAO).link(UPDATED_LINK).ano(UPDATED_ANO).autores(UPDATED_AUTORES);

        restReferenciaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReferencia.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedReferencia))
            )
            .andExpect(status().isOk());

        // Validate the Referencia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReferenciaToMatchAllProperties(updatedReferencia);
    }

    @Test
    @Transactional
    void putNonExistingReferencia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referencia.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferenciaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, referencia.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referencia))
            )
            .andExpect(status().isBadRequest());

        // Validate the Referencia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReferencia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referencia.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenciaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(referencia))
            )
            .andExpect(status().isBadRequest());

        // Validate the Referencia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReferencia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referencia.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenciaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(referencia)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Referencia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReferenciaWithPatch() throws Exception {
        // Initialize the database
        insertedReferencia = referenciaRepository.saveAndFlush(referencia);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the referencia using partial update
        Referencia partialUpdatedReferencia = new Referencia();
        partialUpdatedReferencia.setId(referencia.getId());

        partialUpdatedReferencia.link(UPDATED_LINK).ano(UPDATED_ANO).autores(UPDATED_AUTORES);

        restReferenciaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReferencia.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReferencia))
            )
            .andExpect(status().isOk());

        // Validate the Referencia in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReferenciaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReferencia, referencia),
            getPersistedReferencia(referencia)
        );
    }

    @Test
    @Transactional
    void fullUpdateReferenciaWithPatch() throws Exception {
        // Initialize the database
        insertedReferencia = referenciaRepository.saveAndFlush(referencia);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the referencia using partial update
        Referencia partialUpdatedReferencia = new Referencia();
        partialUpdatedReferencia.setId(referencia.getId());

        partialUpdatedReferencia.citacao(UPDATED_CITACAO).link(UPDATED_LINK).ano(UPDATED_ANO).autores(UPDATED_AUTORES);

        restReferenciaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReferencia.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReferencia))
            )
            .andExpect(status().isOk());

        // Validate the Referencia in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReferenciaUpdatableFieldsEquals(partialUpdatedReferencia, getPersistedReferencia(partialUpdatedReferencia));
    }

    @Test
    @Transactional
    void patchNonExistingReferencia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referencia.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReferenciaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, referencia.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(referencia))
            )
            .andExpect(status().isBadRequest());

        // Validate the Referencia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReferencia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referencia.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenciaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(referencia))
            )
            .andExpect(status().isBadRequest());

        // Validate the Referencia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReferencia() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        referencia.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReferenciaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(referencia)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Referencia in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReferencia() throws Exception {
        // Initialize the database
        insertedReferencia = referenciaRepository.saveAndFlush(referencia);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the referencia
        restReferenciaMockMvc
            .perform(delete(ENTITY_API_URL_ID, referencia.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return referenciaRepository.count();
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

    protected Referencia getPersistedReferencia(Referencia referencia) {
        return referenciaRepository.findById(referencia.getId()).orElseThrow();
    }

    protected void assertPersistedReferenciaToMatchAllProperties(Referencia expectedReferencia) {
        assertReferenciaAllPropertiesEquals(expectedReferencia, getPersistedReferencia(expectedReferencia));
    }

    protected void assertPersistedReferenciaToMatchUpdatableProperties(Referencia expectedReferencia) {
        assertReferenciaAllUpdatablePropertiesEquals(expectedReferencia, getPersistedReferencia(expectedReferencia));
    }
}
