package locnv.haui.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import locnv.haui.repository.OrderItemsRepository;
import locnv.haui.service.OrderItemsService;
import locnv.haui.service.dto.OrderItemsDTO;
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
 * REST controller for managing {@link locnv.haui.domain.OrderItems}.
 */
@RestController
@RequestMapping("/api")
public class OrderItemsResource {

    private final Logger log = LoggerFactory.getLogger(OrderItemsResource.class);

    private static final String ENTITY_NAME = "orderItems";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderItemsService orderItemsService;

    private final OrderItemsRepository orderItemsRepository;

    public OrderItemsResource(OrderItemsService orderItemsService, OrderItemsRepository orderItemsRepository) {
        this.orderItemsService = orderItemsService;
        this.orderItemsRepository = orderItemsRepository;
    }

    /**
     * {@code POST  /order-items} : Create a new orderItems.
     *
     * @param orderItemsDTO the orderItemsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderItemsDTO, or with status {@code 400 (Bad Request)} if the orderItems has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-items")
    public ResponseEntity<OrderItemsDTO> createOrderItems(@RequestBody OrderItemsDTO orderItemsDTO) throws URISyntaxException {
        log.debug("REST request to save OrderItems : {}", orderItemsDTO);
        if (orderItemsDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderItems cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderItemsDTO result = orderItemsService.save(orderItemsDTO);
        return ResponseEntity
            .created(new URI("/api/order-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-items/:id} : Updates an existing orderItems.
     *
     * @param id the id of the orderItemsDTO to save.
     * @param orderItemsDTO the orderItemsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemsDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderItemsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-items/{id}")
    public ResponseEntity<OrderItemsDTO> updateOrderItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderItemsDTO orderItemsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderItems : {}, {}", id, orderItemsDTO);
        if (orderItemsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderItemsDTO result = orderItemsService.save(orderItemsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItemsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-items/:id} : Partial updates given fields of an existing orderItems, field will ignore if it is null
     *
     * @param id the id of the orderItemsDTO to save.
     * @param orderItemsDTO the orderItemsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemsDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderItemsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderItemsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderItemsDTO> partialUpdateOrderItems(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderItemsDTO orderItemsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderItems partially : {}, {}", id, orderItemsDTO);
        if (orderItemsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderItemsDTO> result = orderItemsService.partialUpdate(orderItemsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItemsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-items} : get all the orderItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderItems in body.
     */
    @GetMapping("/order-items")
    public ResponseEntity<List<OrderItemsDTO>> getAllOrderItems(Pageable pageable) {
        log.debug("REST request to get a page of OrderItems");
        Page<OrderItemsDTO> page = orderItemsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /order-items/:id} : get the "id" orderItems.
     *
     * @param id the id of the orderItemsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderItemsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-items/{id}")
    public ResponseEntity<OrderItemsDTO> getOrderItems(@PathVariable Long id) {
        log.debug("REST request to get OrderItems : {}", id);
        Optional<OrderItemsDTO> orderItemsDTO = orderItemsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderItemsDTO);
    }

    /**
     * {@code DELETE  /order-items/:id} : delete the "id" orderItems.
     *
     * @param id the id of the orderItemsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-items/{id}")
    public ResponseEntity<Void> deleteOrderItems(@PathVariable Long id) {
        log.debug("REST request to delete OrderItems : {}", id);
        orderItemsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
