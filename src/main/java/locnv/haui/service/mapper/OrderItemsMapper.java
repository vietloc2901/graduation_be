package locnv.haui.service.mapper;

import locnv.haui.domain.OrderItems;
import locnv.haui.service.dto.OrderItemsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItems} and its DTO {@link OrderItemsDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface OrderItemsMapper extends EntityMapper<OrderItemsDTO, OrderItems> {}
