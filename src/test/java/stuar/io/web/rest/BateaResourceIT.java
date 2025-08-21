package stuar.io.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static stuar.io.domain.BateaAsserts.*;
import static stuar.io.web.rest.TestUtil.createUpdateProxyForBean;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import stuar.io.IntegrationTest;
import stuar.io.domain.Batea;
import stuar.io.domain.Poligono;
import stuar.io.repository.BateaRepository;
import stuar.io.service.BateaService;
import stuar.io.service.dto.BateaDTO;
import stuar.io.service.mapper.BateaMapper;

/**
 * Integration tests for the {@link BateaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BateaResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_X = 1D;
    private static final Double UPDATED_X = 2D;

    private static final Double DEFAULT_Y = 1D;
    private static final Double UPDATED_Y = 2D;

    private static final String ENTITY_API_URL = "/api/bateas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BateaRepository bateaRepository;

    @Mock
    private BateaRepository bateaRepositoryMock;

    @Autowired
    private BateaMapper bateaMapper;

    @Mock
    private BateaService bateaServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBateaMockMvc;

    private Batea batea;

    private Batea insertedBatea;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Batea createEntity(EntityManager em) {
        Batea batea = new Batea()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .ownerName(DEFAULT_OWNER_NAME)
            .x(DEFAULT_X)
            .y(DEFAULT_Y);
        // Add required entity
        Poligono poligono;
        if (TestUtil.findAll(em, Poligono.class).isEmpty()) {
            poligono = PoligonoResourceIT.createEntity(em);
            em.persist(poligono);
            em.flush();
        } else {
            poligono = TestUtil.findAll(em, Poligono.class).get(0);
        }
        batea.setPoligono(poligono);
        return batea;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Batea createUpdatedEntity(EntityManager em) {
        Batea updatedBatea = new Batea()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .ownerName(UPDATED_OWNER_NAME)
            .x(UPDATED_X)
            .y(UPDATED_Y);
        // Add required entity
        Poligono poligono;
        if (TestUtil.findAll(em, Poligono.class).isEmpty()) {
            poligono = PoligonoResourceIT.createUpdatedEntity(em);
            em.persist(poligono);
            em.flush();
        } else {
            poligono = TestUtil.findAll(em, Poligono.class).get(0);
        }
        updatedBatea.setPoligono(poligono);
        return updatedBatea;
    }

    @BeforeEach
    public void initTest() {
        batea = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedBatea != null) {
            bateaRepository.delete(insertedBatea);
            insertedBatea = null;
        }
    }

    @Test
    @Transactional
    void createBatea() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Batea
        BateaDTO bateaDTO = bateaMapper.toDto(batea);
        var returnedBateaDTO = om.readValue(
            restBateaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bateaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BateaDTO.class
        );

        // Validate the Batea in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBatea = bateaMapper.toEntity(returnedBateaDTO);
        assertBateaUpdatableFieldsEquals(returnedBatea, getPersistedBatea(returnedBatea));

        insertedBatea = returnedBatea;
    }

    @Test
    @Transactional
    void createBateaWithExistingId() throws Exception {
        // Create the Batea with an existing ID
        batea.setId(1L);
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBateaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bateaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Batea in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batea.setName(null);

        // Create the Batea, which fails.
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        restBateaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bateaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOwnerNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batea.setOwnerName(null);

        // Create the Batea, which fails.
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        restBateaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bateaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkXIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batea.setX(null);

        // Create the Batea, which fails.
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        restBateaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bateaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkYIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batea.setY(null);

        // Create the Batea, which fails.
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        restBateaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bateaDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBateas() throws Exception {
        // Initialize the database
        insertedBatea = bateaRepository.saveAndFlush(batea);

        // Get all the bateaList
        restBateaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(batea.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME)))
            .andExpect(jsonPath("$.[*].x").value(hasItem(DEFAULT_X.doubleValue())))
            .andExpect(jsonPath("$.[*].y").value(hasItem(DEFAULT_Y.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBateasWithEagerRelationshipsIsEnabled() throws Exception {
        when(bateaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBateaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bateaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBateasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bateaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBateaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bateaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBatea() throws Exception {
        // Initialize the database
        insertedBatea = bateaRepository.saveAndFlush(batea);

        // Get the batea
        restBateaMockMvc
            .perform(get(ENTITY_API_URL_ID, batea.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(batea.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.ownerName").value(DEFAULT_OWNER_NAME))
            .andExpect(jsonPath("$.x").value(DEFAULT_X.doubleValue()))
            .andExpect(jsonPath("$.y").value(DEFAULT_Y.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingBatea() throws Exception {
        // Get the batea
        restBateaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBatea() throws Exception {
        // Initialize the database
        insertedBatea = bateaRepository.saveAndFlush(batea);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the batea
        Batea updatedBatea = bateaRepository.findById(batea.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBatea are not directly saved in db
        em.detach(updatedBatea);
        updatedBatea.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).ownerName(UPDATED_OWNER_NAME).x(UPDATED_X).y(UPDATED_Y);
        BateaDTO bateaDTO = bateaMapper.toDto(updatedBatea);

        restBateaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bateaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bateaDTO))
            )
            .andExpect(status().isOk());

        // Validate the Batea in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBateaToMatchAllProperties(updatedBatea);
    }

    @Test
    @Transactional
    void putNonExistingBatea() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batea.setId(longCount.incrementAndGet());

        // Create the Batea
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBateaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bateaDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bateaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Batea in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBatea() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batea.setId(longCount.incrementAndGet());

        // Create the Batea
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBateaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bateaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Batea in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBatea() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batea.setId(longCount.incrementAndGet());

        // Create the Batea
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBateaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bateaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Batea in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBateaWithPatch() throws Exception {
        // Initialize the database
        insertedBatea = bateaRepository.saveAndFlush(batea);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the batea using partial update
        Batea partialUpdatedBatea = new Batea();
        partialUpdatedBatea.setId(batea.getId());

        partialUpdatedBatea.description(UPDATED_DESCRIPTION).ownerName(UPDATED_OWNER_NAME);

        restBateaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBatea.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBatea))
            )
            .andExpect(status().isOk());

        // Validate the Batea in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBateaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBatea, batea), getPersistedBatea(batea));
    }

    @Test
    @Transactional
    void fullUpdateBateaWithPatch() throws Exception {
        // Initialize the database
        insertedBatea = bateaRepository.saveAndFlush(batea);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the batea using partial update
        Batea partialUpdatedBatea = new Batea();
        partialUpdatedBatea.setId(batea.getId());

        partialUpdatedBatea.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).ownerName(UPDATED_OWNER_NAME).x(UPDATED_X).y(UPDATED_Y);

        restBateaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBatea.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBatea))
            )
            .andExpect(status().isOk());

        // Validate the Batea in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBateaUpdatableFieldsEquals(partialUpdatedBatea, getPersistedBatea(partialUpdatedBatea));
    }

    @Test
    @Transactional
    void patchNonExistingBatea() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batea.setId(longCount.incrementAndGet());

        // Create the Batea
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBateaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bateaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bateaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Batea in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBatea() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batea.setId(longCount.incrementAndGet());

        // Create the Batea
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBateaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bateaDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Batea in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBatea() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batea.setId(longCount.incrementAndGet());

        // Create the Batea
        BateaDTO bateaDTO = bateaMapper.toDto(batea);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBateaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bateaDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Batea in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBatea() throws Exception {
        // Initialize the database
        insertedBatea = bateaRepository.saveAndFlush(batea);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the batea
        restBateaMockMvc
            .perform(delete(ENTITY_API_URL_ID, batea.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bateaRepository.count();
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

    protected Batea getPersistedBatea(Batea batea) {
        return bateaRepository.findById(batea.getId()).orElseThrow();
    }

    protected void assertPersistedBateaToMatchAllProperties(Batea expectedBatea) {
        assertBateaAllPropertiesEquals(expectedBatea, getPersistedBatea(expectedBatea));
    }

    protected void assertPersistedBateaToMatchUpdatableProperties(Batea expectedBatea) {
        assertBateaAllUpdatablePropertiesEquals(expectedBatea, getPersistedBatea(expectedBatea));
    }
}
