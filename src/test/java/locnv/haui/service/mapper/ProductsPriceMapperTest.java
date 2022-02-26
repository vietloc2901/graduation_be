package locnv.haui.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductsPriceMapperTest {

    private ProductsPriceMapper productsPriceMapper;

    @BeforeEach
    public void setUp() {
        productsPriceMapper = new ProductsPriceMapperImpl();
    }
}
