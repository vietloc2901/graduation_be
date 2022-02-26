package locnv.haui.web.rest;

import static locnv.haui.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import locnv.haui.IntegrationTest;
import locnv.haui.domain.CartItems;
import locnv.haui.repository.CartItemsRepository;
import locnv.haui.service.dto.CartItemsDTO;
import locnv.haui.service.mapper.CartItemsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CartItemsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CartItemsResourceIT {

    private static final Long DEFAULT_CART_ID = 1L;
    private static final Long UPDATED_CART_ID = 2L;

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/cart-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CartItemsRepository cartItemsRepository;

    @Autowired
    private CartItemsMapper cartItemsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartItemsMockMvc;

    private CartItems cartItems;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItems createEntity(EntityManager em) {
        CartItems cartItems = new CartItems()
            .cartId(DEFAULT_CART_ID)
            .productId(DEFAULT_PRODUCT_ID)
            .quantity(DEFAULT_QUANTITY)
            .price(DEFAULT_PRICE);
        return cartItems;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItems createUpdatedEntity(EntityManager em) {
        CartItems cartItems = new CartItems()
            .cartId(UPDATED_CART_ID)
            .productId(UPDATED_PRODUCT_ID)
            .quantity(UPDATED_QUANTITY)
            .price(UPDATED_PRICE);
        return cartItems;
    }

    @BeforeEach
    public void initTest() {
        cartItems = createEntity(em);
    }

    @Test
    @Transactional
    void createCartItems() throws Exception {
        int databaseSizeBeforeCreate = cartItemsRepository.findAll().size();
        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);
        restCartItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemsDTO)))
            .andExpect(status().isCreated());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeCreate + 1);
        CartItems testCartItems = cartItemsList.get(cartItemsList.size() - 1);
        assertThat(testCartItems.getCartId()).isEqualTo(DEFAULT_CART_ID);
        assertThat(testCartItems.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testCartItems.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testCartItems.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
    }

    @Test
    @Transactional
    void createCartItemsWithExistingId() throws Exception {
        // Create the CartItems with an existing ID
        cartItems.setId(1L);
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        int databaseSizeBeforeCreate = cartItemsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartItemsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        // Get all the cartItemsList
        restCartItemsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartItems.getId().intValue())))
            .andExpect(jsonPath("$.[*].cartId").value(hasItem(DEFAULT_CART_ID.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))));
    }

    @Test
    @Transactional
    void getCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        // Get the cartItems
        restCartItemsMockMvc
            .perform(get(ENTITY_API_URL_ID, cartItems.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cartItems.getId().intValue()))
            .andExpect(jsonPath("$.cartId").value(DEFAULT_CART_ID.intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)));
    }

    @Test
    @Transactional
    void getNonExistingCartItems() throws Exception {
        // Get the cartItems
        restCartItemsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();

        // Update the cartItems
        CartItems updatedCartItems = cartItemsRepository.findById(cartItems.getId()).get();
        // Disconnect from session so that the updates on updatedCartItems are not directly saved in db
        em.detach(updatedCartItems);
        updatedCartItems.cartId(UPDATED_CART_ID).productId(UPDATED_PRODUCT_ID).quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(updatedCartItems);

        restCartItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isOk());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
        CartItems testCartItems = cartItemsList.get(cartItemsList.size() - 1);
        assertThat(testCartItems.getCartId()).isEqualTo(UPDATED_CART_ID);
        assertThat(testCartItems.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testCartItems.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testCartItems.getPrice()).isEqualTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void putNonExistingCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setId(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setId(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setId(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cartItemsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCartItemsWithPatch() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();

        // Update the cartItems using partial update
        CartItems partialUpdatedCartItems = new CartItems();
        partialUpdatedCartItems.setId(cartItems.getId());

        partialUpdatedCartItems.cartId(UPDATED_CART_ID).price(UPDATED_PRICE);

        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCartItems))
            )
            .andExpect(status().isOk());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
        CartItems testCartItems = cartItemsList.get(cartItemsList.size() - 1);
        assertThat(testCartItems.getCartId()).isEqualTo(UPDATED_CART_ID);
        assertThat(testCartItems.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testCartItems.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testCartItems.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void fullUpdateCartItemsWithPatch() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();

        // Update the cartItems using partial update
        CartItems partialUpdatedCartItems = new CartItems();
        partialUpdatedCartItems.setId(cartItems.getId());

        partialUpdatedCartItems.cartId(UPDATED_CART_ID).productId(UPDATED_PRODUCT_ID).quantity(UPDATED_QUANTITY).price(UPDATED_PRICE);

        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItems.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCartItems))
            )
            .andExpect(status().isOk());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
        CartItems testCartItems = cartItemsList.get(cartItemsList.size() - 1);
        assertThat(testCartItems.getCartId()).isEqualTo(UPDATED_CART_ID);
        assertThat(testCartItems.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testCartItems.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testCartItems.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
    }

    @Test
    @Transactional
    void patchNonExistingCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setId(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cartItemsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setId(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCartItems() throws Exception {
        int databaseSizeBeforeUpdate = cartItemsRepository.findAll().size();
        cartItems.setId(count.incrementAndGet());

        // Create the CartItems
        CartItemsDTO cartItemsDTO = cartItemsMapper.toDto(cartItems);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cartItemsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItems in the database
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCartItems() throws Exception {
        // Initialize the database
        cartItemsRepository.saveAndFlush(cartItems);

        int databaseSizeBeforeDelete = cartItemsRepository.findAll().size();

        // Delete the cartItems
        restCartItemsMockMvc
            .perform(delete(ENTITY_API_URL_ID, cartItems.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CartItems> cartItemsList = cartItemsRepository.findAll();
        assertThat(cartItemsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
