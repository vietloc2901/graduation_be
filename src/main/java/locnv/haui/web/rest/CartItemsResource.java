package locnv.haui.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import locnv.haui.repository.CartItemsRepository;
import locnv.haui.service.CartItemsService;
import locnv.haui.service.dto.CartItemsDTO;
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
 * REST controller for managing {@link locnv.haui.domain.CartItems}.
 */
@RestController
@RequestMapping("/api")
public class CartItemsResource {

    private final Logger log = LoggerFactory.getLogger(CartItemsResource.class);

    private static final String ENTITY_NAME = "cartItems";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartItemsService cartItemsService;

    private final CartItemsRepository cartItemsRepository;

    public CartItemsResource(CartItemsService cartItemsService, CartItemsRepository cartItemsRepository) {
        this.cartItemsService = cartItemsService;
        this.cartItemsRepository = cartItemsRepository;
    }

    /**
     * {@code POST  /cart-items} : Create a new cartItems.
     *
     * @param cartItemsDTO the cartItemsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cartItemsDTO, or with status {@code 400 (Bad Request)} if the cartItems has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cart-items")
    public ResponseEntity<CartItemsDTO> createCartItems(@RequestBody CartItemsDTO cartItemsDTO) throws URISyntaxException {
        log.debug("REST request to save CartItems : {}", cartItemsDTO);
        if (cartItemsDTO.getId() != null) {
            throw new BadRequestAlertException("A new cartItems cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CartItemsDTO result = cartItemsService.save(cartItemsDTO);
        return ResponseEntity
            .created(new URI("/api/cart-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cart-items/:id} : Updates an existing cartItems.
     *
     * @param id the id of the cartItemsDTO to save.
     * @param cartItemsDTO the cartItemsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItemsDTO,
     * or with status {@code 400 (Bad Request)} if the cartItemsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cartItemsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cart-items/{id}")
    public ResponseEntity<CartItemsDTO> updateCartItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CartItemsDTO cartItemsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CartItems : {}, {}", id, cartItemsDTO);
        if (cartItemsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartItemsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CartItemsDTO result = cartItemsService.save(cartItemsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cartItemsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cart-items/:id} : Partial updates given fields of an existing cartItems, field will ignore if it is null
     *
     * @param id the id of the cartItemsDTO to save.
     * @param cartItemsDTO the cartItemsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItemsDTO,
     * or with status {@code 400 (Bad Request)} if the cartItemsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cartItemsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cartItemsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cart-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CartItemsDTO> partialUpdateCartItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CartItemsDTO cartItemsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CartItems partially : {}, {}", id, cartItemsDTO);
        if (cartItemsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartItemsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CartItemsDTO> result = cartItemsService.partialUpdate(cartItemsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cartItemsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cart-items} : get all the cartItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cartItems in body.
     */
    @GetMapping("/cart-items")
    public ResponseEntity<List<CartItemsDTO>> getAllCartItems(Pageable pageable) {
        log.debug("REST request to get a page of CartItems");
        Page<CartItemsDTO> page = cartItemsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cart-items/:id} : get the "id" cartItems.
     *
     * @param id the id of the cartItemsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cartItemsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cart-items/{id}")
    public ResponseEntity<CartItemsDTO> getCartItems(@PathVariable Long id) {
        log.debug("REST request to get CartItems : {}", id);
        Optional<CartItemsDTO> cartItemsDTO = cartItemsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cartItemsDTO);
    }

    /**
     * {@code DELETE  /cart-items/:id} : delete the "id" cartItems.
     *
     * @param id the id of the cartItemsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<Void> deleteCartItems(@PathVariable Long id) {
        log.debug("REST request to delete CartItems : {}", id);
        cartItemsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
