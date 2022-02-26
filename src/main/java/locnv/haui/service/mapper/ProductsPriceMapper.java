package locnv.haui.service.mapper;

import locnv.haui.domain.ProductsPrice;
import locnv.haui.service.dto.ProductsPriceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductsPrice} and its DTO {@link ProductsPriceDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductsPriceMapper extends EntityMapper<ProductsPriceDTO, ProductsPrice> {}
