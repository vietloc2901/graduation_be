package locnv.haui.service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import locnv.haui.service.dto.CatalogsDTO;
import locnv.haui.service.dto.ServiceResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Interface for managing {@link locnv.haui.domain.Catalogs}.
 */
public interface CatalogsService {
    /**
     * Save a catalogs.
     *
     * @param catalogsDTO the entity to save.
     * @return the persisted entity.
     */
    CatalogsDTO save(CatalogsDTO catalogsDTO);

    ServiceResult<CatalogsDTO> create(CatalogsDTO catalogsDTO);

    /**
     * Partially updates a catalogs.
     *
     * @param catalogsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CatalogsDTO> partialUpdate(CatalogsDTO catalogsDTO);

    /**
     * Get all the catalogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CatalogsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" catalogs.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CatalogsDTO> findOne(Long id);

    /**
     * Delete the "id" catalogs.
     *
     * @param id the id of the entity.
     */
//    void delete(Long id);

    ServiceResult<?> delete(Long id);

    List<CatalogsDTO> getCatalogsForTree(CatalogsDTO catalogsDTO);

    List<CatalogsDTO> getDataExport(CatalogsDTO catalogsDTO);

    ServiceResult<CatalogsDTO> checkExist(String code);

    ServiceResult<CatalogsDTO> update(CatalogsDTO catalogsDTO);

    byte[] getSampleFile() throws Exception;

    List<CatalogsDTO> importFile(MultipartFile file, String fileInputPath, Integer isAddNew) throws GeneralSecurityException;

    ByteArrayInputStream downloadExcelError(CatalogsDTO catalogsDTO) throws GeneralSecurityException, IOException;
}
