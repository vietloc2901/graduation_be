package locnv.haui.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import locnv.haui.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductsPriceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductsPriceDTO.class);
        ProductsPriceDTO productsPriceDTO1 = new ProductsPriceDTO();
        productsPriceDTO1.setId(1L);
        ProductsPriceDTO productsPriceDTO2 = new ProductsPriceDTO();
        assertThat(productsPriceDTO1).isNotEqualTo(productsPriceDTO2);
        productsPriceDTO2.setId(productsPriceDTO1.getId());
        assertThat(productsPriceDTO1).isEqualTo(productsPriceDTO2);
        productsPriceDTO2.setId(2L);
        assertThat(productsPriceDTO1).isNotEqualTo(productsPriceDTO2);
        productsPriceDTO1.setId(null);
        assertThat(productsPriceDTO1).isNotEqualTo(productsPriceDTO2);
    }
}
