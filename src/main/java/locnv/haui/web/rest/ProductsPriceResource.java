package locnv.haui.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import locnv.haui.repository.ProductsPriceRepository;
import locnv.haui.service.ProductsPriceService;
import locnv.haui.service.dto.ProductsPriceDTO;
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
 * REST controller for managing {@link locnv.haui.domain.ProductsPrice}.
 */
@RestController
@RequestMapping("/api")
public class ProductsPriceResource {

    private final Logger log = LoggerFactory.getLogger(ProductsPriceResource.class);

    private static final String ENTITY_NAME = "productsPrice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductsPriceService productsPriceService;

    private final ProductsPriceRepository productsPriceRepository;

    public ProductsPriceResource(ProductsPriceService productsPriceService, ProductsPriceRepository productsPriceRepository) {
        this.productsPriceService = productsPriceService;
        this.productsPriceRepository = productsPriceRepository;
    }

    /**
     * {@code POST  /products-prices} : Create a new productsPrice.
     *
     * @param productsPriceDTO the productsPriceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productsPriceDTO, or with status {@code 400 (Bad Request)} if the productsPrice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/products-prices")
    public ResponseEntity<ProductsPriceDTO> createProductsPrice(@RequestBody ProductsPriceDTO productsPriceDTO) throws URISyntaxException {
        log.debug("REST request to save ProductsPrice : {}", productsPriceDTO);
        if (productsPriceDTO.getId() != null) {
            throw new BadRequestAlertException("A new productsPrice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductsPriceDTO result = productsPriceService.save(productsPriceDTO);
        return ResponseEntity
            .created(new URI("/api/products-prices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /products-prices/:id} : Updates an existing productsPrice.
     *
     * @param id the id of the productsPriceDTO to save.
     * @param productsPriceDTO the productsPriceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productsPriceDTO,
     * or with status {@code 400 (Bad Request)} if the productsPriceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productsPriceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/products-prices/{id}")
    public ResponseEntity<ProductsPriceDTO> updateProductsPrice(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductsPriceDTO productsPriceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductsPrice : {}, {}", id, productsPriceDTO);
        if (productsPriceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productsPriceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productsPriceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductsPriceDTO result = productsPriceService.save(productsPriceDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productsPriceDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /products-prices/:id} : Partial updates given fields of an existing productsPrice, field will ignore if it is null
     *
     * @param id the id of the productsPriceDTO to save.
     * @param productsPriceDTO the productsPriceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productsPriceDTO,
     * or with status {@code 400 (Bad Request)} if the productsPriceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productsPriceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productsPriceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/products-prices/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductsPriceDTO> partialUpdateProductsPrice(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductsPriceDTO productsPriceDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductsPrice partially : {}, {}", id, productsPriceDTO);
        if (productsPriceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productsPriceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productsPriceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductsPriceDTO> result = productsPriceService.partialUpdate(productsPriceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productsPriceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /products-prices} : get all the productsPrices.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productsPrices in body.
     */
    @GetMapping("/products-prices")
    public ResponseEntity<List<ProductsPriceDTO>> getAllProductsPrices(Pageable pageable) {
        log.debug("REST request to get a page of ProductsPrices");
        Page<ProductsPriceDTO> page = productsPriceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /products-prices/:id} : get the "id" productsPrice.
     *
     * @param id the id of the productsPriceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productsPriceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/products-prices/{id}")
    public ResponseEntity<ProductsPriceDTO> getProductsPrice(@PathVariable Long id) {
        log.debug("REST request to get ProductsPrice : {}", id);
        Optional<ProductsPriceDTO> productsPriceDTO = productsPriceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productsPriceDTO);
    }

    /**
     * {@code DELETE  /products-prices/:id} : delete the "id" productsPrice.
     *
     * @param id the id of the productsPriceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/products-prices/{id}")
    public ResponseEntity<Void> deleteProductsPrice(@PathVariable Long id) {
        log.debug("REST request to delete ProductsPrice : {}", id);
        productsPriceService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
