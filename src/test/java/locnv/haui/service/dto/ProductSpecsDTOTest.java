package locnv.haui.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import locnv.haui.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductSpecsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductSpecsDTO.class);
        ProductSpecsDTO productSpecsDTO1 = new ProductSpecsDTO();
        productSpecsDTO1.setId(1L);
        ProductSpecsDTO productSpecsDTO2 = new ProductSpecsDTO();
        assertThat(productSpecsDTO1).isNotEqualTo(productSpecsDTO2);
        productSpecsDTO2.setId(productSpecsDTO1.getId());
        assertThat(productSpecsDTO1).isEqualTo(productSpecsDTO2);
        productSpecsDTO2.setId(2L);
        assertThat(productSpecsDTO1).isNotEqualTo(productSpecsDTO2);
        productSpecsDTO1.setId(null);
        assertThat(productSpecsDTO1).isNotEqualTo(productSpecsDTO2);
    }
}
