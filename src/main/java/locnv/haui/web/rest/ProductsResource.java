package locnv.haui.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import locnv.haui.domain.User;
import locnv.haui.repository.ProductsRepository;
import locnv.haui.service.ProductsService;
import locnv.haui.service.UserService;
import locnv.haui.service.dto.*;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link locnv.haui.domain.Products}.
 */
@RestController
@RequestMapping("/api")
public class ProductsResource {

    private final Logger log = LoggerFactory.getLogger(ProductsResource.class);

    private static final String ENTITY_NAME = "products";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductsService productsService;

    private final ProductsRepository productsRepository;

    @Autowired
    private final UserService userService;

    public ProductsResource(ProductsService productsService, ProductsRepository productsRepository, UserService userService) {
        this.productsService = productsService;
        this.productsRepository = productsRepository;
        this.userService = userService;
    }

    /**
     * {@code POST  /products} : Create a new products.
     *
     * @param productsDTO the productsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productsDTO, or with status {@code 400 (Bad Request)} if the products has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/products")
    public ResponseEntity<ProductsDTO> createProducts(@RequestBody ProductsDTO productsDTO) throws URISyntaxException {
        log.debug("REST request to save Products : {}", productsDTO);
        if (productsDTO.getId() != null) {
            throw new BadRequestAlertException("A new products cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductsDTO result = productsService.save(productsDTO);
        return ResponseEntity
            .created(new URI("/api/products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /products/:id} : Updates an existing products.
     *
     * @param id the id of the productsDTO to save.
     * @param productsDTO the productsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productsDTO,
     * or with status {@code 400 (Bad Request)} if the productsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productsDTO couldn't be updated.
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductsDTO> updateProducts(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductsDTO productsDTO
    ) {
        log.debug("REST request to update Products : {}, {}", id, productsDTO);
        if (productsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductsDTO result = productsService.save(productsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /products/:id} : Partial updates given fields of an existing products, field will ignore if it is null
     *
     * @param id the id of the productsDTO to save.
     * @param productsDTO the productsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productsDTO,
     * or with status {@code 400 (Bad Request)} if the productsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productsDTO couldn't be updated.
     */
    @PatchMapping(value = "/products/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductsDTO> partialUpdateProducts(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductsDTO productsDTO
    ) {
        log.debug("REST request to partial update Products partially : {}, {}", id, productsDTO);
        if (productsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductsDTO> result = productsService.partialUpdate(productsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /products} : get all the products.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductsDTO>> getAllProducts(Pageable pageable) {
        log.debug("REST request to get a page of Products");
        Page<ProductsDTO> page = productsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /products/:id} : get the "id" products.
     *
     * @param id the id of the productsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProducts(@PathVariable Long id) {
        log.debug("REST request to get Products : {}", id);
        ServiceResult rs = productsService.searchProduct(id);
        return ResponseEntity.ok(rs);
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" products.
     *
     * @param id the id of the productsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProducts(@PathVariable Long id) {
        log.debug("REST request to delete Products : {}", id);
        productsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/products/search")
    public ResponseEntity<?> searchProducts(@RequestBody ProductsDTO productsDTO,
                                            @RequestParam(value = "page", defaultValue = "1") int page,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        DataDTO<ProductFullDataDTO> result =productsService.search(productsDTO, page, pageSize);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/products/searchForViewProduct")
    public ResponseEntity<?> searchForViewProduct(@RequestBody ProductsDTO productsDTO,
                                            @RequestParam(value = "page", defaultValue = "1") int page,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        DataDTO<ProductFullDataDTO> result =productsService.searchForViewPage(productsDTO, page, pageSize);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/products/specialSearch")
    public ResponseEntity<?> searchProductsForView(@RequestBody ProductsDTO productsDTO){
        DataDTO<ProductFullDataDTO> res = productsService.searchForView((productsDTO));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/products/searchByCatalogNoChild")
    public ResponseEntity<?> searchByCatalogNoChild(){
        DataDTO<ProductFullDataDTO> res = productsService.searchByCatalogNoChild();
        return ResponseEntity.ok(res);
    }

    @PostMapping(value= "/products/create", consumes = { "multipart/form-data" })
    public ResponseEntity<?> createProducts(
        @RequestParam("image")MultipartFile image,
        @RequestParam("catalog")Long catalog,
        @RequestParam("code") String code,
        @RequestParam("name") String name,
        @RequestParam("brand") String brand,
        @RequestParam("price") Double price,
        @RequestParam("productDetails") String productDetails,
        @RequestParam("descriptionDocument") String descriptionDocument,
        @RequestParam("status") Integer status,
        @RequestParam("spec") String spec) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<ProductSpecsDTO> specs = mapper.readValue(spec, new TypeReference<List<ProductSpecsDTO>>(){});
        ProductsDTO productsDTO = new ProductsDTO();
        Optional<User> user = userService.getUserWithAuthorities();
        if(user.isPresent()){
            productsDTO.setCreateBy(user.get().getLogin());
            productsDTO.setCreateDate(ZonedDateTime.now());
            productsDTO.setLastModifiedBy(user.get().getLogin());
            productsDTO.setLastModifiedDate(ZonedDateTime.now());
        }
        productsDTO.setCatalogId(catalog);
        productsDTO.setCode(code);
        productsDTO.setName(name);
        productsDTO.setBrand(brand);
        productsDTO.setProductDetails(productDetails);
        productsDTO.setDescriptionDocument(descriptionDocument);
        productsDTO.setStatus(status == 1);
        productsDTO.setPrice(price);
        ServiceResult fullDataDTO = productsService.create(image, productsDTO, specs);
        return ResponseEntity.ok(fullDataDTO);
    }

    @PostMapping(value= "/products/update", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateProduct(
        @RequestParam("id") Long id,
        @RequestParam(value = "image", required = false)MultipartFile image,
        @RequestParam("catalog")Long catalog,
        @RequestParam("code") String code,
        @RequestParam("name") String name,
        @RequestParam("brand") String brand,
        @RequestParam("price") Double price,
        @RequestParam("productDetails") String productDetails,
        @RequestParam("descriptionDocument") String descriptionDocument,
        @RequestParam("status") Integer status,
        @RequestParam("spec") String spec) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<ProductSpecsDTO> specs = mapper.readValue(spec, new TypeReference<List<ProductSpecsDTO>>(){});
        ProductsDTO productsDTO = new ProductsDTO();
        Optional<User> user = userService.getUserWithAuthorities();
        if(user.isPresent()){
            productsDTO.setLastModifiedBy(user.get().getLogin());
            productsDTO.setLastModifiedDate(ZonedDateTime.now());
        }
        productsDTO.setId(id);
        productsDTO.setCatalogId(catalog);
        productsDTO.setCode(code);
        productsDTO.setName(name);
        productsDTO.setBrand(brand);
        productsDTO.setProductDetails(productDetails);
        productsDTO.setDescriptionDocument(descriptionDocument);
        productsDTO.setStatus(status == 1);
        productsDTO.setPrice(price);
        ServiceResult fullDataDTO = productsService.update(image, productsDTO, specs);
        return ResponseEntity.ok(fullDataDTO);
    }
}
