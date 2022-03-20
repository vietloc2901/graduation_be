package locnv.haui.web.rest;

import locnv.haui.domain.User;
import locnv.haui.repository.CatalogsRepository;
import locnv.haui.service.CatalogsService;
import locnv.haui.service.UserService;
import locnv.haui.service.dto.CatalogsDTO;
import locnv.haui.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link locnv.haui.domain.Catalogs}.
 */
@RestController
@RequestMapping("/api")
public class CatalogsResource {

    private final Logger log = LoggerFactory.getLogger(CatalogsResource.class);

    private static final String ENTITY_NAME = "catalogs";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CatalogsService catalogsService;

    private final CatalogsRepository catalogsRepository;

    @Autowired
    private final UserService userService;

    public CatalogsResource(CatalogsService catalogsService, CatalogsRepository catalogsRepository, UserService userService) {
        this.catalogsService = catalogsService;
        this.catalogsRepository = catalogsRepository;
        this.userService = userService;
    }

    /**
     * {@code POST  /catalogs} : Create a new catalogs.
     *
     * @param catalogsDTO the catalogsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new catalogsDTO, or with status {@code 400 (Bad Request)} if the catalogs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/management/catalogs")
    public ResponseEntity<?> createCatalogs(@RequestBody CatalogsDTO catalogsDTO) throws URISyntaxException {
        log.debug("REST request to save Catalogs : {}", catalogsDTO);
        if (catalogsDTO.getId() != null) {
            throw new BadRequestAlertException("A new catalogs cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<User> user = userService.getUserWithAuthorities();
        if(user.isPresent()){
            catalogsDTO.setCreateBy(user.get().getLogin());
            catalogsDTO.setCreateDate(ZonedDateTime.now());
            catalogsDTO.setLastModifiedBy(user.get().getLogin());
            catalogsDTO.setLastModifiedDate(ZonedDateTime.now());
        }
        return ResponseEntity.ok(catalogsService.create(catalogsDTO));
    }

    /**
     * {@code PUT  /catalogs/:id} : Updates an existing catalogs.
     *
     * @param id the id of the catalogsDTO to save.
     * @param catalogsDTO the catalogsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogsDTO,
     * or with status {@code 400 (Bad Request)} if the catalogsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the catalogsDTO couldn't be updated.
     */
    @PutMapping("/catalogs/{id}")
    public ResponseEntity<CatalogsDTO> updateCatalogs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CatalogsDTO catalogsDTO
    ) {
        log.debug("REST request to update Catalogs : {}, {}", id, catalogsDTO);
        if (catalogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!catalogsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CatalogsDTO result = catalogsService.save(catalogsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, catalogsDTO.getId().toString()))
            .body(result);
    }


    @PostMapping("/management/catalogs/update")
    public ResponseEntity<?> updateCatalogs(@RequestBody CatalogsDTO catalogsDTO){
        if (catalogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<User> user = userService.getUserWithAuthorities();
        if(user.isPresent()){
            catalogsDTO.setLastModifiedDate(ZonedDateTime.now());
            catalogsDTO.setLastModifiedBy(user.get().getLogin());
        }
        return ResponseEntity.ok(catalogsService.update(catalogsDTO));
    }

    /**
     * {@code PATCH  /catalogs/:id} : Partial updates given fields of an existing catalogs, field will ignore if it is null
     *
     * @param id the id of the catalogsDTO to save.
     * @param catalogsDTO the catalogsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogsDTO,
     * or with status {@code 400 (Bad Request)} if the catalogsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the catalogsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the catalogsDTO couldn't be updated.
     */
    @PatchMapping(value = "/catalogs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CatalogsDTO> partialUpdateCatalogs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CatalogsDTO catalogsDTO
    ) {
        log.debug("REST request to partial update Catalogs partially : {}, {}", id, catalogsDTO);
        if (catalogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!catalogsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CatalogsDTO> result = catalogsService.partialUpdate(catalogsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, catalogsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /catalogs} : get all the catalogs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of catalogs in body.
     */
    @GetMapping("/catalogs")
    public ResponseEntity<List<CatalogsDTO>> getAllCatalogs(Pageable pageable) {
        log.debug("REST request to get a page of Catalogs");
        Page<CatalogsDTO> page = catalogsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /catalogs/:id} : get the "id" catalogs.
     *
     * @param id the id of the catalogsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the catalogsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/catalogs/{id}")
    public ResponseEntity<CatalogsDTO> getCatalogs(@PathVariable Long id) {
        log.debug("REST request to get Catalogs : {}", id);
        Optional<CatalogsDTO> catalogsDTO = catalogsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(catalogsDTO);
    }

    /**
     * {@code DELETE  /catalogs/:id} : delete the "id" catalogs.
     *
     * @param catalogsDTO is the catalogsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PostMapping("/management/catalogs/delete")
    public ResponseEntity<?> deleteCatalogs(@RequestBody CatalogsDTO catalogsDTO) {
        log.debug("REST request to delete Catalogs : {}", catalogsDTO.getName());
        return ResponseEntity.ok(catalogsService.delete(catalogsDTO.getId()));
    }

    @PostMapping("/catalogs/searchForTree")
    public  ResponseEntity<List<CatalogsDTO>> getCatalogsForTree(@RequestBody CatalogsDTO catalogsDTO){
        List<CatalogsDTO> rs;
        rs = catalogsService.getCatalogsForTree(catalogsDTO);
        return ResponseEntity.ok(rs);
    }

    @PostMapping("/management/catalogs/checkExist")
    public ResponseEntity<?> checkExistDocumentary(@RequestBody CatalogsDTO catalogsDTO){
        return ResponseEntity.ok().body(catalogsService.checkExist(catalogsDTO.getCode()));
    }
}
