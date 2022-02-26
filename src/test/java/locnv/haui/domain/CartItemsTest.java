package locnv.haui.domain;

import static org.assertj.core.api.Assertions.assertThat;

import locnv.haui.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartItemsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartItems.class);
        CartItems cartItems1 = new CartItems();
        cartItems1.setId(1L);
        CartItems cartItems2 = new CartItems();
        cartItems2.setId(cartItems1.getId());
        assertThat(cartItems1).isEqualTo(cartItems2);
        cartItems2.setId(2L);
        assertThat(cartItems1).isNotEqualTo(cartItems2);
        cartItems1.setId(null);
        assertThat(cartItems1).isNotEqualTo(cartItems2);
    }
}
