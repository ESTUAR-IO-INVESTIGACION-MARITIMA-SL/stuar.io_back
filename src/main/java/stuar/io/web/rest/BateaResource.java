package stuar.io.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import stuar.io.repository.BateaRepository;
import stuar.io.service.BateaService;
import stuar.io.service.dto.BateaDTO;
import stuar.io.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link stuar.io.domain.Batea}.
 */
@RestController
@RequestMapping("/api/bateas")
public class BateaResource {

    private static final Logger LOG = LoggerFactory.getLogger(BateaResource.class);

    private static final String ENTITY_NAME = "batea";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BateaService bateaService;

    private final BateaRepository bateaRepository;

    public BateaResource(BateaService bateaService, BateaRepository bateaRepository) {
        this.bateaService = bateaService;
        this.bateaRepository = bateaRepository;
    }

    /**
     * {@code POST  /bateas} : Create a new batea.
     *
     * @param bateaDTO the bateaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bateaDTO, or with status {@code 400 (Bad Request)} if the batea has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BateaDTO> createBatea(@Valid @RequestBody BateaDTO bateaDTO) throws URISyntaxException {
        LOG.debug("REST request to save Batea : {}", bateaDTO);
        if (bateaDTO.getId() != null) {
            throw new BadRequestAlertException("A new batea cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bateaDTO = bateaService.save(bateaDTO);
        return ResponseEntity.created(new URI("/api/bateas/" + bateaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, bateaDTO.getId().toString()))
            .body(bateaDTO);
    }

    /**
     * {@code PUT  /bateas/:id} : Updates an existing batea.
     *
     * @param id the id of the bateaDTO to save.
     * @param bateaDTO the bateaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bateaDTO,
     * or with status {@code 400 (Bad Request)} if the bateaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bateaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BateaDTO> updateBatea(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BateaDTO bateaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Batea : {}, {}", id, bateaDTO);
        if (bateaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bateaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bateaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bateaDTO = bateaService.update(bateaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bateaDTO.getId().toString()))
            .body(bateaDTO);
    }

    /**
     * {@code PATCH  /bateas/:id} : Partial updates given fields of an existing batea, field will ignore if it is null
     *
     * @param id the id of the bateaDTO to save.
     * @param bateaDTO the bateaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bateaDTO,
     * or with status {@code 400 (Bad Request)} if the bateaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bateaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bateaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BateaDTO> partialUpdateBatea(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BateaDTO bateaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Batea partially : {}, {}", id, bateaDTO);
        if (bateaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bateaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bateaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BateaDTO> result = bateaService.partialUpdate(bateaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bateaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /bateas} : get all the bateas.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bateas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BateaDTO>> getAllBateas(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Bateas");
        Page<BateaDTO> page;
        if (eagerload) {
            page = bateaService.findAllWithEagerRelationships(pageable);
        } else {
            page = bateaService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bateas/:id} : get the "id" batea.
     *
     * @param id the id of the bateaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bateaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BateaDTO> getBatea(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Batea : {}", id);
        Optional<BateaDTO> bateaDTO = bateaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bateaDTO);
    }

    /**
     * {@code DELETE  /bateas/:id} : delete the "id" batea.
     *
     * @param id the id of the bateaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatea(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Batea : {}", id);
        bateaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
