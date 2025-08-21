package stuar.io.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stuar.io.domain.PoligonoPunto;
import stuar.io.repository.PoligonoPuntoRepository;
import stuar.io.service.dto.PoligonoPuntoDTO;
import stuar.io.service.mapper.PoligonoPuntoMapper;

/**
 * Service Implementation for managing {@link stuar.io.domain.PoligonoPunto}.
 */
@Service
@Transactional
public class PoligonoPuntoService {

    private static final Logger LOG = LoggerFactory.getLogger(PoligonoPuntoService.class);

    private final PoligonoPuntoRepository poligonoPuntoRepository;

    private final PoligonoPuntoMapper poligonoPuntoMapper;

    public PoligonoPuntoService(PoligonoPuntoRepository poligonoPuntoRepository, PoligonoPuntoMapper poligonoPuntoMapper) {
        this.poligonoPuntoRepository = poligonoPuntoRepository;
        this.poligonoPuntoMapper = poligonoPuntoMapper;
    }

    /**
     * Save a poligonoPunto.
     *
     * @param poligonoPuntoDTO the entity to save.
     * @return the persisted entity.
     */
    public PoligonoPuntoDTO save(PoligonoPuntoDTO poligonoPuntoDTO) {
        LOG.debug("Request to save PoligonoPunto : {}", poligonoPuntoDTO);
        PoligonoPunto poligonoPunto = poligonoPuntoMapper.toEntity(poligonoPuntoDTO);
        poligonoPunto = poligonoPuntoRepository.save(poligonoPunto);
        return poligonoPuntoMapper.toDto(poligonoPunto);
    }

    /**
     * Update a poligonoPunto.
     *
     * @param poligonoPuntoDTO the entity to save.
     * @return the persisted entity.
     */
    public PoligonoPuntoDTO update(PoligonoPuntoDTO poligonoPuntoDTO) {
        LOG.debug("Request to update PoligonoPunto : {}", poligonoPuntoDTO);
        PoligonoPunto poligonoPunto = poligonoPuntoMapper.toEntity(poligonoPuntoDTO);
        poligonoPunto = poligonoPuntoRepository.save(poligonoPunto);
        return poligonoPuntoMapper.toDto(poligonoPunto);
    }

    /**
     * Partially update a poligonoPunto.
     *
     * @param poligonoPuntoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PoligonoPuntoDTO> partialUpdate(PoligonoPuntoDTO poligonoPuntoDTO) {
        LOG.debug("Request to partially update PoligonoPunto : {}", poligonoPuntoDTO);

        return poligonoPuntoRepository
            .findById(poligonoPuntoDTO.getId())
            .map(existingPoligonoPunto -> {
                poligonoPuntoMapper.partialUpdate(existingPoligonoPunto, poligonoPuntoDTO);

                return existingPoligonoPunto;
            })
            .map(poligonoPuntoRepository::save)
            .map(poligonoPuntoMapper::toDto);
    }

    /**
     * Get all the poligonoPuntos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PoligonoPuntoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PoligonoPuntos");
        return poligonoPuntoRepository.findAll(pageable).map(poligonoPuntoMapper::toDto);
    }

    /**
     * Get all the poligonoPuntos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PoligonoPuntoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return poligonoPuntoRepository.findAllWithEagerRelationships(pageable).map(poligonoPuntoMapper::toDto);
    }

    /**
     * Get one poligonoPunto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PoligonoPuntoDTO> findOne(Long id) {
        LOG.debug("Request to get PoligonoPunto : {}", id);
        return poligonoPuntoRepository.findOneWithEagerRelationships(id).map(poligonoPuntoMapper::toDto);
    }

    /**
     * Delete the poligonoPunto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PoligonoPunto : {}", id);
        poligonoPuntoRepository.deleteById(id);
    }
}
