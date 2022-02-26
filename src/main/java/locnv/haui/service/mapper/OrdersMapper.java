package locnv.haui.service.mapper;

import locnv.haui.domain.Orders;
import locnv.haui.service.dto.OrdersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Orders} and its DTO {@link OrdersDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface OrdersMapper extends EntityMapper<OrdersDTO, Orders> {}
