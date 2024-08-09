package org.semprotdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.semprotdb.domain.CuradoriaAsserts.*;
import static org.semprotdb.web.rest.TestUtil.createUpdateProxyForBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semprotdb.IntegrationTest;
import org.semprotdb.domain.Curadoria;
import org.semprotdb.repository.CuradoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CuradoriaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CuradoriaResourceIT {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ANOTACOES = "AAAAAAAAAA";
    private static final String UPDATED_ANOTACOES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/curadorias";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CuradoriaRepository curadoriaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCuradoriaMockMvc;

    private Curadoria curadoria;

    private Curadoria insertedCuradoria;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Curadoria createEntity(EntityManager em) {
        Curadoria curadoria = new Curadoria().email(DEFAULT_EMAIL).data(DEFAULT_DATA).anotacoes(DEFAULT_ANOTACOES);
        return curadoria;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Curadoria createUpdatedEntity(EntityManager em) {
        Curadoria curadoria = new Curadoria().email(UPDATED_EMAIL).data(UPDATED_DATA).anotacoes(UPDATED_ANOTACOES);
        return curadoria;
    }

    @BeforeEach
    public void initTest() {
        curadoria = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedCuradoria != null) {
            curadoriaRepository.delete(insertedCuradoria);
            insertedCuradoria = null;
        }
    }

    @Test
    @Transactional
    void createCuradoria() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Curadoria
        var returnedCuradoria = om.readValue(
            restCuradoriaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(curadoria)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Curadoria.class
        );

        // Validate the Curadoria in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCuradoriaUpdatableFieldsEquals(returnedCuradoria, getPersistedCuradoria(returnedCuradoria));

        insertedCuradoria = returnedCuradoria;
    }

    @Test
    @Transactional
    void createCuradoriaWithExistingId() throws Exception {
        // Create the Curadoria with an existing ID
        curadoria.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCuradoriaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(curadoria)))
            .andExpect(status().isBadRequest());

        // Validate the Curadoria in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        curadoria.setEmail(null);

        // Create the Curadoria, which fails.

        restCuradoriaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(curadoria)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDataIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        curadoria.setData(null);

        // Create the Curadoria, which fails.

        restCuradoriaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(curadoria)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCuradorias() throws Exception {
        // Initialize the database
        insertedCuradoria = curadoriaRepository.saveAndFlush(curadoria);

        // Get all the curadoriaList
        restCuradoriaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(curadoria.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA.toString())))
            .andExpect(jsonPath("$.[*].anotacoes").value(hasItem(DEFAULT_ANOTACOES)));
    }

    @Test
    @Transactional
    void getCuradoria() throws Exception {
        // Initialize the database
        insertedCuradoria = curadoriaRepository.saveAndFlush(curadoria);

        // Get the curadoria
        restCuradoriaMockMvc
            .perform(get(ENTITY_API_URL_ID, curadoria.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(curadoria.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()))
            .andExpect(jsonPath("$.anotacoes").value(DEFAULT_ANOTACOES));
    }

    @Test
    @Transactional
    void getNonExistingCuradoria() throws Exception {
        // Get the curadoria
        restCuradoriaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCuradoria() throws Exception {
        // Initialize the database
        insertedCuradoria = curadoriaRepository.saveAndFlush(curadoria);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the curadoria
        Curadoria updatedCuradoria = curadoriaRepository.findById(curadoria.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCuradoria are not directly saved in db
        em.detach(updatedCuradoria);
        updatedCuradoria.email(UPDATED_EMAIL).data(UPDATED_DATA).anotacoes(UPDATED_ANOTACOES);

        restCuradoriaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCuradoria.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCuradoria))
            )
            .andExpect(status().isOk());

        // Validate the Curadoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCuradoriaToMatchAllProperties(updatedCuradoria);
    }

    @Test
    @Transactional
    void putNonExistingCuradoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        curadoria.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCuradoriaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, curadoria.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(curadoria))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curadoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCuradoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        curadoria.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuradoriaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(curadoria))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curadoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCuradoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        curadoria.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuradoriaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(curadoria)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Curadoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCuradoriaWithPatch() throws Exception {
        // Initialize the database
        insertedCuradoria = curadoriaRepository.saveAndFlush(curadoria);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the curadoria using partial update
        Curadoria partialUpdatedCuradoria = new Curadoria();
        partialUpdatedCuradoria.setId(curadoria.getId());

        partialUpdatedCuradoria.email(UPDATED_EMAIL).data(UPDATED_DATA);

        restCuradoriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCuradoria.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCuradoria))
            )
            .andExpect(status().isOk());

        // Validate the Curadoria in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCuradoriaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCuradoria, curadoria),
            getPersistedCuradoria(curadoria)
        );
    }

    @Test
    @Transactional
    void fullUpdateCuradoriaWithPatch() throws Exception {
        // Initialize the database
        insertedCuradoria = curadoriaRepository.saveAndFlush(curadoria);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the curadoria using partial update
        Curadoria partialUpdatedCuradoria = new Curadoria();
        partialUpdatedCuradoria.setId(curadoria.getId());

        partialUpdatedCuradoria.email(UPDATED_EMAIL).data(UPDATED_DATA).anotacoes(UPDATED_ANOTACOES);

        restCuradoriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCuradoria.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCuradoria))
            )
            .andExpect(status().isOk());

        // Validate the Curadoria in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCuradoriaUpdatableFieldsEquals(partialUpdatedCuradoria, getPersistedCuradoria(partialUpdatedCuradoria));
    }

    @Test
    @Transactional
    void patchNonExistingCuradoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        curadoria.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCuradoriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, curadoria.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(curadoria))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curadoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCuradoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        curadoria.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuradoriaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(curadoria))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curadoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCuradoria() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        curadoria.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCuradoriaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(curadoria)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Curadoria in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCuradoria() throws Exception {
        // Initialize the database
        insertedCuradoria = curadoriaRepository.saveAndFlush(curadoria);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the curadoria
        restCuradoriaMockMvc
            .perform(delete(ENTITY_API_URL_ID, curadoria.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return curadoriaRepository.count();
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

    protected Curadoria getPersistedCuradoria(Curadoria curadoria) {
        return curadoriaRepository.findById(curadoria.getId()).orElseThrow();
    }

    protected void assertPersistedCuradoriaToMatchAllProperties(Curadoria expectedCuradoria) {
        assertCuradoriaAllPropertiesEquals(expectedCuradoria, getPersistedCuradoria(expectedCuradoria));
    }

    protected void assertPersistedCuradoriaToMatchUpdatableProperties(Curadoria expectedCuradoria) {
        assertCuradoriaAllUpdatablePropertiesEquals(expectedCuradoria, getPersistedCuradoria(expectedCuradoria));
    }
}
