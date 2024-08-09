package org.semprotdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.semprotdb.domain.RecursoAsserts.*;
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
import org.semprotdb.domain.Recurso;
import org.semprotdb.domain.enumeration.BioDB;
import org.semprotdb.repository.RecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RecursoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RecursoResourceIT {

    private static final String DEFAULT_UID = "AAAAAAAAAA";
    private static final String UPDATED_UID = "BBBBBBBBBB";

    private static final BioDB DEFAULT_DB = BioDB.UNIPROT;
    private static final BioDB UPDATED_DB = BioDB.INTERPRO;

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/recursos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RecursoRepository recursoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecursoMockMvc;

    private Recurso recurso;

    private Recurso insertedRecurso;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recurso createEntity(EntityManager em) {
        Recurso recurso = new Recurso().uid(DEFAULT_UID).db(DEFAULT_DB).link(DEFAULT_LINK);
        return recurso;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recurso createUpdatedEntity(EntityManager em) {
        Recurso recurso = new Recurso().uid(UPDATED_UID).db(UPDATED_DB).link(UPDATED_LINK);
        return recurso;
    }

    @BeforeEach
    public void initTest() {
        recurso = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedRecurso != null) {
            recursoRepository.delete(insertedRecurso);
            insertedRecurso = null;
        }
    }

    @Test
    @Transactional
    void createRecurso() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Recurso
        var returnedRecurso = om.readValue(
            restRecursoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(recurso)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Recurso.class
        );

        // Validate the Recurso in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRecursoUpdatableFieldsEquals(returnedRecurso, getPersistedRecurso(returnedRecurso));

        insertedRecurso = returnedRecurso;
    }

    @Test
    @Transactional
    void createRecursoWithExistingId() throws Exception {
        // Create the Recurso with an existing ID
        recurso.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecursoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(recurso)))
            .andExpect(status().isBadRequest());

        // Validate the Recurso in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUidIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        recurso.setUid(null);

        // Create the Recurso, which fails.

        restRecursoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(recurso)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRecursos() throws Exception {
        // Initialize the database
        insertedRecurso = recursoRepository.saveAndFlush(recurso);

        // Get all the recursoList
        restRecursoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recurso.getId().intValue())))
            .andExpect(jsonPath("$.[*].uid").value(hasItem(DEFAULT_UID)))
            .andExpect(jsonPath("$.[*].db").value(hasItem(DEFAULT_DB.toString())))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)));
    }

    @Test
    @Transactional
    void getRecurso() throws Exception {
        // Initialize the database
        insertedRecurso = recursoRepository.saveAndFlush(recurso);

        // Get the recurso
        restRecursoMockMvc
            .perform(get(ENTITY_API_URL_ID, recurso.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recurso.getId().intValue()))
            .andExpect(jsonPath("$.uid").value(DEFAULT_UID))
            .andExpect(jsonPath("$.db").value(DEFAULT_DB.toString()))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK));
    }

    @Test
    @Transactional
    void getNonExistingRecurso() throws Exception {
        // Get the recurso
        restRecursoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRecurso() throws Exception {
        // Initialize the database
        insertedRecurso = recursoRepository.saveAndFlush(recurso);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the recurso
        Recurso updatedRecurso = recursoRepository.findById(recurso.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRecurso are not directly saved in db
        em.detach(updatedRecurso);
        updatedRecurso.uid(UPDATED_UID).db(UPDATED_DB).link(UPDATED_LINK);

        restRecursoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRecurso.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRecurso))
            )
            .andExpect(status().isOk());

        // Validate the Recurso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRecursoToMatchAllProperties(updatedRecurso);
    }

    @Test
    @Transactional
    void putNonExistingRecurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recurso.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecursoMockMvc
            .perform(put(ENTITY_API_URL_ID, recurso.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(recurso)))
            .andExpect(status().isBadRequest());

        // Validate the Recurso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recurso.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecursoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(recurso))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recurso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recurso.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecursoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(recurso)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recurso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRecursoWithPatch() throws Exception {
        // Initialize the database
        insertedRecurso = recursoRepository.saveAndFlush(recurso);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the recurso using partial update
        Recurso partialUpdatedRecurso = new Recurso();
        partialUpdatedRecurso.setId(recurso.getId());

        partialUpdatedRecurso.uid(UPDATED_UID);

        restRecursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecurso.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRecurso))
            )
            .andExpect(status().isOk());

        // Validate the Recurso in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRecursoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRecurso, recurso), getPersistedRecurso(recurso));
    }

    @Test
    @Transactional
    void fullUpdateRecursoWithPatch() throws Exception {
        // Initialize the database
        insertedRecurso = recursoRepository.saveAndFlush(recurso);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the recurso using partial update
        Recurso partialUpdatedRecurso = new Recurso();
        partialUpdatedRecurso.setId(recurso.getId());

        partialUpdatedRecurso.uid(UPDATED_UID).db(UPDATED_DB).link(UPDATED_LINK);

        restRecursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRecurso.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRecurso))
            )
            .andExpect(status().isOk());

        // Validate the Recurso in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRecursoUpdatableFieldsEquals(partialUpdatedRecurso, getPersistedRecurso(partialUpdatedRecurso));
    }

    @Test
    @Transactional
    void patchNonExistingRecurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recurso.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, recurso.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(recurso))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recurso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recurso.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(recurso))
            )
            .andExpect(status().isBadRequest());

        // Validate the Recurso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecurso() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        recurso.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecursoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(recurso)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Recurso in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRecurso() throws Exception {
        // Initialize the database
        insertedRecurso = recursoRepository.saveAndFlush(recurso);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the recurso
        restRecursoMockMvc
            .perform(delete(ENTITY_API_URL_ID, recurso.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return recursoRepository.count();
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

    protected Recurso getPersistedRecurso(Recurso recurso) {
        return recursoRepository.findById(recurso.getId()).orElseThrow();
    }

    protected void assertPersistedRecursoToMatchAllProperties(Recurso expectedRecurso) {
        assertRecursoAllPropertiesEquals(expectedRecurso, getPersistedRecurso(expectedRecurso));
    }

    protected void assertPersistedRecursoToMatchUpdatableProperties(Recurso expectedRecurso) {
        assertRecursoAllUpdatablePropertiesEquals(expectedRecurso, getPersistedRecurso(expectedRecurso));
    }
}
