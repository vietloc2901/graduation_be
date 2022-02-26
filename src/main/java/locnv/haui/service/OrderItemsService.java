package locnv.haui.service;

import java.util.Optional;
import locnv.haui.service.dto.OrderItemsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link locnv.haui.domain.OrderItems}.
 */
public interface OrderItemsService {
    /**
     * Save a orderItems.
     *
     * @param orderItemsDTO the entity to save.
     * @return the persisted entity.
     */
    OrderItemsDTO save(OrderItemsDTO orderItemsDTO);

    /**
     * Partially updates a orderItems.
     *
     * @param orderItemsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OrderItemsDTO> partialUpdate(OrderItemsDTO orderItemsDTO);

    /**
     * Get all the orderItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrderItemsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" orderItems.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrderItemsDTO> findOne(Long id);

    /**
     * Delete the "id" orderItems.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
