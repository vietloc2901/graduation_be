package locnv.haui.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogsMapperTest {

    private CatalogsMapper catalogsMapper;

    @BeforeEach
    public void setUp() {
        catalogsMapper = new CatalogsMapperImpl();
    }
}
