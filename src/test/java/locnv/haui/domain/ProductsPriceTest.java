package locnv.haui.domain;

import static org.assertj.core.api.Assertions.assertThat;

import locnv.haui.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductsPriceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductsPrice.class);
        ProductsPrice productsPrice1 = new ProductsPrice();
        productsPrice1.setId(1L);
        ProductsPrice productsPrice2 = new ProductsPrice();
        productsPrice2.setId(productsPrice1.getId());
        assertThat(productsPrice1).isEqualTo(productsPrice2);
        productsPrice2.setId(2L);
        assertThat(productsPrice1).isNotEqualTo(productsPrice2);
        productsPrice1.setId(null);
        assertThat(productsPrice1).isNotEqualTo(productsPrice2);
    }
}
