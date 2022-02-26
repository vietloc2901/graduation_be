package locnv.haui.domain;

import static org.assertj.core.api.Assertions.assertThat;

import locnv.haui.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductSpecsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductSpecs.class);
        ProductSpecs productSpecs1 = new ProductSpecs();
        productSpecs1.setId(1L);
        ProductSpecs productSpecs2 = new ProductSpecs();
        productSpecs2.setId(productSpecs1.getId());
        assertThat(productSpecs1).isEqualTo(productSpecs2);
        productSpecs2.setId(2L);
        assertThat(productSpecs1).isNotEqualTo(productSpecs2);
        productSpecs1.setId(null);
        assertThat(productSpecs1).isNotEqualTo(productSpecs2);
    }
}
