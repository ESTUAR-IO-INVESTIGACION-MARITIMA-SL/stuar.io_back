package stuar.io.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stuar.io.domain.Batea;
import stuar.io.repository.BateaRepository;
import stuar.io.service.dto.BateaDTO;
import stuar.io.service.mapper.BateaMapper;

/**
 * Service Implementation for managing {@link stuar.io.domain.Batea}.
 */
@Service
@Transactional
public class BateaService {

    private static final Logger LOG = LoggerFactory.getLogger(BateaService.class);

    private final BateaRepository bateaRepository;

    private final BateaMapper bateaMapper;

    public BateaService(BateaRepository bateaRepository, BateaMapper bateaMapper) {
        this.bateaRepository = bateaRepository;
        this.bateaMapper = bateaMapper;
    }

    /**
     * Save a batea.
     *
     * @param bateaDTO the entity to save.
     * @return the persisted entity.
     */
    public BateaDTO save(BateaDTO bateaDTO) {
        LOG.debug("Request to save Batea : {}", bateaDTO);
        Batea batea = bateaMapper.toEntity(bateaDTO);
        batea = bateaRepository.save(batea);
        return bateaMapper.toDto(batea);
    }

    /**
     * Update a batea.
     *
     * @param bateaDTO the entity to save.
     * @return the persisted entity.
     */
    public BateaDTO update(BateaDTO bateaDTO) {
        LOG.debug("Request to update Batea : {}", bateaDTO);
        Batea batea = bateaMapper.toEntity(bateaDTO);
        batea = bateaRepository.save(batea);
        return bateaMapper.toDto(batea);
    }

    /**
     * Partially update a batea.
     *
     * @param bateaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BateaDTO> partialUpdate(BateaDTO bateaDTO) {
        LOG.debug("Request to partially update Batea : {}", bateaDTO);

        return bateaRepository
            .findById(bateaDTO.getId())
            .map(existingBatea -> {
                bateaMapper.partialUpdate(existingBatea, bateaDTO);

                return existingBatea;
            })
            .map(bateaRepository::save)
            .map(bateaMapper::toDto);
    }

    /**
     * Get all the bateas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BateaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Bateas");
        return bateaRepository.findAll(pageable).map(bateaMapper::toDto);
    }

    /**
     * Get all the bateas with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BateaDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bateaRepository.findAllWithEagerRelationships(pageable).map(bateaMapper::toDto);
    }

    /**
     * Get one batea by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BateaDTO> findOne(Long id) {
        LOG.debug("Request to get Batea : {}", id);
        return bateaRepository.findOneWithEagerRelationships(id).map(bateaMapper::toDto);
    }

    /**
     * Delete the batea by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Batea : {}", id);
        bateaRepository.deleteById(id);
    }
}
