package stuar.io.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static stuar.io.domain.ZonaAsserts.*;
import static stuar.io.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import stuar.io.IntegrationTest;
import stuar.io.domain.Zona;
import stuar.io.repository.ZonaRepository;
import stuar.io.service.dto.ZonaDTO;
import stuar.io.service.mapper.ZonaMapper;

/**
 * Integration tests for the {@link ZonaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ZonaResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/zonas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private ZonaMapper zonaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restZonaMockMvc;

    private Zona zona;

    private Zona insertedZona;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Zona createEntity() {
        return new Zona().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Zona createUpdatedEntity() {
        return new Zona().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        zona = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedZona != null) {
            zonaRepository.delete(insertedZona);
            insertedZona = null;
        }
    }

    @Test
    @Transactional
    void createZona() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Zona
        ZonaDTO zonaDTO = zonaMapper.toDto(zona);
        var returnedZonaDTO = om.readValue(
            restZonaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ZonaDTO.class
        );

        // Validate the Zona in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedZona = zonaMapper.toEntity(returnedZonaDTO);
        assertZonaUpdatableFieldsEquals(returnedZona, getPersistedZona(returnedZona));

        insertedZona = returnedZona;
    }

    @Test
    @Transactional
    void createZonaWithExistingId() throws Exception {
        // Create the Zona with an existing ID
        zona.setId(1L);
        ZonaDTO zonaDTO = zonaMapper.toDto(zona);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restZonaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Zona in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        zona.setName(null);

        // Create the Zona, which fails.
        ZonaDTO zonaDTO = zonaMapper.toDto(zona);

        restZonaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllZonas() throws Exception {
        // Initialize the database
        insertedZona = zonaRepository.saveAndFlush(zona);

        // Get all the zonaList
        restZonaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(zona.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getZona() throws Exception {
        // Initialize the database
        insertedZona = zonaRepository.saveAndFlush(zona);

        // Get the zona
        restZonaMockMvc
            .perform(get(ENTITY_API_URL_ID, zona.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(zona.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingZona() throws Exception {
        // Get the zona
        restZonaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingZona() throws Exception {
        // Initialize the database
        insertedZona = zonaRepository.saveAndFlush(zona);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the zona
        Zona updatedZona = zonaRepository.findById(zona.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedZona are not directly saved in db
        em.detach(updatedZona);
        updatedZona.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        ZonaDTO zonaDTO = zonaMapper.toDto(updatedZona);

        restZonaMockMvc
            .perform(put(ENTITY_API_URL_ID, zonaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaDTO)))
            .andExpect(status().isOk());

        // Validate the Zona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedZonaToMatchAllProperties(updatedZona);
    }

    @Test
    @Transactional
    void putNonExistingZona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zona.setId(longCount.incrementAndGet());

        // Create the Zona
        ZonaDTO zonaDTO = zonaMapper.toDto(zona);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restZonaMockMvc
            .perform(put(ENTITY_API_URL_ID, zonaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Zona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchZona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zona.setId(longCount.incrementAndGet());

        // Create the Zona
        ZonaDTO zonaDTO = zonaMapper.toDto(zona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZonaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(zonaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Zona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamZona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zona.setId(longCount.incrementAndGet());

        // Create the Zona
        ZonaDTO zonaDTO = zonaMapper.toDto(zona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZonaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Zona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateZonaWithPatch() throws Exception {
        // Initialize the database
        insertedZona = zonaRepository.saveAndFlush(zona);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the zona using partial update
        Zona partialUpdatedZona = new Zona();
        partialUpdatedZona.setId(zona.getId());

        partialUpdatedZona.description(UPDATED_DESCRIPTION);

        restZonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedZona.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedZona))
            )
            .andExpect(status().isOk());

        // Validate the Zona in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertZonaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedZona, zona), getPersistedZona(zona));
    }

    @Test
    @Transactional
    void fullUpdateZonaWithPatch() throws Exception {
        // Initialize the database
        insertedZona = zonaRepository.saveAndFlush(zona);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the zona using partial update
        Zona partialUpdatedZona = new Zona();
        partialUpdatedZona.setId(zona.getId());

        partialUpdatedZona.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restZonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedZona.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedZona))
            )
            .andExpect(status().isOk());

        // Validate the Zona in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertZonaUpdatableFieldsEquals(partialUpdatedZona, getPersistedZona(partialUpdatedZona));
    }

    @Test
    @Transactional
    void patchNonExistingZona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zona.setId(longCount.incrementAndGet());

        // Create the Zona
        ZonaDTO zonaDTO = zonaMapper.toDto(zona);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restZonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, zonaDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(zonaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Zona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchZona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zona.setId(longCount.incrementAndGet());

        // Create the Zona
        ZonaDTO zonaDTO = zonaMapper.toDto(zona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZonaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(zonaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Zona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamZona() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zona.setId(longCount.incrementAndGet());

        // Create the Zona
        ZonaDTO zonaDTO = zonaMapper.toDto(zona);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZonaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(zonaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Zona in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteZona() throws Exception {
        // Initialize the database
        insertedZona = zonaRepository.saveAndFlush(zona);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the zona
        restZonaMockMvc
            .perform(delete(ENTITY_API_URL_ID, zona.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return zonaRepository.count();
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

    protected Zona getPersistedZona(Zona zona) {
        return zonaRepository.findById(zona.getId()).orElseThrow();
    }

    protected void assertPersistedZonaToMatchAllProperties(Zona expectedZona) {
        assertZonaAllPropertiesEquals(expectedZona, getPersistedZona(expectedZona));
    }

    protected void assertPersistedZonaToMatchUpdatableProperties(Zona expectedZona) {
        assertZonaAllUpdatablePropertiesEquals(expectedZona, getPersistedZona(expectedZona));
    }
}
