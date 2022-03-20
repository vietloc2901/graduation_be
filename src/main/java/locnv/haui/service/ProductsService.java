package locnv.haui.service;

import java.util.List;
import java.util.Optional;

import locnv.haui.service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Interface for managing {@link locnv.haui.domain.Products}.
 */
public interface ProductsService {
    /**
     * Save a products.
     *
     * @param productsDTO the entity to save.
     * @return the persisted entity.
     */
    ProductsDTO save(ProductsDTO productsDTO);

    /**
     * Partially updates a products.
     *
     * @param productsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductsDTO> partialUpdate(ProductsDTO productsDTO);

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" products.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductsDTO> findOne(Long id);

    /**
     * Delete the "id" products.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    DataDTO<ProductFullDataDTO> search(ProductsDTO productsDTO, int page, int pageSize);

    DataDTO<ProductFullDataDTO> searchForViewPage(ProductsDTO productsDTO, int page, int pageSize);

    DataDTO<ProductFullDataDTO> searchForView(ProductsDTO productsDTO);

    ServiceResult  create(MultipartFile image, ProductsDTO productsDTO, List<ProductSpecsDTO> spec);

    ServiceResult  update(MultipartFile image, ProductsDTO productsDTO, List<ProductSpecsDTO> spec);

    ServiceResult searchProduct(Long id);

    DataDTO<ProductFullDataDTO> searchByCatalogNoChild();
}
