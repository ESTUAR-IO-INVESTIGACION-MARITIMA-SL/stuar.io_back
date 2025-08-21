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
import stuar.io.repository.PoligonoRepository;
import stuar.io.service.PoligonoService;
import stuar.io.service.dto.PoligonoDTO;
import stuar.io.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link stuar.io.domain.Poligono}.
 */
@RestController
@RequestMapping("/api/poligonos")
public class PoligonoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PoligonoResource.class);

    private static final String ENTITY_NAME = "poligono";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PoligonoService poligonoService;

    private final PoligonoRepository poligonoRepository;

    public PoligonoResource(PoligonoService poligonoService, PoligonoRepository poligonoRepository) {
        this.poligonoService = poligonoService;
        this.poligonoRepository = poligonoRepository;
    }

    /**
     * {@code POST  /poligonos} : Create a new poligono.
     *
     * @param poligonoDTO the poligonoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new poligonoDTO, or with status {@code 400 (Bad Request)} if the poligono has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PoligonoDTO> createPoligono(@Valid @RequestBody PoligonoDTO poligonoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Poligono : {}", poligonoDTO);
        if (poligonoDTO.getId() != null) {
            throw new BadRequestAlertException("A new poligono cannot already have an ID", ENTITY_NAME, "idexists");
        }
        poligonoDTO = poligonoService.save(poligonoDTO);
        return ResponseEntity.created(new URI("/api/poligonos/" + poligonoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, poligonoDTO.getId().toString()))
            .body(poligonoDTO);
    }

    /**
     * {@code PUT  /poligonos/:id} : Updates an existing poligono.
     *
     * @param id the id of the poligonoDTO to save.
     * @param poligonoDTO the poligonoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated poligonoDTO,
     * or with status {@code 400 (Bad Request)} if the poligonoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the poligonoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PoligonoDTO> updatePoligono(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PoligonoDTO poligonoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Poligono : {}, {}", id, poligonoDTO);
        if (poligonoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, poligonoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!poligonoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        poligonoDTO = poligonoService.update(poligonoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, poligonoDTO.getId().toString()))
            .body(poligonoDTO);
    }

    /**
     * {@code PATCH  /poligonos/:id} : Partial updates given fields of an existing poligono, field will ignore if it is null
     *
     * @param id the id of the poligonoDTO to save.
     * @param poligonoDTO the poligonoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated poligonoDTO,
     * or with status {@code 400 (Bad Request)} if the poligonoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the poligonoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the poligonoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PoligonoDTO> partialUpdatePoligono(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PoligonoDTO poligonoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Poligono partially : {}, {}", id, poligonoDTO);
        if (poligonoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, poligonoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!poligonoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PoligonoDTO> result = poligonoService.partialUpdate(poligonoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, poligonoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /poligonos} : get all the poligonos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of poligonos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PoligonoDTO>> getAllPoligonos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Poligonos");
        Page<PoligonoDTO> page;
        if (eagerload) {
            page = poligonoService.findAllWithEagerRelationships(pageable);
        } else {
            page = poligonoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /poligonos/:id} : get the "id" poligono.
     *
     * @param id the id of the poligonoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the poligonoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PoligonoDTO> getPoligono(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Poligono : {}", id);
        Optional<PoligonoDTO> poligonoDTO = poligonoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(poligonoDTO);
    }

    /**
     * {@code DELETE  /poligonos/:id} : delete the "id" poligono.
     *
     * @param id the id of the poligonoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoligono(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Poligono : {}", id);
        poligonoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
