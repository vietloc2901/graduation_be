package locnv.haui.service.mapper;

import locnv.haui.domain.Catalogs;
import locnv.haui.service.dto.CatalogsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Catalogs} and its DTO {@link CatalogsDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CatalogsMapper extends EntityMapper<CatalogsDTO, Catalogs> {}
