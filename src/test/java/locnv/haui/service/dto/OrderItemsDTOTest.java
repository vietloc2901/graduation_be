package locnv.haui.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import locnv.haui.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderItemsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItemsDTO.class);
        OrderItemsDTO orderItemsDTO1 = new OrderItemsDTO();
        orderItemsDTO1.setId(1L);
        OrderItemsDTO orderItemsDTO2 = new OrderItemsDTO();
        assertThat(orderItemsDTO1).isNotEqualTo(orderItemsDTO2);
        orderItemsDTO2.setId(orderItemsDTO1.getId());
        assertThat(orderItemsDTO1).isEqualTo(orderItemsDTO2);
        orderItemsDTO2.setId(2L);
        assertThat(orderItemsDTO1).isNotEqualTo(orderItemsDTO2);
        orderItemsDTO1.setId(null);
        assertThat(orderItemsDTO1).isNotEqualTo(orderItemsDTO2);
    }
}
