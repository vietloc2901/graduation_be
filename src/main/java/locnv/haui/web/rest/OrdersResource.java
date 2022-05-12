package locnv.haui.web.rest;

import locnv.haui.commons.ExportUtils;
import locnv.haui.commons.Translator;
import locnv.haui.repository.OrdersRepository;
import locnv.haui.service.OrdersService;
import locnv.haui.service.dto.*;
import locnv.haui.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link locnv.haui.domain.Orders}.
 */
@RestController
@RequestMapping("/api")
public class OrdersResource {

    private final Logger log = LoggerFactory.getLogger(OrdersResource.class);

    private static final String ENTITY_NAME = "orders";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrdersService ordersService;

    private final OrdersRepository ordersRepository;

    private final ExportUtils exportUtils;

    public OrdersResource(OrdersService ordersService, OrdersRepository ordersRepository, ExportUtils exportUtils) {
        this.ordersService = ordersService;
        this.ordersRepository = ordersRepository;
        this.exportUtils = exportUtils;
    }

    /**
     * {@code POST  /orders} : Create a new orders.
     *
     * @param ordersDTO the ordersDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ordersDTO, or with status {@code 400 (Bad Request)} if the orders has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/orders")
    public ResponseEntity<OrdersDTO> createOrders(@RequestBody OrdersDTO ordersDTO) throws URISyntaxException {
        log.debug("REST request to save Orders : {}", ordersDTO);
        if (ordersDTO.getId() != null) {
            throw new BadRequestAlertException("A new orders cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrdersDTO result = ordersService.save(ordersDTO);
        return ResponseEntity
            .created(new URI("/api/orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing orders.
     *
     * @param id the id of the ordersDTO to save.
     * @param ordersDTO the ordersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO,
     * or with status {@code 400 (Bad Request)} if the ordersDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ordersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/orders/{id}")
    public ResponseEntity<OrdersDTO> updateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrdersDTO ordersDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Orders : {}, {}", id, ordersDTO);
        if (ordersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrdersDTO result = ordersService.save(ordersDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ordersDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing orders, field will ignore if it is null
     *
     * @param id the id of the ordersDTO to save.
     * @param ordersDTO the ordersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ordersDTO,
     * or with status {@code 400 (Bad Request)} if the ordersDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ordersDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ordersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/orders/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrdersDTO> partialUpdateOrders(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrdersDTO ordersDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Orders partially : {}, {}", id, ordersDTO);
        if (ordersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ordersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ordersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrdersDTO> result = ordersService.partialUpdate(ordersDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ordersDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrdersDTO>> getAllOrders(Pageable pageable) {
        log.debug("REST request to get a page of Orders");
        Page<OrdersDTO> page = ordersService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /orders/:id} : get the "id" orders.
     *
     * @param id the id of the ordersDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ordersDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrdersDTO> getOrders(@PathVariable Long id) {
        log.debug("REST request to get Orders : {}", id);
        Optional<OrdersDTO> ordersDTO = ordersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ordersDTO);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" orders.
     *
     * @param id the id of the ordersDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrders(@PathVariable Long id) {
        log.debug("REST request to delete Orders : {}", id);
        ordersService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/orders/create")
    public ResponseEntity<?> createNewcảOrders(@RequestBody OrdersDTO ordersDTO){
        ServiceResult rs = ordersService.create(ordersDTO);

        return ResponseEntity.ok(rs);
    }

    @PostMapping("/orders/search")
    public ResponseEntity<?> search(@RequestBody OrdersDTO ordersDTO,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        DataDTO rs = ordersService.search(ordersDTO, page, pageSize);

        return ResponseEntity.ok(rs);
    }

    @PostMapping("/orders/getById")
    public ResponseEntity<?> getById(@RequestBody OrdersDTO ordersDTO){
        if(Objects.isNull(ordersDTO.getId())){
            return null;
        }else{
            return ResponseEntity.ok(ordersService.findById(ordersDTO));
        }
    }

    @PostMapping("/orders/getChangeStatus")
    public ResponseEntity<?> changeOrderStatus(@RequestBody OrdersDTO ordersDTO){
        ServiceResult<OrdersDTO> rs = ordersService.changeStatus(ordersDTO);
        return ResponseEntity.ok(rs);
    }

    @PostMapping("/orders/getWithAuthority")
    public ResponseEntity<?> getWithAuthority(@RequestBody OrdersDTO ordersDTO,
                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        DataDTO rs = ordersService.getWithAuthority(ordersDTO, page, pageSize);

        return ResponseEntity.ok(rs);
    }

    @PostMapping("/orders/cancelOrder")
    public ResponseEntity<?> cancelOrder(@RequestBody OrdersDTO ordersDTO){
        return ResponseEntity.ok(ordersService.cancelOrder(ordersDTO));
    }

    @PostMapping("/orders/exportExcel")
    public ResponseEntity<?> exportExcel(@RequestBody OrdersDTO ordersDTO) throws IOException, IllegalAccessException {
        List<OrdersDTO> listExport = ordersService.getDataExport(ordersDTO);
        List<ExcelColumn> listColumn = buildColumnExport();
        String title = Translator.toLocale("order.title.export");
        ExcelTitle excelTitle = new ExcelTitle(title,title,"");
        ByteArrayInputStream byteArrayInputStream = exportUtils.onExport(listColumn, listExport, 3, 0, excelTitle, true);
        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
        return ResponseEntity.ok()
            .contentLength(byteArrayInputStream.available())
            .contentType(MediaType.parseMediaType("application/octet-stream"))
            .body(resource);
    }

    private List<ExcelColumn> buildColumnExport() {
        List<ExcelColumn> listColumn = new ArrayList<>();
        listColumn.add(new ExcelColumn("id", Translator.toLocale("order.excelTitle.id"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("name", Translator.toLocale("order.excelTitle.name"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("receiverName", Translator.toLocale("order.excelTitle.receiverName"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("phone", Translator.toLocale("order.excelTitle.phone"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("receiverPhone", Translator.toLocale("order.excelTitle.receiverPhone"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("email", Translator.toLocale("order.excelTitle.email"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("address", Translator.toLocale("order.excelTitle.address"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("createDateString", Translator.toLocale("order.excelTitle.createDate"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("createBy", Translator.toLocale("order.excelTitle.createBy"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("statusString", Translator.toLocale("order.excelTitle.status"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("sumPrice", Translator.toLocale("order.excelTitle.sumPrice"), ExcelColumn.ALIGN_MENT.LEFT));
        return listColumn;
    }

    @PostMapping("/statistic")
    public ResponseEntity<?> statistic(@RequestBody StatisticDTO statisticDTO) throws IOException, IllegalAccessException {

        StatisticDTO rs = new StatisticDTO();
        rs = ordersService.statistic(statisticDTO);

        return ResponseEntity.ok(rs);
    }

}
