package org.semprotdb.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.semprotdb.domain.DBConfigAsserts.*;
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
import org.semprotdb.domain.DBConfig;
import org.semprotdb.repository.DBConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DBConfigResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DBConfigResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_HABILITADO = false;
    private static final Boolean UPDATED_HABILITADO = true;

    private static final String DEFAULT_VSTRING = "AAAAAAAAAA";
    private static final String UPDATED_VSTRING = "BBBBBBBBBB";

    private static final Boolean DEFAULT_VBOL = false;
    private static final Boolean UPDATED_VBOL = true;

    private static final Instant DEFAULT_VDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_VINT = 1;
    private static final Integer UPDATED_VINT = 2;

    private static final String DEFAULT_VTEXT = "AAAAAAAAAA";
    private static final String UPDATED_VTEXT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_VIMG = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_VIMG = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_VIMG_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_VIMG_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/db-configs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DBConfigRepository dBConfigRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDBConfigMockMvc;

    private DBConfig dBConfig;

    private DBConfig insertedDBConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DBConfig createEntity(EntityManager em) {
        DBConfig dBConfig = new DBConfig()
            .key(DEFAULT_KEY)
            .habilitado(DEFAULT_HABILITADO)
            .vstring(DEFAULT_VSTRING)
            .vbol(DEFAULT_VBOL)
            .vdate(DEFAULT_VDATE)
            .vint(DEFAULT_VINT)
            .vtext(DEFAULT_VTEXT)
            .vimg(DEFAULT_VIMG)
            .vimgContentType(DEFAULT_VIMG_CONTENT_TYPE);
        return dBConfig;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DBConfig createUpdatedEntity(EntityManager em) {
        DBConfig dBConfig = new DBConfig()
            .key(UPDATED_KEY)
            .habilitado(UPDATED_HABILITADO)
            .vstring(UPDATED_VSTRING)
            .vbol(UPDATED_VBOL)
            .vdate(UPDATED_VDATE)
            .vint(UPDATED_VINT)
            .vtext(UPDATED_VTEXT)
            .vimg(UPDATED_VIMG)
            .vimgContentType(UPDATED_VIMG_CONTENT_TYPE);
        return dBConfig;
    }

    @BeforeEach
    public void initTest() {
        dBConfig = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedDBConfig != null) {
            dBConfigRepository.delete(insertedDBConfig);
            insertedDBConfig = null;
        }
    }

    @Test
    @Transactional
    void createDBConfig() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DBConfig
        var returnedDBConfig = om.readValue(
            restDBConfigMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dBConfig)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DBConfig.class
        );

        // Validate the DBConfig in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDBConfigUpdatableFieldsEquals(returnedDBConfig, getPersistedDBConfig(returnedDBConfig));

        insertedDBConfig = returnedDBConfig;
    }

    @Test
    @Transactional
    void createDBConfigWithExistingId() throws Exception {
        // Create the DBConfig with an existing ID
        dBConfig.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDBConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dBConfig)))
            .andExpect(status().isBadRequest());

        // Validate the DBConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        dBConfig.setKey(null);

        // Create the DBConfig, which fails.

        restDBConfigMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dBConfig)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDBConfigs() throws Exception {
        // Initialize the database
        insertedDBConfig = dBConfigRepository.saveAndFlush(dBConfig);

        // Get all the dBConfigList
        restDBConfigMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dBConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].habilitado").value(hasItem(DEFAULT_HABILITADO.booleanValue())))
            .andExpect(jsonPath("$.[*].vstring").value(hasItem(DEFAULT_VSTRING)))
            .andExpect(jsonPath("$.[*].vbol").value(hasItem(DEFAULT_VBOL.booleanValue())))
            .andExpect(jsonPath("$.[*].vdate").value(hasItem(DEFAULT_VDATE.toString())))
            .andExpect(jsonPath("$.[*].vint").value(hasItem(DEFAULT_VINT)))
            .andExpect(jsonPath("$.[*].vtext").value(hasItem(DEFAULT_VTEXT.toString())))
            .andExpect(jsonPath("$.[*].vimgContentType").value(hasItem(DEFAULT_VIMG_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].vimg").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_VIMG))));
    }

    @Test
    @Transactional
    void getDBConfig() throws Exception {
        // Initialize the database
        insertedDBConfig = dBConfigRepository.saveAndFlush(dBConfig);

        // Get the dBConfig
        restDBConfigMockMvc
            .perform(get(ENTITY_API_URL_ID, dBConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dBConfig.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.habilitado").value(DEFAULT_HABILITADO.booleanValue()))
            .andExpect(jsonPath("$.vstring").value(DEFAULT_VSTRING))
            .andExpect(jsonPath("$.vbol").value(DEFAULT_VBOL.booleanValue()))
            .andExpect(jsonPath("$.vdate").value(DEFAULT_VDATE.toString()))
            .andExpect(jsonPath("$.vint").value(DEFAULT_VINT))
            .andExpect(jsonPath("$.vtext").value(DEFAULT_VTEXT.toString()))
            .andExpect(jsonPath("$.vimgContentType").value(DEFAULT_VIMG_CONTENT_TYPE))
            .andExpect(jsonPath("$.vimg").value(Base64.getEncoder().encodeToString(DEFAULT_VIMG)));
    }

    @Test
    @Transactional
    void getNonExistingDBConfig() throws Exception {
        // Get the dBConfig
        restDBConfigMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDBConfig() throws Exception {
        // Initialize the database
        insertedDBConfig = dBConfigRepository.saveAndFlush(dBConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dBConfig
        DBConfig updatedDBConfig = dBConfigRepository.findById(dBConfig.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDBConfig are not directly saved in db
        em.detach(updatedDBConfig);
        updatedDBConfig
            .key(UPDATED_KEY)
            .habilitado(UPDATED_HABILITADO)
            .vstring(UPDATED_VSTRING)
            .vbol(UPDATED_VBOL)
            .vdate(UPDATED_VDATE)
            .vint(UPDATED_VINT)
            .vtext(UPDATED_VTEXT)
            .vimg(UPDATED_VIMG)
            .vimgContentType(UPDATED_VIMG_CONTENT_TYPE);

        restDBConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDBConfig.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDBConfig))
            )
            .andExpect(status().isOk());

        // Validate the DBConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDBConfigToMatchAllProperties(updatedDBConfig);
    }

    @Test
    @Transactional
    void putNonExistingDBConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dBConfig.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDBConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dBConfig.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dBConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the DBConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDBConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dBConfig.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDBConfigMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dBConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the DBConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDBConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dBConfig.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDBConfigMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dBConfig)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DBConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDBConfigWithPatch() throws Exception {
        // Initialize the database
        insertedDBConfig = dBConfigRepository.saveAndFlush(dBConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dBConfig using partial update
        DBConfig partialUpdatedDBConfig = new DBConfig();
        partialUpdatedDBConfig.setId(dBConfig.getId());

        partialUpdatedDBConfig.key(UPDATED_KEY).habilitado(UPDATED_HABILITADO).vint(UPDATED_VINT);

        restDBConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDBConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDBConfig))
            )
            .andExpect(status().isOk());

        // Validate the DBConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDBConfigUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDBConfig, dBConfig), getPersistedDBConfig(dBConfig));
    }

    @Test
    @Transactional
    void fullUpdateDBConfigWithPatch() throws Exception {
        // Initialize the database
        insertedDBConfig = dBConfigRepository.saveAndFlush(dBConfig);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dBConfig using partial update
        DBConfig partialUpdatedDBConfig = new DBConfig();
        partialUpdatedDBConfig.setId(dBConfig.getId());

        partialUpdatedDBConfig
            .key(UPDATED_KEY)
            .habilitado(UPDATED_HABILITADO)
            .vstring(UPDATED_VSTRING)
            .vbol(UPDATED_VBOL)
            .vdate(UPDATED_VDATE)
            .vint(UPDATED_VINT)
            .vtext(UPDATED_VTEXT)
            .vimg(UPDATED_VIMG)
            .vimgContentType(UPDATED_VIMG_CONTENT_TYPE);

        restDBConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDBConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDBConfig))
            )
            .andExpect(status().isOk());

        // Validate the DBConfig in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDBConfigUpdatableFieldsEquals(partialUpdatedDBConfig, getPersistedDBConfig(partialUpdatedDBConfig));
    }

    @Test
    @Transactional
    void patchNonExistingDBConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dBConfig.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDBConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dBConfig.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dBConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the DBConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDBConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dBConfig.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDBConfigMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dBConfig))
            )
            .andExpect(status().isBadRequest());

        // Validate the DBConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDBConfig() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dBConfig.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDBConfigMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dBConfig)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DBConfig in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDBConfig() throws Exception {
        // Initialize the database
        insertedDBConfig = dBConfigRepository.saveAndFlush(dBConfig);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dBConfig
        restDBConfigMockMvc
            .perform(delete(ENTITY_API_URL_ID, dBConfig.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dBConfigRepository.count();
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

    protected DBConfig getPersistedDBConfig(DBConfig dBConfig) {
        return dBConfigRepository.findById(dBConfig.getId()).orElseThrow();
    }

    protected void assertPersistedDBConfigToMatchAllProperties(DBConfig expectedDBConfig) {
        assertDBConfigAllPropertiesEquals(expectedDBConfig, getPersistedDBConfig(expectedDBConfig));
    }

    protected void assertPersistedDBConfigToMatchUpdatableProperties(DBConfig expectedDBConfig) {
        assertDBConfigAllUpdatablePropertiesEquals(expectedDBConfig, getPersistedDBConfig(expectedDBConfig));
    }
}
