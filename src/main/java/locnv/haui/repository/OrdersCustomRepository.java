package locnv.haui.repository;

import locnv.haui.service.dto.OrderItemsDTO;
import locnv.haui.service.dto.OrdersDTO;

import java.math.BigInteger;
import java.util.List;

public interface OrdersCustomRepository {
    List<OrdersDTO> search(OrdersDTO ordersDTO, int page, int pageSize);
    BigInteger totalRecord(OrdersDTO ordersDTO);
    List<OrderItemsDTO> getByOrderId(Long id);
    List<OrdersDTO> searchWithAuthority(OrdersDTO ordersDTO, int page, int pageSize);
    BigInteger totalRecordWithAuthority(OrdersDTO ordersDTO);
    List<OrdersDTO> getDataExport(OrdersDTO ordersDTO);
}
