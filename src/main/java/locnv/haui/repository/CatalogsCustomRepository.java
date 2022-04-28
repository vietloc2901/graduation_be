package locnv.haui.repository;

import locnv.haui.service.dto.CatalogsDTO;

import java.util.List;

public interface CatalogsCustomRepository {
    List<CatalogsDTO> getCatalogsForTree(CatalogsDTO catalogsDTO);

    List<CatalogsDTO> getDataExport(CatalogsDTO catalogsDTO);
}
