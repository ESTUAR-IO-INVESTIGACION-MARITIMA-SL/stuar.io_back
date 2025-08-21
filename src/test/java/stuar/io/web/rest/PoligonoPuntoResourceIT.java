package stuar.io.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static stuar.io.domain.PoligonoPuntoAsserts.*;
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
import stuar.io.domain.PoligonoPunto;
import stuar.io.repository.PoligonoPuntoRepository;
import stuar.io.service.PoligonoPuntoService;
import stuar.io.service.dto.PoligonoPuntoDTO;
import stuar.io.service.mapper.PoligonoPuntoMapper;

/**
 * Integration tests for the {@link PoligonoPuntoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PoligonoPuntoResourceIT {

    private static final Double DEFAULT_X = 1D;
    private static final Double UPDATED_X = 2D;

    private static final Double DEFAULT_Y = 1D;
    private static final Double UPDATED_Y = 2D;

    private static final Integer DEFAULT_ORDER_INDEX = 0;
    private static final Integer UPDATED_ORDER_INDEX = 1;

    private static final String ENTITY_API_URL = "/api/poligono-puntos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PoligonoPuntoRepository poligonoPuntoRepository;

    @Mock
    private PoligonoPuntoRepository poligonoPuntoRepositoryMock;

    @Autowired
    private PoligonoPuntoMapper poligonoPuntoMapper;

    @Mock
    private PoligonoPuntoService poligonoPuntoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPoligonoPuntoMockMvc;

    private PoligonoPunto poligonoPunto;

    private PoligonoPunto insertedPoligonoPunto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PoligonoPunto createEntity(EntityManager em) {
        PoligonoPunto poligonoPunto = new PoligonoPunto().x(DEFAULT_X).y(DEFAULT_Y).orderIndex(DEFAULT_ORDER_INDEX);
        // Add required entity
        Poligono poligono;
        if (TestUtil.findAll(em, Poligono.class).isEmpty()) {
            poligono = PoligonoResourceIT.createEntity(em);
            em.persist(poligono);
            em.flush();
        } else {
            poligono = TestUtil.findAll(em, Poligono.class).get(0);
        }
        poligonoPunto.setPoligono(poligono);
        return poligonoPunto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PoligonoPunto createUpdatedEntity(EntityManager em) {
        PoligonoPunto updatedPoligonoPunto = new PoligonoPunto().x(UPDATED_X).y(UPDATED_Y).orderIndex(UPDATED_ORDER_INDEX);
        // Add required entity
        Poligono poligono;
        if (TestUtil.findAll(em, Poligono.class).isEmpty()) {
            poligono = PoligonoResourceIT.createUpdatedEntity(em);
            em.persist(poligono);
            em.flush();
        } else {
            poligono = TestUtil.findAll(em, Poligono.class).get(0);
        }
        updatedPoligonoPunto.setPoligono(poligono);
        return updatedPoligonoPunto;
    }

    @BeforeEach
    public void initTest() {
        poligonoPunto = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedPoligonoPunto != null) {
            poligonoPuntoRepository.delete(insertedPoligonoPunto);
            insertedPoligonoPunto = null;
        }
    }

    @Test
    @Transactional
    void createPoligonoPunto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PoligonoPunto
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);
        var returnedPoligonoPuntoDTO = om.readValue(
            restPoligonoPuntoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoPuntoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PoligonoPuntoDTO.class
        );

        // Validate the PoligonoPunto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPoligonoPunto = poligonoPuntoMapper.toEntity(returnedPoligonoPuntoDTO);
        assertPoligonoPuntoUpdatableFieldsEquals(returnedPoligonoPunto, getPersistedPoligonoPunto(returnedPoligonoPunto));

        insertedPoligonoPunto = returnedPoligonoPunto;
    }

    @Test
    @Transactional
    void createPoligonoPuntoWithExistingId() throws Exception {
        // Create the PoligonoPunto with an existing ID
        poligonoPunto.setId(1L);
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPoligonoPuntoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoPuntoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PoligonoPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkXIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        poligonoPunto.setX(null);

        // Create the PoligonoPunto, which fails.
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        restPoligonoPuntoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoPuntoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkYIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        poligonoPunto.setY(null);

        // Create the PoligonoPunto, which fails.
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        restPoligonoPuntoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoPuntoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        poligonoPunto.setOrderIndex(null);

        // Create the PoligonoPunto, which fails.
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        restPoligonoPuntoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoPuntoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPoligonoPuntos() throws Exception {
        // Initialize the database
        insertedPoligonoPunto = poligonoPuntoRepository.saveAndFlush(poligonoPunto);

        // Get all the poligonoPuntoList
        restPoligonoPuntoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(poligonoPunto.getId().intValue())))
            .andExpect(jsonPath("$.[*].x").value(hasItem(DEFAULT_X.doubleValue())))
            .andExpect(jsonPath("$.[*].y").value(hasItem(DEFAULT_Y.doubleValue())))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPoligonoPuntosWithEagerRelationshipsIsEnabled() throws Exception {
        when(poligonoPuntoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPoligonoPuntoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(poligonoPuntoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPoligonoPuntosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(poligonoPuntoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPoligonoPuntoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(poligonoPuntoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPoligonoPunto() throws Exception {
        // Initialize the database
        insertedPoligonoPunto = poligonoPuntoRepository.saveAndFlush(poligonoPunto);

        // Get the poligonoPunto
        restPoligonoPuntoMockMvc
            .perform(get(ENTITY_API_URL_ID, poligonoPunto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(poligonoPunto.getId().intValue()))
            .andExpect(jsonPath("$.x").value(DEFAULT_X.doubleValue()))
            .andExpect(jsonPath("$.y").value(DEFAULT_Y.doubleValue()))
            .andExpect(jsonPath("$.orderIndex").value(DEFAULT_ORDER_INDEX));
    }

    @Test
    @Transactional
    void getNonExistingPoligonoPunto() throws Exception {
        // Get the poligonoPunto
        restPoligonoPuntoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPoligonoPunto() throws Exception {
        // Initialize the database
        insertedPoligonoPunto = poligonoPuntoRepository.saveAndFlush(poligonoPunto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poligonoPunto
        PoligonoPunto updatedPoligonoPunto = poligonoPuntoRepository.findById(poligonoPunto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPoligonoPunto are not directly saved in db
        em.detach(updatedPoligonoPunto);
        updatedPoligonoPunto.x(UPDATED_X).y(UPDATED_Y).orderIndex(UPDATED_ORDER_INDEX);
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(updatedPoligonoPunto);

        restPoligonoPuntoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, poligonoPuntoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poligonoPuntoDTO))
            )
            .andExpect(status().isOk());

        // Validate the PoligonoPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPoligonoPuntoToMatchAllProperties(updatedPoligonoPunto);
    }

    @Test
    @Transactional
    void putNonExistingPoligonoPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligonoPunto.setId(longCount.incrementAndGet());

        // Create the PoligonoPunto
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoligonoPuntoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, poligonoPuntoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poligonoPuntoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoligonoPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPoligonoPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligonoPunto.setId(longCount.incrementAndGet());

        // Create the PoligonoPunto
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoligonoPuntoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(poligonoPuntoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoligonoPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPoligonoPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligonoPunto.setId(longCount.incrementAndGet());

        // Create the PoligonoPunto
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoligonoPuntoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(poligonoPuntoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PoligonoPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePoligonoPuntoWithPatch() throws Exception {
        // Initialize the database
        insertedPoligonoPunto = poligonoPuntoRepository.saveAndFlush(poligonoPunto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poligonoPunto using partial update
        PoligonoPunto partialUpdatedPoligonoPunto = new PoligonoPunto();
        partialUpdatedPoligonoPunto.setId(poligonoPunto.getId());

        restPoligonoPuntoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoligonoPunto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoligonoPunto))
            )
            .andExpect(status().isOk());

        // Validate the PoligonoPunto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoligonoPuntoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPoligonoPunto, poligonoPunto),
            getPersistedPoligonoPunto(poligonoPunto)
        );
    }

    @Test
    @Transactional
    void fullUpdatePoligonoPuntoWithPatch() throws Exception {
        // Initialize the database
        insertedPoligonoPunto = poligonoPuntoRepository.saveAndFlush(poligonoPunto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the poligonoPunto using partial update
        PoligonoPunto partialUpdatedPoligonoPunto = new PoligonoPunto();
        partialUpdatedPoligonoPunto.setId(poligonoPunto.getId());

        partialUpdatedPoligonoPunto.x(UPDATED_X).y(UPDATED_Y).orderIndex(UPDATED_ORDER_INDEX);

        restPoligonoPuntoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPoligonoPunto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPoligonoPunto))
            )
            .andExpect(status().isOk());

        // Validate the PoligonoPunto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPoligonoPuntoUpdatableFieldsEquals(partialUpdatedPoligonoPunto, getPersistedPoligonoPunto(partialUpdatedPoligonoPunto));
    }

    @Test
    @Transactional
    void patchNonExistingPoligonoPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligonoPunto.setId(longCount.incrementAndGet());

        // Create the PoligonoPunto
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPoligonoPuntoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, poligonoPuntoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poligonoPuntoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoligonoPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPoligonoPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligonoPunto.setId(longCount.incrementAndGet());

        // Create the PoligonoPunto
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoligonoPuntoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(poligonoPuntoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PoligonoPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPoligonoPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        poligonoPunto.setId(longCount.incrementAndGet());

        // Create the PoligonoPunto
        PoligonoPuntoDTO poligonoPuntoDTO = poligonoPuntoMapper.toDto(poligonoPunto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPoligonoPuntoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(poligonoPuntoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PoligonoPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePoligonoPunto() throws Exception {
        // Initialize the database
        insertedPoligonoPunto = poligonoPuntoRepository.saveAndFlush(poligonoPunto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the poligonoPunto
        restPoligonoPuntoMockMvc
            .perform(delete(ENTITY_API_URL_ID, poligonoPunto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return poligonoPuntoRepository.count();
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

    protected PoligonoPunto getPersistedPoligonoPunto(PoligonoPunto poligonoPunto) {
        return poligonoPuntoRepository.findById(poligonoPunto.getId()).orElseThrow();
    }

    protected void assertPersistedPoligonoPuntoToMatchAllProperties(PoligonoPunto expectedPoligonoPunto) {
        assertPoligonoPuntoAllPropertiesEquals(expectedPoligonoPunto, getPersistedPoligonoPunto(expectedPoligonoPunto));
    }

    protected void assertPersistedPoligonoPuntoToMatchUpdatableProperties(PoligonoPunto expectedPoligonoPunto) {
        assertPoligonoPuntoAllUpdatablePropertiesEquals(expectedPoligonoPunto, getPersistedPoligonoPunto(expectedPoligonoPunto));
    }
}
