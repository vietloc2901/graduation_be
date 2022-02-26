package locnv.haui.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderItemsMapperTest {

    private OrderItemsMapper orderItemsMapper;

    @BeforeEach
    public void setUp() {
        orderItemsMapper = new OrderItemsMapperImpl();
    }
}
