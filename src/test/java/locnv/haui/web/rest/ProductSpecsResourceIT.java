package locnv.haui.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import locnv.haui.IntegrationTest;
import locnv.haui.domain.ProductSpecs;
import locnv.haui.repository.ProductSpecsRepository;
import locnv.haui.service.dto.ProductSpecsDTO;
import locnv.haui.service.mapper.ProductSpecsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductSpecsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductSpecsResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final String ENTITY_API_URL = "/api/product-specs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductSpecsRepository productSpecsRepository;

    @Autowired
    private ProductSpecsMapper productSpecsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductSpecsMockMvc;

    private ProductSpecs productSpecs;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductSpecs createEntity(EntityManager em) {
        ProductSpecs productSpecs = new ProductSpecs().key(DEFAULT_KEY).value(DEFAULT_VALUE).productId(DEFAULT_PRODUCT_ID);
        return productSpecs;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductSpecs createUpdatedEntity(EntityManager em) {
        ProductSpecs productSpecs = new ProductSpecs().key(UPDATED_KEY).value(UPDATED_VALUE).productId(UPDATED_PRODUCT_ID);
        return productSpecs;
    }

    @BeforeEach
    public void initTest() {
        productSpecs = createEntity(em);
    }

    @Test
    @Transactional
    void createProductSpecs() throws Exception {
        int databaseSizeBeforeCreate = productSpecsRepository.findAll().size();
        // Create the ProductSpecs
        ProductSpecsDTO productSpecsDTO = productSpecsMapper.toDto(productSpecs);
        restProductSpecsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productSpecsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeCreate + 1);
        ProductSpecs testProductSpecs = productSpecsList.get(productSpecsList.size() - 1);
        assertThat(testProductSpecs.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testProductSpecs.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProductSpecs.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
    }

    @Test
    @Transactional
    void createProductSpecsWithExistingId() throws Exception {
        // Create the ProductSpecs with an existing ID
        productSpecs.setId(1L);
        ProductSpecsDTO productSpecsDTO = productSpecsMapper.toDto(productSpecs);

        int databaseSizeBeforeCreate = productSpecsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductSpecsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productSpecsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProductSpecs() throws Exception {
        // Initialize the database
        productSpecsRepository.saveAndFlush(productSpecs);

        // Get all the productSpecsList
        restProductSpecsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productSpecs.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())));
    }

    @Test
    @Transactional
    void getProductSpecs() throws Exception {
        // Initialize the database
        productSpecsRepository.saveAndFlush(productSpecs);

        // Get the productSpecs
        restProductSpecsMockMvc
            .perform(get(ENTITY_API_URL_ID, productSpecs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productSpecs.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingProductSpecs() throws Exception {
        // Get the productSpecs
        restProductSpecsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProductSpecs() throws Exception {
        // Initialize the database
        productSpecsRepository.saveAndFlush(productSpecs);

        int databaseSizeBeforeUpdate = productSpecsRepository.findAll().size();

        // Update the productSpecs
        ProductSpecs updatedProductSpecs = productSpecsRepository.findById(productSpecs.getId()).get();
        // Disconnect from session so that the updates on updatedProductSpecs are not directly saved in db
        em.detach(updatedProductSpecs);
        updatedProductSpecs.key(UPDATED_KEY).value(UPDATED_VALUE).productId(UPDATED_PRODUCT_ID);
        ProductSpecsDTO productSpecsDTO = productSpecsMapper.toDto(updatedProductSpecs);

        restProductSpecsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productSpecsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productSpecsDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeUpdate);
        ProductSpecs testProductSpecs = productSpecsList.get(productSpecsList.size() - 1);
        assertThat(testProductSpecs.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testProductSpecs.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProductSpecs.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    void putNonExistingProductSpecs() throws Exception {
        int databaseSizeBeforeUpdate = productSpecsRepository.findAll().size();
        productSpecs.setId(count.incrementAndGet());

        // Create the ProductSpecs
        ProductSpecsDTO productSpecsDTO = productSpecsMapper.toDto(productSpecs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductSpecsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productSpecsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productSpecsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductSpecs() throws Exception {
        int databaseSizeBeforeUpdate = productSpecsRepository.findAll().size();
        productSpecs.setId(count.incrementAndGet());

        // Create the ProductSpecs
        ProductSpecsDTO productSpecsDTO = productSpecsMapper.toDto(productSpecs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductSpecsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productSpecsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductSpecs() throws Exception {
        int databaseSizeBeforeUpdate = productSpecsRepository.findAll().size();
        productSpecs.setId(count.incrementAndGet());

        // Create the ProductSpecs
        ProductSpecsDTO productSpecsDTO = productSpecsMapper.toDto(productSpecs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductSpecsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productSpecsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductSpecsWithPatch() throws Exception {
        // Initialize the database
        productSpecsRepository.saveAndFlush(productSpecs);

        int databaseSizeBeforeUpdate = productSpecsRepository.findAll().size();

        // Update the productSpecs using partial update
        ProductSpecs partialUpdatedProductSpecs = new ProductSpecs();
        partialUpdatedProductSpecs.setId(productSpecs.getId());

        partialUpdatedProductSpecs.value(UPDATED_VALUE);

        restProductSpecsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductSpecs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductSpecs))
            )
            .andExpect(status().isOk());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeUpdate);
        ProductSpecs testProductSpecs = productSpecsList.get(productSpecsList.size() - 1);
        assertThat(testProductSpecs.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testProductSpecs.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProductSpecs.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
    }

    @Test
    @Transactional
    void fullUpdateProductSpecsWithPatch() throws Exception {
        // Initialize the database
        productSpecsRepository.saveAndFlush(productSpecs);

        int databaseSizeBeforeUpdate = productSpecsRepository.findAll().size();

        // Update the productSpecs using partial update
        ProductSpecs partialUpdatedProductSpecs = new ProductSpecs();
        partialUpdatedProductSpecs.setId(productSpecs.getId());

        partialUpdatedProductSpecs.key(UPDATED_KEY).value(UPDATED_VALUE).productId(UPDATED_PRODUCT_ID);

        restProductSpecsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductSpecs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductSpecs))
            )
            .andExpect(status().isOk());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeUpdate);
        ProductSpecs testProductSpecs = productSpecsList.get(productSpecsList.size() - 1);
        assertThat(testProductSpecs.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testProductSpecs.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProductSpecs.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
    }

    @Test
    @Transactional
    void patchNonExistingProductSpecs() throws Exception {
        int databaseSizeBeforeUpdate = productSpecsRepository.findAll().size();
        productSpecs.setId(count.incrementAndGet());

        // Create the ProductSpecs
        ProductSpecsDTO productSpecsDTO = productSpecsMapper.toDto(productSpecs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductSpecsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productSpecsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productSpecsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductSpecs() throws Exception {
        int databaseSizeBeforeUpdate = productSpecsRepository.findAll().size();
        productSpecs.setId(count.incrementAndGet());

        // Create the ProductSpecs
        ProductSpecsDTO productSpecsDTO = productSpecsMapper.toDto(productSpecs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductSpecsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productSpecsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductSpecs() throws Exception {
        int databaseSizeBeforeUpdate = productSpecsRepository.findAll().size();
        productSpecs.setId(count.incrementAndGet());

        // Create the ProductSpecs
        ProductSpecsDTO productSpecsDTO = productSpecsMapper.toDto(productSpecs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductSpecsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productSpecsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductSpecs in the database
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductSpecs() throws Exception {
        // Initialize the database
        productSpecsRepository.saveAndFlush(productSpecs);

        int databaseSizeBeforeDelete = productSpecsRepository.findAll().size();

        // Delete the productSpecs
        restProductSpecsMockMvc
            .perform(delete(ENTITY_API_URL_ID, productSpecs.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductSpecs> productSpecsList = productSpecsRepository.findAll();
        assertThat(productSpecsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
