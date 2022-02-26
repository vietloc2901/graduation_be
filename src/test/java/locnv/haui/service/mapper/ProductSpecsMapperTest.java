package locnv.haui.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductSpecsMapperTest {

    private ProductSpecsMapper productSpecsMapper;

    @BeforeEach
    public void setUp() {
        productSpecsMapper = new ProductSpecsMapperImpl();
    }
}
