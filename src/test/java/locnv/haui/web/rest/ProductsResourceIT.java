package locnv.haui.web.rest;

import static locnv.haui.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import locnv.haui.IntegrationTest;
import locnv.haui.domain.Products;
import locnv.haui.repository.ProductsRepository;
import locnv.haui.service.dto.ProductsDTO;
import locnv.haui.service.mapper.ProductsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductsResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CREATE_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATE_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final String DEFAULT_DETAIL_IMAGES = "AAAAAAAAAA";
    private static final String UPDATED_DETAIL_IMAGES = "BBBBBBBBBB";

    private static final String DEFAULT_BRAND = "AAAAAAAAAA";
    private static final String UPDATED_BRAND = "BBBBBBBBBB";

    private static final String DEFAULT_PRODUCT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_DETAILS = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_DOCUMENT = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_DOCUMENT = "BBBBBBBBBB";

    private static final String DEFAULT_VIDEO = "AAAAAAAAAA";
    private static final String UPDATED_VIDEO = "BBBBBBBBBB";

    private static final Long DEFAULT_CATALOG_ID = 1L;
    private static final Long UPDATED_CATALOG_ID = 2L;

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductsMapper productsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductsMockMvc;

    private Products products;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createEntity(EntityManager em) {
        Products products = new Products()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .createDate(DEFAULT_CREATE_DATE)
            .createBy(DEFAULT_CREATE_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .image(DEFAULT_IMAGE)
            .brand(DEFAULT_BRAND)
            .productDetails(DEFAULT_PRODUCT_DETAILS)
            .descriptionDocument(DEFAULT_DESCRIPTION_DOCUMENT)
            .video(DEFAULT_VIDEO)
            .catalogId(DEFAULT_CATALOG_ID);
        return products;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createUpdatedEntity(EntityManager em) {
        Products products = new Products()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .createDate(UPDATED_CREATE_DATE)
            .createBy(UPDATED_CREATE_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .image(UPDATED_IMAGE)
            .brand(UPDATED_BRAND)
            .productDetails(UPDATED_PRODUCT_DETAILS)
            .descriptionDocument(UPDATED_DESCRIPTION_DOCUMENT)
            .video(UPDATED_VIDEO)
            .catalogId(UPDATED_CATALOG_ID);
        return products;
    }

    @BeforeEach
    public void initTest() {
        products = createEntity(em);
    }

    @Test
    @Transactional
    void createProducts() throws Exception {
        int databaseSizeBeforeCreate = productsRepository.findAll().size();
        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isCreated());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate + 1);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testProducts.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProducts.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testProducts.getCreateBy()).isEqualTo(DEFAULT_CREATE_BY);
        assertThat(testProducts.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testProducts.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testProducts.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testProducts.getBrand()).isEqualTo(DEFAULT_BRAND);
        assertThat(testProducts.getProductDetails()).isEqualTo(DEFAULT_PRODUCT_DETAILS);
        assertThat(testProducts.getDescriptionDocument()).isEqualTo(DEFAULT_DESCRIPTION_DOCUMENT);
        assertThat(testProducts.getVideo()).isEqualTo(DEFAULT_VIDEO);
        assertThat(testProducts.getCatalogId()).isEqualTo(DEFAULT_CATALOG_ID);
    }

    @Test
    @Transactional
    void createProductsWithExistingId() throws Exception {
        // Create the Products with an existing ID
        products.setId(1L);
        ProductsDTO productsDTO = productsMapper.toDto(products);

        int databaseSizeBeforeCreate = productsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(sameInstant(DEFAULT_CREATE_DATE))))
            .andExpect(jsonPath("$.[*].createBy").value(hasItem(DEFAULT_CREATE_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.[*].detailImages").value(hasItem(DEFAULT_DETAIL_IMAGES)))
            .andExpect(jsonPath("$.[*].brand").value(hasItem(DEFAULT_BRAND)))
            .andExpect(jsonPath("$.[*].productDetails").value(hasItem(DEFAULT_PRODUCT_DETAILS)))
            .andExpect(jsonPath("$.[*].descriptionDocument").value(hasItem(DEFAULT_DESCRIPTION_DOCUMENT)))
            .andExpect(jsonPath("$.[*].video").value(hasItem(DEFAULT_VIDEO)))
            .andExpect(jsonPath("$.[*].catalogId").value(hasItem(DEFAULT_CATALOG_ID)));
    }

    @Test
    @Transactional
    void getProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get the products
        restProductsMockMvc
            .perform(get(ENTITY_API_URL_ID, products.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(products.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createDate").value(sameInstant(DEFAULT_CREATE_DATE)))
            .andExpect(jsonPath("$.createBy").value(DEFAULT_CREATE_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(sameInstant(DEFAULT_LAST_MODIFIED_DATE)))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE))
            .andExpect(jsonPath("$.detailImages").value(DEFAULT_DETAIL_IMAGES))
            .andExpect(jsonPath("$.brand").value(DEFAULT_BRAND))
            .andExpect(jsonPath("$.productDetails").value(DEFAULT_PRODUCT_DETAILS))
            .andExpect(jsonPath("$.descriptionDocument").value(DEFAULT_DESCRIPTION_DOCUMENT))
            .andExpect(jsonPath("$.video").value(DEFAULT_VIDEO))
            .andExpect(jsonPath("$.catalogId").value(DEFAULT_CATALOG_ID));
    }

    @Test
    @Transactional
    void getNonExistingProducts() throws Exception {
        // Get the products
        restProductsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products
        Products updatedProducts = productsRepository.findById(products.getId()).get();
        // Disconnect from session so that the updates on updatedProducts are not directly saved in db
        em.detach(updatedProducts);
        updatedProducts
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .createDate(UPDATED_CREATE_DATE)
            .createBy(UPDATED_CREATE_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .image(UPDATED_IMAGE)
            .brand(UPDATED_BRAND)
            .productDetails(UPDATED_PRODUCT_DETAILS)
            .descriptionDocument(UPDATED_DESCRIPTION_DOCUMENT)
            .video(UPDATED_VIDEO)
            .catalogId(UPDATED_CATALOG_ID);
        ProductsDTO productsDTO = productsMapper.toDto(updatedProducts);

        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testProducts.getCreateBy()).isEqualTo(UPDATED_CREATE_BY);
        assertThat(testProducts.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testProducts.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testProducts.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testProducts.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testProducts.getProductDetails()).isEqualTo(UPDATED_PRODUCT_DETAILS);
        assertThat(testProducts.getDescriptionDocument()).isEqualTo(UPDATED_DESCRIPTION_DOCUMENT);
        assertThat(testProducts.getVideo()).isEqualTo(UPDATED_VIDEO);
        assertThat(testProducts.getCatalogId()).isEqualTo(UPDATED_CATALOG_ID);
    }

    @Test
    @Transactional
    void putNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts
            .code(UPDATED_CODE)
            .createDate(UPDATED_CREATE_DATE)
            .brand(UPDATED_BRAND)
            .productDetails(UPDATED_PRODUCT_DETAILS)
            .video(UPDATED_VIDEO)
            .catalogId(UPDATED_CATALOG_ID);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testProducts.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProducts.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testProducts.getCreateBy()).isEqualTo(DEFAULT_CREATE_BY);
        assertThat(testProducts.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testProducts.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testProducts.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testProducts.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testProducts.getProductDetails()).isEqualTo(UPDATED_PRODUCT_DETAILS);
        assertThat(testProducts.getDescriptionDocument()).isEqualTo(DEFAULT_DESCRIPTION_DOCUMENT);
        assertThat(testProducts.getVideo()).isEqualTo(UPDATED_VIDEO);
        assertThat(testProducts.getCatalogId()).isEqualTo(UPDATED_CATALOG_ID);
    }

    @Test
    @Transactional
    void fullUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .createDate(UPDATED_CREATE_DATE)
            .createBy(UPDATED_CREATE_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .image(UPDATED_IMAGE)
            .brand(UPDATED_BRAND)
            .productDetails(UPDATED_PRODUCT_DETAILS)
            .descriptionDocument(UPDATED_DESCRIPTION_DOCUMENT)
            .video(UPDATED_VIDEO)
            .catalogId(UPDATED_CATALOG_ID);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testProducts.getCreateBy()).isEqualTo(UPDATED_CREATE_BY);
        assertThat(testProducts.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testProducts.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testProducts.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testProducts.getBrand()).isEqualTo(UPDATED_BRAND);
        assertThat(testProducts.getProductDetails()).isEqualTo(UPDATED_PRODUCT_DETAILS);
        assertThat(testProducts.getDescriptionDocument()).isEqualTo(UPDATED_DESCRIPTION_DOCUMENT);
        assertThat(testProducts.getVideo()).isEqualTo(UPDATED_VIDEO);
        assertThat(testProducts.getCatalogId()).isEqualTo(UPDATED_CATALOG_ID);
    }

    @Test
    @Transactional
    void patchNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeDelete = productsRepository.findAll().size();

        // Delete the products
        restProductsMockMvc
            .perform(delete(ENTITY_API_URL_ID, products.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
