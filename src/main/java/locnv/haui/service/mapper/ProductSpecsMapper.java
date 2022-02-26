package locnv.haui.service.mapper;

import locnv.haui.domain.ProductSpecs;
import locnv.haui.service.dto.ProductSpecsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductSpecs} and its DTO {@link ProductSpecsDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProductSpecsMapper extends EntityMapper<ProductSpecsDTO, ProductSpecs> {}
