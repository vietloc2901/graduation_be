package locnv.haui.service.mapper;

import locnv.haui.domain.Products;
import locnv.haui.service.dto.ProductsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Products} and its DTO {@link ProductsDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductsMapper extends EntityMapper<ProductsDTO, Products> {}
