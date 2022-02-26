package locnv.haui.service.mapper;

import locnv.haui.domain.CartItems;
import locnv.haui.service.dto.CartItemsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartItems} and its DTO {@link CartItemsDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CartItemsMapper extends EntityMapper<CartItemsDTO, CartItems> {}
