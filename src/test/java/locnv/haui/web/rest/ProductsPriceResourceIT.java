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
import locnv.haui.domain.ProductsPrice;
import locnv.haui.repository.ProductsPriceRepository;
import locnv.haui.service.dto.ProductsPriceDTO;
import locnv.haui.service.mapper.ProductsPriceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductsPriceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductsPriceResourceIT {

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final ZonedDateTime DEFAULT_APPLY_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_APPLY_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CREATE_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATE_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/products-prices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductsPriceRepository productsPriceRepository;

    @Autowired
    private ProductsPriceMapper productsPriceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductsPriceMockMvc;

    private ProductsPrice productsPrice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductsPrice createEntity(EntityManager em) {
        ProductsPrice productsPrice = new ProductsPrice()
            .productId(DEFAULT_PRODUCT_ID)
            .price(DEFAULT_PRICE)
            .applyDate(DEFAULT_APPLY_DATE)
            .createBy(DEFAULT_CREATE_BY);
        return productsPrice;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductsPrice createUpdatedEntity(EntityManager em) {
        ProductsPrice productsPrice = new ProductsPrice()
            .productId(UPDATED_PRODUCT_ID)
            .price(UPDATED_PRICE)
            .applyDate(UPDATED_APPLY_DATE)
            .createBy(UPDATED_CREATE_BY);
        return productsPrice;
    }

    @BeforeEach
    public void initTest() {
        productsPrice = createEntity(em);
    }

    @Test
    @Transactional
    void createProductsPrice() throws Exception {
        int databaseSizeBeforeCreate = productsPriceRepository.findAll().size();
        // Create the ProductsPrice
        ProductsPriceDTO productsPriceDTO = productsPriceMapper.toDto(productsPrice);
        restProductsPriceMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsPriceDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeCreate + 1);
        ProductsPrice testProductsPrice = productsPriceList.get(productsPriceList.size() - 1);
        assertThat(testProductsPrice.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testProductsPrice.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testProductsPrice.getApplyDate()).isEqualTo(DEFAULT_APPLY_DATE);
        assertThat(testProductsPrice.getCreateBy()).isEqualTo(DEFAULT_CREATE_BY);
    }

    @Test
    @Transactional
    void createProductsPriceWithExistingId() throws Exception {
        // Create the ProductsPrice with an existing ID
        productsPrice.setId(1L);
        ProductsPriceDTO productsPriceDTO = productsPriceMapper.toDto(productsPrice);

        int databaseSizeBeforeCreate = productsPriceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductsPriceMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsPriceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProductsPrices() throws Exception {
        // Initialize the database
        productsPriceRepository.saveAndFlush(productsPrice);

        // Get all the productsPriceList
        restProductsPriceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productsPrice.getId().intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].applyDate").value(hasItem(sameInstant(DEFAULT_APPLY_DATE))))
            .andExpect(jsonPath("$.[*].createBy").value(hasItem(DEFAULT_CREATE_BY)));
    }

    @Test
    @Transactional
    void getProductsPrice() throws Exception {
        // Initialize the database
        productsPriceRepository.saveAndFlush(productsPrice);

        // Get the productsPrice
        restProductsPriceMockMvc
            .perform(get(ENTITY_API_URL_ID, productsPrice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productsPrice.getId().intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.applyDate").value(sameInstant(DEFAULT_APPLY_DATE)))
            .andExpect(jsonPath("$.createBy").value(DEFAULT_CREATE_BY));
    }

    @Test
    @Transactional
    void getNonExistingProductsPrice() throws Exception {
        // Get the productsPrice
        restProductsPriceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProductsPrice() throws Exception {
        // Initialize the database
        productsPriceRepository.saveAndFlush(productsPrice);

        int databaseSizeBeforeUpdate = productsPriceRepository.findAll().size();

        // Update the productsPrice
        ProductsPrice updatedProductsPrice = productsPriceRepository.findById(productsPrice.getId()).get();
        // Disconnect from session so that the updates on updatedProductsPrice are not directly saved in db
        em.detach(updatedProductsPrice);
        updatedProductsPrice.productId(UPDATED_PRODUCT_ID).price(UPDATED_PRICE).applyDate(UPDATED_APPLY_DATE).createBy(UPDATED_CREATE_BY);
        ProductsPriceDTO productsPriceDTO = productsPriceMapper.toDto(updatedProductsPrice);

        restProductsPriceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productsPriceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsPriceDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeUpdate);
        ProductsPrice testProductsPrice = productsPriceList.get(productsPriceList.size() - 1);
        assertThat(testProductsPrice.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testProductsPrice.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProductsPrice.getApplyDate()).isEqualTo(UPDATED_APPLY_DATE);
        assertThat(testProductsPrice.getCreateBy()).isEqualTo(UPDATED_CREATE_BY);
    }

    @Test
    @Transactional
    void putNonExistingProductsPrice() throws Exception {
        int databaseSizeBeforeUpdate = productsPriceRepository.findAll().size();
        productsPrice.setId(count.incrementAndGet());

        // Create the ProductsPrice
        ProductsPriceDTO productsPriceDTO = productsPriceMapper.toDto(productsPrice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsPriceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productsPriceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsPriceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductsPrice() throws Exception {
        int databaseSizeBeforeUpdate = productsPriceRepository.findAll().size();
        productsPrice.setId(count.incrementAndGet());

        // Create the ProductsPrice
        ProductsPriceDTO productsPriceDTO = productsPriceMapper.toDto(productsPrice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsPriceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsPriceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductsPrice() throws Exception {
        int databaseSizeBeforeUpdate = productsPriceRepository.findAll().size();
        productsPrice.setId(count.incrementAndGet());

        // Create the ProductsPrice
        ProductsPriceDTO productsPriceDTO = productsPriceMapper.toDto(productsPrice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsPriceMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsPriceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductsPriceWithPatch() throws Exception {
        // Initialize the database
        productsPriceRepository.saveAndFlush(productsPrice);

        int databaseSizeBeforeUpdate = productsPriceRepository.findAll().size();

        // Update the productsPrice using partial update
        ProductsPrice partialUpdatedProductsPrice = new ProductsPrice();
        partialUpdatedProductsPrice.setId(productsPrice.getId());

        partialUpdatedProductsPrice
            .productId(UPDATED_PRODUCT_ID)
            .price(UPDATED_PRICE)
            .applyDate(UPDATED_APPLY_DATE)
            .createBy(UPDATED_CREATE_BY);

        restProductsPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductsPrice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductsPrice))
            )
            .andExpect(status().isOk());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeUpdate);
        ProductsPrice testProductsPrice = productsPriceList.get(productsPriceList.size() - 1);
        assertThat(testProductsPrice.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testProductsPrice.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testProductsPrice.getApplyDate()).isEqualTo(UPDATED_APPLY_DATE);
        assertThat(testProductsPrice.getCreateBy()).isEqualTo(UPDATED_CREATE_BY);
    }

    @Test
    @Transactional
    void fullUpdateProductsPriceWithPatch() throws Exception {
        // Initialize the database
        productsPriceRepository.saveAndFlush(productsPrice);

        int databaseSizeBeforeUpdate = productsPriceRepository.findAll().size();

        // Update the productsPrice using partial update
        ProductsPrice partialUpdatedProductsPrice = new ProductsPrice();
        partialUpdatedProductsPrice.setId(productsPrice.getId());

        partialUpdatedProductsPrice
            .productId(UPDATED_PRODUCT_ID)
            .price(UPDATED_PRICE)
            .applyDate(UPDATED_APPLY_DATE)
            .createBy(UPDATED_CREATE_BY);

        restProductsPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductsPrice.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductsPrice))
            )
            .andExpect(status().isOk());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeUpdate);
        ProductsPrice testProductsPrice = productsPriceList.get(productsPriceList.size() - 1);
        assertThat(testProductsPrice.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testProductsPrice.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testProductsPrice.getApplyDate()).isEqualTo(UPDATED_APPLY_DATE);
        assertThat(testProductsPrice.getCreateBy()).isEqualTo(UPDATED_CREATE_BY);
    }

    @Test
    @Transactional
    void patchNonExistingProductsPrice() throws Exception {
        int databaseSizeBeforeUpdate = productsPriceRepository.findAll().size();
        productsPrice.setId(count.incrementAndGet());

        // Create the ProductsPrice
        ProductsPriceDTO productsPriceDTO = productsPriceMapper.toDto(productsPrice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productsPriceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productsPriceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductsPrice() throws Exception {
        int databaseSizeBeforeUpdate = productsPriceRepository.findAll().size();
        productsPrice.setId(count.incrementAndGet());

        // Create the ProductsPrice
        ProductsPriceDTO productsPriceDTO = productsPriceMapper.toDto(productsPrice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productsPriceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductsPrice() throws Exception {
        int databaseSizeBeforeUpdate = productsPriceRepository.findAll().size();
        productsPrice.setId(count.incrementAndGet());

        // Create the ProductsPrice
        ProductsPriceDTO productsPriceDTO = productsPriceMapper.toDto(productsPrice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsPriceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productsPriceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductsPrice in the database
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductsPrice() throws Exception {
        // Initialize the database
        productsPriceRepository.saveAndFlush(productsPrice);

        int databaseSizeBeforeDelete = productsPriceRepository.findAll().size();

        // Delete the productsPrice
        restProductsPriceMockMvc
            .perform(delete(ENTITY_API_URL_ID, productsPrice.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductsPrice> productsPriceList = productsPriceRepository.findAll();
        assertThat(productsPriceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
