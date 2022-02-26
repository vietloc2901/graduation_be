package locnv.haui.web.rest;

import static locnv.haui.web.rest.TestUtil.sameInstant;
import static locnv.haui.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import locnv.haui.IntegrationTest;
import locnv.haui.domain.OrderItems;
import locnv.haui.repository.OrderItemsRepository;
import locnv.haui.service.dto.OrderItemsDTO;
import locnv.haui.service.mapper.OrderItemsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link OrderItemsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderItemsResourceIT {

    private static final Long DEFAULT_ORDER_ID = 1L;
    private static final Long UPDATED_ORDER_ID = 2L;

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final ZonedDateTime DEFAULT_CREATE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/order-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderItemsMockMvc;

    private OrderItems orderItems;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItems createEntity(EntityManager em) {
        OrderItems orderItems = new OrderItems()
            .orderId(DEFAULT_ORDER_ID)
            .productId(DEFAULT_PRODUCT_ID)
            .quantity(DEFAULT_QUANTITY)
            .price(DEFAULT_PRICE)
            .createDate(DEFAULT_CREATE_DATE);
        return orderItems;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItems createUpdatedEntity(EntityManager em) {
        OrderItems orderItems = new OrderItems()
            .orderId(UPDATED_ORDER_ID)
            .productId(UPDATED_PRODUCT_ID)
            .quantity(UPDATED_QUANTITY)
            .price(UPDATED_PRICE)
            .createDate(UPDATED_CREATE_DATE);
        return orderItems;
    }

    @BeforeEach
    public void initTest() {
        orderItems = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderItems() throws Exception {
        int databaseSizeBeforeCreate = orderItemsRepository.findAll().size();
        // Create the OrderItems
        OrderItemsDTO orderItemsDTO = orderItemsMapper.toDto(orderItems);
        restOrderItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderItemsDTO)))
            .andExpect(status().isCreated());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeCreate + 1);
        OrderItems testOrderItems = orderItemsList.get(orderItemsList.size() - 1);
        assertThat(testOrderItems.getOrderId()).isEqualTo(DEFAULT_ORDER_ID);
        assertThat(testOrderItems.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testOrderItems.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testOrderItems.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testOrderItems.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
    }

    @Test
    @Transactional
    void createOrderItemsWithExistingId() throws Exception {
        // Create the OrderItems with an existing ID
        orderItems.setId(1L);
        OrderItemsDTO orderItemsDTO = orderItemsMapper.toDto(orderItems);

        int databaseSizeBeforeCreate = orderItemsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderItemsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrderItems() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        // Get all the orderItemsList
        restOrderItemsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItems.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderId").value(hasItem(DEFAULT_ORDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(sameInstant(DEFAULT_CREATE_DATE))));
    }

    @Test
    @Transactional
    void getOrderItems() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        // Get the orderItems
        restOrderItemsMockMvc
            .perform(get(ENTITY_API_URL_ID, orderItems.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderItems.getId().intValue()))
            .andExpect(jsonPath("$.orderId").value(DEFAULT_ORDER_ID.intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.createDate").value(sameInstant(DEFAULT_CREATE_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingOrderItems() throws Exception {
        // Get the orderItems
        restOrderItemsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrderItems() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        int databaseSizeBeforeUpdate = orderItemsRepository.findAll().size();

        // Update the orderItems
        OrderItems updatedOrderItems = orderItemsRepository.findById(orderItems.getId()).get();
        // Disconnect from session so that the updates on updatedOrderItems are not directly saved in db
        em.detach(updatedOrderItems);
        updatedOrderItems
            .orderId(UPDATED_ORDER_ID)
            .productId(UPDATED_PRODUCT_ID)
            .quantity(UPDATED_QUANTITY)
            .price(UPDATED_PRICE)
            .createDate(UPDATED_CREATE_DATE);
        OrderItemsDTO orderItemsDTO = orderItemsMapper.toDto(updatedOrderItems);

        restOrderItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderItemsDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeUpdate);
        OrderItems testOrderItems = orderItemsList.get(orderItemsList.size() - 1);
        assertThat(testOrderItems.getOrderId()).isEqualTo(UPDATED_ORDER_ID);
        assertThat(testOrderItems.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testOrderItems.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderItems.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testOrderItems.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    void putNonExistingOrderItems() throws Exception {
        int databaseSizeBeforeUpdate = orderItemsRepository.findAll().size();
        orderItems.setId(count.incrementAndGet());

        // Create the OrderItems
        OrderItemsDTO orderItemsDTO = orderItemsMapper.toDto(orderItems);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderItems() throws Exception {
        int databaseSizeBeforeUpdate = orderItemsRepository.findAll().size();
        orderItems.setId(count.incrementAndGet());

        // Create the OrderItems
        OrderItemsDTO orderItemsDTO = orderItemsMapper.toDto(orderItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderItems() throws Exception {
        int databaseSizeBeforeUpdate = orderItemsRepository.findAll().size();
        orderItems.setId(count.incrementAndGet());

        // Create the OrderItems
        OrderItemsDTO orderItemsDTO = orderItemsMapper.toDto(orderItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderItemsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderItemsWithPatch() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        int databaseSizeBeforeUpdate = orderItemsRepository.findAll().size();

        // Update the orderItems using partial update
        OrderItems partialUpdatedOrderItems = new OrderItems();
        partialUpdatedOrderItems.setId(orderItems.getId());

        partialUpdatedOrderItems.orderId(UPDATED_ORDER_ID).quantity(UPDATED_QUANTITY);

        restOrderItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderItems))
            )
            .andExpect(status().isOk());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeUpdate);
        OrderItems testOrderItems = orderItemsList.get(orderItemsList.size() - 1);
        assertThat(testOrderItems.getOrderId()).isEqualTo(UPDATED_ORDER_ID);
        assertThat(testOrderItems.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testOrderItems.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderItems.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testOrderItems.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
    }

    @Test
    @Transactional
    void fullUpdateOrderItemsWithPatch() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        int databaseSizeBeforeUpdate = orderItemsRepository.findAll().size();

        // Update the orderItems using partial update
        OrderItems partialUpdatedOrderItems = new OrderItems();
        partialUpdatedOrderItems.setId(orderItems.getId());

        partialUpdatedOrderItems
            .orderId(UPDATED_ORDER_ID)
            .productId(UPDATED_PRODUCT_ID)
            .quantity(UPDATED_QUANTITY)
            .price(UPDATED_PRICE)
            .createDate(UPDATED_CREATE_DATE);

        restOrderItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderItems))
            )
            .andExpect(status().isOk());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeUpdate);
        OrderItems testOrderItems = orderItemsList.get(orderItemsList.size() - 1);
        assertThat(testOrderItems.getOrderId()).isEqualTo(UPDATED_ORDER_ID);
        assertThat(testOrderItems.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testOrderItems.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderItems.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testOrderItems.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingOrderItems() throws Exception {
        int databaseSizeBeforeUpdate = orderItemsRepository.findAll().size();
        orderItems.setId(count.incrementAndGet());

        // Create the OrderItems
        OrderItemsDTO orderItemsDTO = orderItemsMapper.toDto(orderItems);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderItemsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderItems() throws Exception {
        int databaseSizeBeforeUpdate = orderItemsRepository.findAll().size();
        orderItems.setId(count.incrementAndGet());

        // Create the OrderItems
        OrderItemsDTO orderItemsDTO = orderItemsMapper.toDto(orderItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderItems() throws Exception {
        int databaseSizeBeforeUpdate = orderItemsRepository.findAll().size();
        orderItems.setId(count.incrementAndGet());

        // Create the OrderItems
        OrderItemsDTO orderItemsDTO = orderItemsMapper.toDto(orderItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(orderItemsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItems in the database
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderItems() throws Exception {
        // Initialize the database
        orderItemsRepository.saveAndFlush(orderItems);

        int databaseSizeBeforeDelete = orderItemsRepository.findAll().size();

        // Delete the orderItems
        restOrderItemsMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderItems.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderItems> orderItemsList = orderItemsRepository.findAll();
        assertThat(orderItemsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
