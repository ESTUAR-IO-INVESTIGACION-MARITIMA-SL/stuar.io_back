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
import stuar.io.repository.ZonaPuntoRepository;
import stuar.io.service.ZonaPuntoService;
import stuar.io.service.dto.ZonaPuntoDTO;
import stuar.io.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link stuar.io.domain.ZonaPunto}.
 */
@RestController
@RequestMapping("/api/zona-puntos")
public class ZonaPuntoResource {

    private static final Logger LOG = LoggerFactory.getLogger(ZonaPuntoResource.class);

    private static final String ENTITY_NAME = "zonaPunto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ZonaPuntoService zonaPuntoService;

    private final ZonaPuntoRepository zonaPuntoRepository;

    public ZonaPuntoResource(ZonaPuntoService zonaPuntoService, ZonaPuntoRepository zonaPuntoRepository) {
        this.zonaPuntoService = zonaPuntoService;
        this.zonaPuntoRepository = zonaPuntoRepository;
    }

    /**
     * {@code POST  /zona-puntos} : Create a new zonaPunto.
     *
     * @param zonaPuntoDTO the zonaPuntoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new zonaPuntoDTO, or with status {@code 400 (Bad Request)} if the zonaPunto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ZonaPuntoDTO> createZonaPunto(@Valid @RequestBody ZonaPuntoDTO zonaPuntoDTO) throws URISyntaxException {
        LOG.debug("REST request to save ZonaPunto : {}", zonaPuntoDTO);
        if (zonaPuntoDTO.getId() != null) {
            throw new BadRequestAlertException("A new zonaPunto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        zonaPuntoDTO = zonaPuntoService.save(zonaPuntoDTO);
        return ResponseEntity.created(new URI("/api/zona-puntos/" + zonaPuntoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, zonaPuntoDTO.getId().toString()))
            .body(zonaPuntoDTO);
    }

    /**
     * {@code PUT  /zona-puntos/:id} : Updates an existing zonaPunto.
     *
     * @param id the id of the zonaPuntoDTO to save.
     * @param zonaPuntoDTO the zonaPuntoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated zonaPuntoDTO,
     * or with status {@code 400 (Bad Request)} if the zonaPuntoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the zonaPuntoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ZonaPuntoDTO> updateZonaPunto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ZonaPuntoDTO zonaPuntoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ZonaPunto : {}, {}", id, zonaPuntoDTO);
        if (zonaPuntoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, zonaPuntoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!zonaPuntoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        zonaPuntoDTO = zonaPuntoService.update(zonaPuntoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, zonaPuntoDTO.getId().toString()))
            .body(zonaPuntoDTO);
    }

    /**
     * {@code PATCH  /zona-puntos/:id} : Partial updates given fields of an existing zonaPunto, field will ignore if it is null
     *
     * @param id the id of the zonaPuntoDTO to save.
     * @param zonaPuntoDTO the zonaPuntoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated zonaPuntoDTO,
     * or with status {@code 400 (Bad Request)} if the zonaPuntoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the zonaPuntoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the zonaPuntoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ZonaPuntoDTO> partialUpdateZonaPunto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ZonaPuntoDTO zonaPuntoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ZonaPunto partially : {}, {}", id, zonaPuntoDTO);
        if (zonaPuntoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, zonaPuntoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!zonaPuntoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ZonaPuntoDTO> result = zonaPuntoService.partialUpdate(zonaPuntoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, zonaPuntoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /zona-puntos} : get all the zonaPuntos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of zonaPuntos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ZonaPuntoDTO>> getAllZonaPuntos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ZonaPuntos");
        Page<ZonaPuntoDTO> page;
        if (eagerload) {
            page = zonaPuntoService.findAllWithEagerRelationships(pageable);
        } else {
            page = zonaPuntoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /zona-puntos/:id} : get the "id" zonaPunto.
     *
     * @param id the id of the zonaPuntoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the zonaPuntoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ZonaPuntoDTO> getZonaPunto(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ZonaPunto : {}", id);
        Optional<ZonaPuntoDTO> zonaPuntoDTO = zonaPuntoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(zonaPuntoDTO);
    }

    /**
     * {@code DELETE  /zona-puntos/:id} : delete the "id" zonaPunto.
     *
     * @param id the id of the zonaPuntoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZonaPunto(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ZonaPunto : {}", id);
        zonaPuntoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
