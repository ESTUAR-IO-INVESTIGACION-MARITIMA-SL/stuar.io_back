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
import stuar.io.repository.PoligonoPuntoRepository;
import stuar.io.service.PoligonoPuntoService;
import stuar.io.service.dto.PoligonoPuntoDTO;
import stuar.io.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link stuar.io.domain.PoligonoPunto}.
 */
@RestController
@RequestMapping("/api/poligono-puntos")
public class PoligonoPuntoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PoligonoPuntoResource.class);

    private static final String ENTITY_NAME = "poligonoPunto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PoligonoPuntoService poligonoPuntoService;

    private final PoligonoPuntoRepository poligonoPuntoRepository;

    public PoligonoPuntoResource(PoligonoPuntoService poligonoPuntoService, PoligonoPuntoRepository poligonoPuntoRepository) {
        this.poligonoPuntoService = poligonoPuntoService;
        this.poligonoPuntoRepository = poligonoPuntoRepository;
    }

    /**
     * {@code POST  /poligono-puntos} : Create a new poligonoPunto.
     *
     * @param poligonoPuntoDTO the poligonoPuntoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new poligonoPuntoDTO, or with status {@code 400 (Bad Request)} if the poligonoPunto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PoligonoPuntoDTO> createPoligonoPunto(@Valid @RequestBody PoligonoPuntoDTO poligonoPuntoDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PoligonoPunto : {}", poligonoPuntoDTO);
        if (poligonoPuntoDTO.getId() != null) {
            throw new BadRequestAlertException("A new poligonoPunto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        poligonoPuntoDTO = poligonoPuntoService.save(poligonoPuntoDTO);
        return ResponseEntity.created(new URI("/api/poligono-puntos/" + poligonoPuntoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, poligonoPuntoDTO.getId().toString()))
            .body(poligonoPuntoDTO);
    }

    /**
     * {@code PUT  /poligono-puntos/:id} : Updates an existing poligonoPunto.
     *
     * @param id the id of the poligonoPuntoDTO to save.
     * @param poligonoPuntoDTO the poligonoPuntoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated poligonoPuntoDTO,
     * or with status {@code 400 (Bad Request)} if the poligonoPuntoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the poligonoPuntoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PoligonoPuntoDTO> updatePoligonoPunto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PoligonoPuntoDTO poligonoPuntoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PoligonoPunto : {}, {}", id, poligonoPuntoDTO);
        if (poligonoPuntoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, poligonoPuntoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!poligonoPuntoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        poligonoPuntoDTO = poligonoPuntoService.update(poligonoPuntoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, poligonoPuntoDTO.getId().toString()))
            .body(poligonoPuntoDTO);
    }

    /**
     * {@code PATCH  /poligono-puntos/:id} : Partial updates given fields of an existing poligonoPunto, field will ignore if it is null
     *
     * @param id the id of the poligonoPuntoDTO to save.
     * @param poligonoPuntoDTO the poligonoPuntoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated poligonoPuntoDTO,
     * or with status {@code 400 (Bad Request)} if the poligonoPuntoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the poligonoPuntoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the poligonoPuntoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PoligonoPuntoDTO> partialUpdatePoligonoPunto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PoligonoPuntoDTO poligonoPuntoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PoligonoPunto partially : {}, {}", id, poligonoPuntoDTO);
        if (poligonoPuntoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, poligonoPuntoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!poligonoPuntoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PoligonoPuntoDTO> result = poligonoPuntoService.partialUpdate(poligonoPuntoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, poligonoPuntoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /poligono-puntos} : get all the poligonoPuntos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of poligonoPuntos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PoligonoPuntoDTO>> getAllPoligonoPuntos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of PoligonoPuntos");
        Page<PoligonoPuntoDTO> page;
        if (eagerload) {
            page = poligonoPuntoService.findAllWithEagerRelationships(pageable);
        } else {
            page = poligonoPuntoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /poligono-puntos/:id} : get the "id" poligonoPunto.
     *
     * @param id the id of the poligonoPuntoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the poligonoPuntoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PoligonoPuntoDTO> getPoligonoPunto(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PoligonoPunto : {}", id);
        Optional<PoligonoPuntoDTO> poligonoPuntoDTO = poligonoPuntoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(poligonoPuntoDTO);
    }

    /**
     * {@code DELETE  /poligono-puntos/:id} : delete the "id" poligonoPunto.
     *
     * @param id the id of the poligonoPuntoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoligonoPunto(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PoligonoPunto : {}", id);
        poligonoPuntoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
