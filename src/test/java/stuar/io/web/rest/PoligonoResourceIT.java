package stuar.io.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static stuar.io.domain.PoligonoAsserts.*;
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
import stuar.io.domain.Poligono;
import stuar.io.domain.Zona;
import stuar.io.repository.PoligonoRepository;
import stuar.io.service.PoligonoService;
import stuar.io.service.dto.PoligonoDTO;
import stuar.io.service.mapper.PoligonoMapper;

/**
 * Integration tests for the {@link PoligonoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PoligonoResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/poligonos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PoligonoRepository poligonoRepository;

    @Mock
    private PoligonoRepository poligonoRepositoryMock;

    @Autowired
    private PoligonoMapper poligonoMapper;

    @Mock
    private PoligonoService poligonoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPoligonoMockMvc;

    private Poligono poligono;

    private Poligono insertedPoligono;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Poligono createEntity(EntityManager em) {
        Poligono poligono = new Poligono().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        // Add required entity
        Zona zona;
        if (TestUtil.findAll(em, Zona.class).isEmpty()) {
            zona = ZonaResourceIT.createEntity();
            em.persist(zona);
            em.flush();
        } else {
            zona = TestUtil.findAll(em, Zona.class).get(0);
        }
        poligono.setZona(zona);
        return poligono;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Poligono createUpdatedEntity(EntityManager em) {
        Poligono updatedPoligono = new Poligono().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        // Add required entity
        Zona zona;
        if (TestUtil.findAll(em, Zona.class).isEmpty()) {
            zona = ZonaResourceIT.createUpdatedEntity();
            em.persist(zona);
            em.flush();
        } else {
            zona = TestUtil.findAll(em, Zona.class).get(0);
        }
        updatedPoligono.setZona(zona);
        return updatedPoligono;
    }

    @BeforeEach
    public void initTest() {
        poligono = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedPoligono != null) {
            poligonoRepository.delete(insertedPoligono);
            insertedPoligono = null;
        }
    }

    @Test
    @Transactional
    void createPoligono() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Poligono
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(poligono);
        var returnedPoligonoDTO = om.readValue(
            restPoligonoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PoligonoDTO.class
        );

        // Validate the Poligono in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPoligono = poligonoMapper.toEntity(returnedPoligonoDTO);
        assertPoligonoUpdatableFieldsEquals(returnedPoligono, getPersistedPoligono(returnedPoligono));

        insertedPoligono = returnedPoligono;
    }

    @Test
    @Transactional
    void createPoligonoWithExistingId() throws Exception {
        // Create the Poligono with an existing ID
        poligono.setId(1L);
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(poligono);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPoligonoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Poligono in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        poligono.setName(null);

        // Create the Poligono, which fails.
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(poligono);

        restPoligonoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPoligonos() throws Exception {
        // Initialize the database
        insertedPoligono = poligonoRepository.saveAndFlush(poligono);

        // Get all the poligonoList
        restPoligonoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(poligono.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPoligonosWithEagerRelationshipsIsEnabled() throws Exception {
        when(poligonoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPoligonoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(poligonoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPoligonosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(poligonoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPoligonoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(poligonoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPoligono() throws Exception {
        // Initialize the database
        insertedPoligono = poligonoRepository.saveAndFlush(poligono);

        // Get the poligono
        restPoligonoMockMvc
            .perform(get(ENTITY_API_URL_ID, poligono.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(poligono.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingPoligono() throws Exception {
        // Get the poligono
        restPoligonoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPoligono() throws Exception {
        // Initialize the database
        insertedPoligono = poligonoRepository.saveAndFlush(poligono);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poligono
        Poligono updatedPoligono = poligonoRepository.findById(poligono.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPoligono are not directly saved in db
        em.detach(updatedPoligono);
        updatedPoligono.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(updatedPoligono);

        restPoligonoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, poligonoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poligonoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Poligono in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPoligonoToMatchAllProperties(updatedPoligono);
    }

    @Test
    @Transactional
    void putNonExistingPoligono() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligono.setId(longCount.incrementAndGet());

        // Create the Poligono
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(poligono);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoligonoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, poligonoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poligonoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poligono in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPoligono() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligono.setId(longCount.incrementAndGet());

        // Create the Poligono
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(poligono);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoligonoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poligonoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poligono in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPoligono() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligono.setId(longCount.incrementAndGet());

        // Create the Poligono
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(poligono);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoligonoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Poligono in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePoligonoWithPatch() throws Exception {
        // Initialize the database
        insertedPoligono = poligonoRepository.saveAndFlush(poligono);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poligono using partial update
        Poligono partialUpdatedPoligono = new Poligono();
        partialUpdatedPoligono.setId(poligono.getId());

        partialUpdatedPoligono.description(UPDATED_DESCRIPTION);

        restPoligonoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoligono.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoligono))
            )
            .andExpect(status().isOk());

        // Validate the Poligono in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoligonoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPoligono, poligono), getPersistedPoligono(poligono));
    }

    @Test
    @Transactional
    void fullUpdatePoligonoWithPatch() throws Exception {
        // Initialize the database
        insertedPoligono = poligonoRepository.saveAndFlush(poligono);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poligono using partial update
        Poligono partialUpdatedPoligono = new Poligono();
        partialUpdatedPoligono.setId(poligono.getId());

        partialUpdatedPoligono.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restPoligonoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoligono.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoligono))
            )
            .andExpect(status().isOk());

        // Validate the Poligono in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoligonoUpdatableFieldsEquals(partialUpdatedPoligono, getPersistedPoligono(partialUpdatedPoligono));
    }

    @Test
    @Transactional
    void patchNonExistingPoligono() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligono.setId(longCount.incrementAndGet());

        // Create the Poligono
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(poligono);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoligonoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, poligonoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poligonoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poligono in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPoligono() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligono.setId(longCount.incrementAndGet());

        // Create the Poligono
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(poligono);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoligonoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poligonoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Poligono in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPoligono() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligono.setId(longCount.incrementAndGet());

        // Create the Poligono
        PoligonoDTO poligonoDTO = poligonoMapper.toDto(poligono);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoligonoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(poligonoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Poligono in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePoligono() throws Exception {
        // Initialize the database
        insertedPoligono = poligonoRepository.saveAndFlush(poligono);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the poligono
        restPoligonoMockMvc
            .perform(delete(ENTITY_API_URL_ID, poligono.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return poligonoRepository.count();
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

    protected Poligono getPersistedPoligono(Poligono poligono) {
        return poligonoRepository.findById(poligono.getId()).orElseThrow();
    }

    protected void assertPersistedPoligonoToMatchAllProperties(Poligono expectedPoligono) {
        assertPoligonoAllPropertiesEquals(expectedPoligono, getPersistedPoligono(expectedPoligono));
    }

    protected void assertPersistedPoligonoToMatchUpdatableProperties(Poligono expectedPoligono) {
        assertPoligonoAllUpdatablePropertiesEquals(expectedPoligono, getPersistedPoligono(expectedPoligono));
    }
}
