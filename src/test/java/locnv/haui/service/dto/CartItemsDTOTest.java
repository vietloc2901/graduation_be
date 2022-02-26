package locnv.haui.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import locnv.haui.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CartItemsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartItemsDTO.class);
        CartItemsDTO cartItemsDTO1 = new CartItemsDTO();
        cartItemsDTO1.setId(1L);
        CartItemsDTO cartItemsDTO2 = new CartItemsDTO();
        assertThat(cartItemsDTO1).isNotEqualTo(cartItemsDTO2);
        cartItemsDTO2.setId(cartItemsDTO1.getId());
        assertThat(cartItemsDTO1).isEqualTo(cartItemsDTO2);
        cartItemsDTO2.setId(2L);
        assertThat(cartItemsDTO1).isNotEqualTo(cartItemsDTO2);
        cartItemsDTO1.setId(null);
        assertThat(cartItemsDTO1).isNotEqualTo(cartItemsDTO2);
    }
}
