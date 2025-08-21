package stuar.io.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stuar.io.domain.Zona;
import stuar.io.repository.ZonaRepository;
import stuar.io.service.dto.ZonaDTO;
import stuar.io.service.mapper.ZonaMapper;

/**
 * Service Implementation for managing {@link stuar.io.domain.Zona}.
 */
@Service
@Transactional
public class ZonaService {

    private static final Logger LOG = LoggerFactory.getLogger(ZonaService.class);

    private final ZonaRepository zonaRepository;

    private final ZonaMapper zonaMapper;

    public ZonaService(ZonaRepository zonaRepository, ZonaMapper zonaMapper) {
        this.zonaRepository = zonaRepository;
        this.zonaMapper = zonaMapper;
    }

    /**
     * Save a zona.
     *
     * @param zonaDTO the entity to save.
     * @return the persisted entity.
     */
    public ZonaDTO save(ZonaDTO zonaDTO) {
        LOG.debug("Request to save Zona : {}", zonaDTO);
        Zona zona = zonaMapper.toEntity(zonaDTO);
        zona = zonaRepository.save(zona);
        return zonaMapper.toDto(zona);
    }

    /**
     * Update a zona.
     *
     * @param zonaDTO the entity to save.
     * @return the persisted entity.
     */
    public ZonaDTO update(ZonaDTO zonaDTO) {
        LOG.debug("Request to update Zona : {}", zonaDTO);
        Zona zona = zonaMapper.toEntity(zonaDTO);
        zona = zonaRepository.save(zona);
        return zonaMapper.toDto(zona);
    }

    /**
     * Partially update a zona.
     *
     * @param zonaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ZonaDTO> partialUpdate(ZonaDTO zonaDTO) {
        LOG.debug("Request to partially update Zona : {}", zonaDTO);

        return zonaRepository
            .findById(zonaDTO.getId())
            .map(existingZona -> {
                zonaMapper.partialUpdate(existingZona, zonaDTO);

                return existingZona;
            })
            .map(zonaRepository::save)
            .map(zonaMapper::toDto);
    }

    /**
     * Get all the zonas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ZonaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Zonas");
        return zonaRepository.findAll(pageable).map(zonaMapper::toDto);
    }

    /**
     * Get one zona by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ZonaDTO> findOne(Long id) {
        LOG.debug("Request to get Zona : {}", id);
        return zonaRepository.findById(id).map(zonaMapper::toDto);
    }

    /**
     * Delete the zona by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Zona : {}", id);
        zonaRepository.deleteById(id);
    }
}
