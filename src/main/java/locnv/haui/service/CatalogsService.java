package locnv.haui.service;

import java.util.Optional;
import locnv.haui.service.dto.CatalogsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    void delete(Long id);
}
