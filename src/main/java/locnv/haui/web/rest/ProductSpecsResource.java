package locnv.haui.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import locnv.haui.repository.ProductSpecsRepository;
import locnv.haui.service.ProductSpecsService;
import locnv.haui.service.dto.ProductSpecsDTO;
import locnv.haui.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link locnv.haui.domain.ProductSpecs}.
 */
@RestController
@RequestMapping("/api")
public class ProductSpecsResource {

    private final Logger log = LoggerFactory.getLogger(ProductSpecsResource.class);

    private static final String ENTITY_NAME = "productSpecs";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductSpecsService productSpecsService;

    private final ProductSpecsRepository productSpecsRepository;

    public ProductSpecsResource(ProductSpecsService productSpecsService, ProductSpecsRepository productSpecsRepository) {
        this.productSpecsService = productSpecsService;
        this.productSpecsRepository = productSpecsRepository;
    }

    /**
     * {@code POST  /product-specs} : Create a new productSpecs.
     *
     * @param productSpecsDTO the productSpecsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productSpecsDTO, or with status {@code 400 (Bad Request)} if the productSpecs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-specs")
    public ResponseEntity<ProductSpecsDTO> createProductSpecs(@RequestBody ProductSpecsDTO productSpecsDTO) throws URISyntaxException {
        log.debug("REST request to save ProductSpecs : {}", productSpecsDTO);
        if (productSpecsDTO.getId() != null) {
            throw new BadRequestAlertException("A new productSpecs cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductSpecsDTO result = productSpecsService.save(productSpecsDTO);
        return ResponseEntity
            .created(new URI("/api/product-specs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-specs/:id} : Updates an existing productSpecs.
     *
     * @param id the id of the productSpecsDTO to save.
     * @param productSpecsDTO the productSpecsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productSpecsDTO,
     * or with status {@code 400 (Bad Request)} if the productSpecsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productSpecsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-specs/{id}")
    public ResponseEntity<ProductSpecsDTO> updateProductSpecs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductSpecsDTO productSpecsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductSpecs : {}, {}", id, productSpecsDTO);
        if (productSpecsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productSpecsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productSpecsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductSpecsDTO result = productSpecsService.save(productSpecsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productSpecsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /product-specs/:id} : Partial updates given fields of an existing productSpecs, field will ignore if it is null
     *
     * @param id the id of the productSpecsDTO to save.
     * @param productSpecsDTO the productSpecsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productSpecsDTO,
     * or with status {@code 400 (Bad Request)} if the productSpecsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productSpecsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productSpecsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-specs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductSpecsDTO> partialUpdateProductSpecs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductSpecsDTO productSpecsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductSpecs partially : {}, {}", id, productSpecsDTO);
        if (productSpecsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productSpecsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productSpecsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductSpecsDTO> result = productSpecsService.partialUpdate(productSpecsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productSpecsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-specs} : get all the productSpecs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productSpecs in body.
     */
    @GetMapping("/product-specs")
    public ResponseEntity<List<ProductSpecsDTO>> getAllProductSpecs(Pageable pageable) {
        log.debug("REST request to get a page of ProductSpecs");
        Page<ProductSpecsDTO> page = productSpecsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-specs/:id} : get the "id" productSpecs.
     *
     * @param id the id of the productSpecsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productSpecsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-specs/{id}")
    public ResponseEntity<ProductSpecsDTO> getProductSpecs(@PathVariable Long id) {
        log.debug("REST request to get ProductSpecs : {}", id);
        Optional<ProductSpecsDTO> productSpecsDTO = productSpecsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productSpecsDTO);
    }

    /**
     * {@code DELETE  /product-specs/:id} : delete the "id" productSpecs.
     *
     * @param id the id of the productSpecsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-specs/{id}")
    public ResponseEntity<Void> deleteProductSpecs(@PathVariable Long id) {
        log.debug("REST request to delete ProductSpecs : {}", id);
        productSpecsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
