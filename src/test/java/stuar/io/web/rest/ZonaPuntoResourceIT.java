package stuar.io.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static stuar.io.domain.ZonaPuntoAsserts.*;
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
import stuar.io.domain.Zona;
import stuar.io.domain.ZonaPunto;
import stuar.io.repository.ZonaPuntoRepository;
import stuar.io.service.ZonaPuntoService;
import stuar.io.service.dto.ZonaPuntoDTO;
import stuar.io.service.mapper.ZonaPuntoMapper;

/**
 * Integration tests for the {@link ZonaPuntoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ZonaPuntoResourceIT {

    private static final Double DEFAULT_X = 1D;
    private static final Double UPDATED_X = 2D;

    private static final Double DEFAULT_Y = 1D;
    private static final Double UPDATED_Y = 2D;

    private static final Integer DEFAULT_ORDER_INDEX = 0;
    private static final Integer UPDATED_ORDER_INDEX = 1;

    private static final String ENTITY_API_URL = "/api/zona-puntos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ZonaPuntoRepository zonaPuntoRepository;

    @Mock
    private ZonaPuntoRepository zonaPuntoRepositoryMock;

    @Autowired
    private ZonaPuntoMapper zonaPuntoMapper;

    @Mock
    private ZonaPuntoService zonaPuntoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restZonaPuntoMockMvc;

    private ZonaPunto zonaPunto;

    private ZonaPunto insertedZonaPunto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ZonaPunto createEntity(EntityManager em) {
        ZonaPunto zonaPunto = new ZonaPunto().x(DEFAULT_X).y(DEFAULT_Y).orderIndex(DEFAULT_ORDER_INDEX);
        // Add required entity
        Zona zona;
        if (TestUtil.findAll(em, Zona.class).isEmpty()) {
            zona = ZonaResourceIT.createEntity();
            em.persist(zona);
            em.flush();
        } else {
            zona = TestUtil.findAll(em, Zona.class).get(0);
        }
        zonaPunto.setZona(zona);
        return zonaPunto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ZonaPunto createUpdatedEntity(EntityManager em) {
        ZonaPunto updatedZonaPunto = new ZonaPunto().x(UPDATED_X).y(UPDATED_Y).orderIndex(UPDATED_ORDER_INDEX);
        // Add required entity
        Zona zona;
        if (TestUtil.findAll(em, Zona.class).isEmpty()) {
            zona = ZonaResourceIT.createUpdatedEntity();
            em.persist(zona);
            em.flush();
        } else {
            zona = TestUtil.findAll(em, Zona.class).get(0);
        }
        updatedZonaPunto.setZona(zona);
        return updatedZonaPunto;
    }

    @BeforeEach
    public void initTest() {
        zonaPunto = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedZonaPunto != null) {
            zonaPuntoRepository.delete(insertedZonaPunto);
            insertedZonaPunto = null;
        }
    }

    @Test
    @Transactional
    void createZonaPunto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ZonaPunto
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);
        var returnedZonaPuntoDTO = om.readValue(
            restZonaPuntoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaPuntoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ZonaPuntoDTO.class
        );

        // Validate the ZonaPunto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedZonaPunto = zonaPuntoMapper.toEntity(returnedZonaPuntoDTO);
        assertZonaPuntoUpdatableFieldsEquals(returnedZonaPunto, getPersistedZonaPunto(returnedZonaPunto));

        insertedZonaPunto = returnedZonaPunto;
    }

    @Test
    @Transactional
    void createZonaPuntoWithExistingId() throws Exception {
        // Create the ZonaPunto with an existing ID
        zonaPunto.setId(1L);
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restZonaPuntoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaPuntoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ZonaPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkXIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        zonaPunto.setX(null);

        // Create the ZonaPunto, which fails.
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        restZonaPuntoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaPuntoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkYIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        zonaPunto.setY(null);

        // Create the ZonaPunto, which fails.
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        restZonaPuntoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaPuntoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderIndexIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        zonaPunto.setOrderIndex(null);

        // Create the ZonaPunto, which fails.
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        restZonaPuntoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaPuntoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllZonaPuntos() throws Exception {
        // Initialize the database
        insertedZonaPunto = zonaPuntoRepository.saveAndFlush(zonaPunto);

        // Get all the zonaPuntoList
        restZonaPuntoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(zonaPunto.getId().intValue())))
            .andExpect(jsonPath("$.[*].x").value(hasItem(DEFAULT_X.doubleValue())))
            .andExpect(jsonPath("$.[*].y").value(hasItem(DEFAULT_Y.doubleValue())))
            .andExpect(jsonPath("$.[*].orderIndex").value(hasItem(DEFAULT_ORDER_INDEX)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllZonaPuntosWithEagerRelationshipsIsEnabled() throws Exception {
        when(zonaPuntoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restZonaPuntoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(zonaPuntoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllZonaPuntosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(zonaPuntoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restZonaPuntoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(zonaPuntoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getZonaPunto() throws Exception {
        // Initialize the database
        insertedZonaPunto = zonaPuntoRepository.saveAndFlush(zonaPunto);

        // Get the zonaPunto
        restZonaPuntoMockMvc
            .perform(get(ENTITY_API_URL_ID, zonaPunto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(zonaPunto.getId().intValue()))
            .andExpect(jsonPath("$.x").value(DEFAULT_X.doubleValue()))
            .andExpect(jsonPath("$.y").value(DEFAULT_Y.doubleValue()))
            .andExpect(jsonPath("$.orderIndex").value(DEFAULT_ORDER_INDEX));
    }

    @Test
    @Transactional
    void getNonExistingZonaPunto() throws Exception {
        // Get the zonaPunto
        restZonaPuntoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingZonaPunto() throws Exception {
        // Initialize the database
        insertedZonaPunto = zonaPuntoRepository.saveAndFlush(zonaPunto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the zonaPunto
        ZonaPunto updatedZonaPunto = zonaPuntoRepository.findById(zonaPunto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedZonaPunto are not directly saved in db
        em.detach(updatedZonaPunto);
        updatedZonaPunto.x(UPDATED_X).y(UPDATED_Y).orderIndex(UPDATED_ORDER_INDEX);
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(updatedZonaPunto);

        restZonaPuntoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, zonaPuntoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(zonaPuntoDTO))
            )
            .andExpect(status().isOk());

        // Validate the ZonaPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedZonaPuntoToMatchAllProperties(updatedZonaPunto);
    }

    @Test
    @Transactional
    void putNonExistingZonaPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zonaPunto.setId(longCount.incrementAndGet());

        // Create the ZonaPunto
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restZonaPuntoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, zonaPuntoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(zonaPuntoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ZonaPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchZonaPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zonaPunto.setId(longCount.incrementAndGet());

        // Create the ZonaPunto
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZonaPuntoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(zonaPuntoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ZonaPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamZonaPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zonaPunto.setId(longCount.incrementAndGet());

        // Create the ZonaPunto
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZonaPuntoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(zonaPuntoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ZonaPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateZonaPuntoWithPatch() throws Exception {
        // Initialize the database
        insertedZonaPunto = zonaPuntoRepository.saveAndFlush(zonaPunto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the zonaPunto using partial update
        ZonaPunto partialUpdatedZonaPunto = new ZonaPunto();
        partialUpdatedZonaPunto.setId(zonaPunto.getId());

        partialUpdatedZonaPunto.x(UPDATED_X).y(UPDATED_Y);

        restZonaPuntoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedZonaPunto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedZonaPunto))
            )
            .andExpect(status().isOk());

        // Validate the ZonaPunto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertZonaPuntoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedZonaPunto, zonaPunto),
            getPersistedZonaPunto(zonaPunto)
        );
    }

    @Test
    @Transactional
    void fullUpdateZonaPuntoWithPatch() throws Exception {
        // Initialize the database
        insertedZonaPunto = zonaPuntoRepository.saveAndFlush(zonaPunto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the zonaPunto using partial update
        ZonaPunto partialUpdatedZonaPunto = new ZonaPunto();
        partialUpdatedZonaPunto.setId(zonaPunto.getId());

        partialUpdatedZonaPunto.x(UPDATED_X).y(UPDATED_Y).orderIndex(UPDATED_ORDER_INDEX);

        restZonaPuntoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedZonaPunto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedZonaPunto))
            )
            .andExpect(status().isOk());

        // Validate the ZonaPunto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertZonaPuntoUpdatableFieldsEquals(partialUpdatedZonaPunto, getPersistedZonaPunto(partialUpdatedZonaPunto));
    }

    @Test
    @Transactional
    void patchNonExistingZonaPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zonaPunto.setId(longCount.incrementAndGet());

        // Create the ZonaPunto
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restZonaPuntoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, zonaPuntoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(zonaPuntoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ZonaPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchZonaPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zonaPunto.setId(longCount.incrementAndGet());

        // Create the ZonaPunto
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZonaPuntoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(zonaPuntoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ZonaPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamZonaPunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        zonaPunto.setId(longCount.incrementAndGet());

        // Create the ZonaPunto
        ZonaPuntoDTO zonaPuntoDTO = zonaPuntoMapper.toDto(zonaPunto);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restZonaPuntoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(zonaPuntoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ZonaPunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteZonaPunto() throws Exception {
        // Initialize the database
        insertedZonaPunto = zonaPuntoRepository.saveAndFlush(zonaPunto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the zonaPunto
        restZonaPuntoMockMvc
            .perform(delete(ENTITY_API_URL_ID, zonaPunto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return zonaPuntoRepository.count();
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

    protected ZonaPunto getPersistedZonaPunto(ZonaPunto zonaPunto) {
        return zonaPuntoRepository.findById(zonaPunto.getId()).orElseThrow();
    }

    protected void assertPersistedZonaPuntoToMatchAllProperties(ZonaPunto expectedZonaPunto) {
        assertZonaPuntoAllPropertiesEquals(expectedZonaPunto, getPersistedZonaPunto(expectedZonaPunto));
    }

    protected void assertPersistedZonaPuntoToMatchUpdatableProperties(ZonaPunto expectedZonaPunto) {
        assertZonaPuntoAllUpdatablePropertiesEquals(expectedZonaPunto, getPersistedZonaPunto(expectedZonaPunto));
    }
}
