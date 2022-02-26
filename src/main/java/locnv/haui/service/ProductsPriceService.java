package locnv.haui.service;

import java.util.Optional;
import locnv.haui.service.dto.ProductsPriceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link locnv.haui.domain.ProductsPrice}.
 */
public interface ProductsPriceService {
    /**
     * Save a productsPrice.
     *
     * @param productsPriceDTO the entity to save.
     * @return the persisted entity.
     */
    ProductsPriceDTO save(ProductsPriceDTO productsPriceDTO);

    /**
     * Partially updates a productsPrice.
     *
     * @param productsPriceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ProductsPriceDTO> partialUpdate(ProductsPriceDTO productsPriceDTO);

    /**
     * Get all the productsPrices.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ProductsPriceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" productsPrice.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ProductsPriceDTO> findOne(Long id);

    /**
     * Delete the "id" productsPrice.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
