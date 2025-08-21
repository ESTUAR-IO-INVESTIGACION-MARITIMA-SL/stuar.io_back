package stuar.io.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stuar.io.domain.ZonaPunto;
import stuar.io.repository.ZonaPuntoRepository;
import stuar.io.service.dto.ZonaPuntoDTO;
import stuar.io.service.mapper.ZonaPuntoMapper;

/**
 * Service Implementation for managing {@link stuar.io.domain.ZonaPunto}.
 */
@Service
@Transactional
public class ZonaPuntoService {

    private static final Logger LOG = LoggerFactory.getLogger(ZonaPuntoService.class);

    private final ZonaPuntoRepository zonaPuntoRepository;

    private final ZonaPuntoMapper zonaPuntoMapper;

    public ZonaPuntoService(ZonaPuntoRepository zonaPuntoRepository, ZonaPuntoMapper zonaPuntoMapper) {
        this.zonaPuntoRepository = zonaPuntoRepository;
        this.zonaPuntoMapper = zonaPuntoMapper;
    }

    /**
     * Save a zonaPunto.
     *
     * @param zonaPuntoDTO the entity to save.
     * @return the persisted entity.
     */
    public ZonaPuntoDTO save(ZonaPuntoDTO zonaPuntoDTO) {
        LOG.debug("Request to save ZonaPunto : {}", zonaPuntoDTO);
        ZonaPunto zonaPunto = zonaPuntoMapper.toEntity(zonaPuntoDTO);
        zonaPunto = zonaPuntoRepository.save(zonaPunto);
        return zonaPuntoMapper.toDto(zonaPunto);
    }

    /**
     * Update a zonaPunto.
     *
     * @param zonaPuntoDTO the entity to save.
     * @return the persisted entity.
     */
    public ZonaPuntoDTO update(ZonaPuntoDTO zonaPuntoDTO) {
        LOG.debug("Request to update ZonaPunto : {}", zonaPuntoDTO);
        ZonaPunto zonaPunto = zonaPuntoMapper.toEntity(zonaPuntoDTO);
        zonaPunto = zonaPuntoRepository.save(zonaPunto);
        return zonaPuntoMapper.toDto(zonaPunto);
    }

    /**
     * Partially update a zonaPunto.
     *
     * @param zonaPuntoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ZonaPuntoDTO> partialUpdate(ZonaPuntoDTO zonaPuntoDTO) {
        LOG.debug("Request to partially update ZonaPunto : {}", zonaPuntoDTO);

        return zonaPuntoRepository
            .findById(zonaPuntoDTO.getId())
            .map(existingZonaPunto -> {
                zonaPuntoMapper.partialUpdate(existingZonaPunto, zonaPuntoDTO);

                return existingZonaPunto;
            })
            .map(zonaPuntoRepository::save)
            .map(zonaPuntoMapper::toDto);
    }

    /**
     * Get all the zonaPuntos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ZonaPuntoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ZonaPuntos");
        return zonaPuntoRepository.findAll(pageable).map(zonaPuntoMapper::toDto);
    }

    /**
     * Get all the zonaPuntos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ZonaPuntoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return zonaPuntoRepository.findAllWithEagerRelationships(pageable).map(zonaPuntoMapper::toDto);
    }

    /**
     * Get one zonaPunto by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ZonaPuntoDTO> findOne(Long id) {
        LOG.debug("Request to get ZonaPunto : {}", id);
        return zonaPuntoRepository.findOneWithEagerRelationships(id).map(zonaPuntoMapper::toDto);
    }

    /**
     * Delete the zonaPunto by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ZonaPunto : {}", id);
        zonaPuntoRepository.deleteById(id);
    }
}
