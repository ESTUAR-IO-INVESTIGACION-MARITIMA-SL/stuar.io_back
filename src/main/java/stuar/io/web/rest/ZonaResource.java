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
import stuar.io.repository.ZonaRepository;
import stuar.io.service.ZonaService;
import stuar.io.service.dto.ZonaDTO;
import stuar.io.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link stuar.io.domain.Zona}.
 */
@RestController
@RequestMapping("/api/zonas")
public class ZonaResource {

    private static final Logger LOG = LoggerFactory.getLogger(ZonaResource.class);

    private static final String ENTITY_NAME = "zona";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ZonaService zonaService;

    private final ZonaRepository zonaRepository;

    public ZonaResource(ZonaService zonaService, ZonaRepository zonaRepository) {
        this.zonaService = zonaService;
        this.zonaRepository = zonaRepository;
    }

    /**
     * {@code POST  /zonas} : Create a new zona.
     *
     * @param zonaDTO the zonaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new zonaDTO, or with status {@code 400 (Bad Request)} if the zona has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ZonaDTO> createZona(@Valid @RequestBody ZonaDTO zonaDTO) throws URISyntaxException {
        LOG.debug("REST request to save Zona : {}", zonaDTO);
        if (zonaDTO.getId() != null) {
            throw new BadRequestAlertException("A new zona cannot already have an ID", ENTITY_NAME, "idexists");
        }
        zonaDTO = zonaService.save(zonaDTO);
        return ResponseEntity.created(new URI("/api/zonas/" + zonaDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, zonaDTO.getId().toString()))
            .body(zonaDTO);
    }

    /**
     * {@code PUT  /zonas/:id} : Updates an existing zona.
     *
     * @param id the id of the zonaDTO to save.
     * @param zonaDTO the zonaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated zonaDTO,
     * or with status {@code 400 (Bad Request)} if the zonaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the zonaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ZonaDTO> updateZona(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ZonaDTO zonaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Zona : {}, {}", id, zonaDTO);
        if (zonaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, zonaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!zonaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        zonaDTO = zonaService.update(zonaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, zonaDTO.getId().toString()))
            .body(zonaDTO);
    }

    /**
     * {@code PATCH  /zonas/:id} : Partial updates given fields of an existing zona, field will ignore if it is null
     *
     * @param id the id of the zonaDTO to save.
     * @param zonaDTO the zonaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated zonaDTO,
     * or with status {@code 400 (Bad Request)} if the zonaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the zonaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the zonaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ZonaDTO> partialUpdateZona(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ZonaDTO zonaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Zona partially : {}, {}", id, zonaDTO);
        if (zonaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, zonaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!zonaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ZonaDTO> result = zonaService.partialUpdate(zonaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, zonaDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /zonas} : get all the zonas.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of zonas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ZonaDTO>> getAllZonas(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Zonas");
        Page<ZonaDTO> page = zonaService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /zonas/:id} : get the "id" zona.
     *
     * @param id the id of the zonaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the zonaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ZonaDTO> getZona(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Zona : {}", id);
        Optional<ZonaDTO> zonaDTO = zonaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(zonaDTO);
    }

    /**
     * {@code DELETE  /zonas/:id} : delete the "id" zona.
     *
     * @param id the id of the zonaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZona(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Zona : {}", id);
        zonaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
