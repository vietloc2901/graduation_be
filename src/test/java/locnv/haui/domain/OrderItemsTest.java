package locnv.haui.domain;

import static org.assertj.core.api.Assertions.assertThat;

import locnv.haui.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItems.class);
        OrderItems orderItems1 = new OrderItems();
        orderItems1.setId(1L);
        OrderItems orderItems2 = new OrderItems();
        orderItems2.setId(orderItems1.getId());
        assertThat(orderItems1).isEqualTo(orderItems2);
        orderItems2.setId(2L);
        assertThat(orderItems1).isNotEqualTo(orderItems2);
        orderItems1.setId(null);
        assertThat(orderItems1).isNotEqualTo(orderItems2);
    }
}
