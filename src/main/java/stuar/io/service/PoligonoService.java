package stuar.io.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stuar.io.domain.Poligono;
import stuar.io.repository.PoligonoRepository;
import stuar.io.service.dto.PoligonoDTO;
import stuar.io.service.mapper.PoligonoMapper;

/**
 * Service Implementation for managing {@link stuar.io.domain.Poligono}.
 */
@Service
@Transactional
public class PoligonoService {

    private static final Logger LOG = LoggerFactory.getLogger(PoligonoService.class);

    private final PoligonoRepository poligonoRepository;

    private final PoligonoMapper poligonoMapper;

    public PoligonoService(PoligonoRepository poligonoRepository, PoligonoMapper poligonoMapper) {
        this.poligonoRepository = poligonoRepository;
        this.poligonoMapper = poligonoMapper;
    }

    /**
     * Save a poligono.
     *
     * @param poligonoDTO the entity to save.
     * @return the persisted entity.
     */
    public PoligonoDTO save(PoligonoDTO poligonoDTO) {
        LOG.debug("Request to save Poligono : {}", poligonoDTO);
        Poligono poligono = poligonoMapper.toEntity(poligonoDTO);
        poligono = poligonoRepository.save(poligono);
        return poligonoMapper.toDto(poligono);
    }

    /**
     * Update a poligono.
     *
     * @param poligonoDTO the entity to save.
     * @return the persisted entity.
     */
    public PoligonoDTO update(PoligonoDTO poligonoDTO) {
        LOG.debug("Request to update Poligono : {}", poligonoDTO);
        Poligono poligono = poligonoMapper.toEntity(poligonoDTO);
        poligono = poligonoRepository.save(poligono);
        return poligonoMapper.toDto(poligono);
    }

    /**
     * Partially update a poligono.
     *
     * @param poligonoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PoligonoDTO> partialUpdate(PoligonoDTO poligonoDTO) {
        LOG.debug("Request to partially update Poligono : {}", poligonoDTO);

        return poligonoRepository
            .findById(poligonoDTO.getId())
            .map(existingPoligono -> {
                poligonoMapper.partialUpdate(existingPoligono, poligonoDTO);

                return existingPoligono;
            })
            .map(poligonoRepository::save)
            .map(poligonoMapper::toDto);
    }

    /**
     * Get all the poligonos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PoligonoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Poligonos");
        return poligonoRepository.findAll(pageable).map(poligonoMapper::toDto);
    }

    /**
     * Get all the poligonos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PoligonoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return poligonoRepository.findAllWithEagerRelationships(pageable).map(poligonoMapper::toDto);
    }

    /**
     * Get one poligono by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PoligonoDTO> findOne(Long id) {
        LOG.debug("Request to get Poligono : {}", id);
        return poligonoRepository.findOneWithEagerRelationships(id).map(poligonoMapper::toDto);
    }

    /**
     * Delete the poligono by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Poligono : {}", id);
        poligonoRepository.deleteById(id);
    }
}
