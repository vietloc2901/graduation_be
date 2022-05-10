package locnv.haui.service;

import java.util.List;
import java.util.Optional;

import locnv.haui.service.dto.DataDTO;
import locnv.haui.service.dto.OrdersDTO;
import locnv.haui.service.dto.ServiceResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link locnv.haui.domain.Orders}.
 */
public interface OrdersService {
    /**
     * Save a orders.
     *
     * @param ordersDTO the entity to save.
     * @return the persisted entity.
     */
    OrdersDTO save(OrdersDTO ordersDTO);

    /**
     * Partially updates a orders.
     *
     * @param ordersDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OrdersDTO> partialUpdate(OrdersDTO ordersDTO);

    /**
     * Get all the orders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrdersDTO> findAll(Pageable pageable);

    /**
     * Get the "id" orders.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrdersDTO> findOne(Long id);

    /**
     * Delete the "id" orders.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    ServiceResult create(OrdersDTO ordersDTO);

    DataDTO search(OrdersDTO ordersDTO, int page, int pageSize);

    OrdersDTO findById(OrdersDTO ordersDTO);

    ServiceResult<OrdersDTO> changeStatus(OrdersDTO ordersDTO);

    DataDTO getWithAuthority(OrdersDTO ordersDTO, int page, int pageSize);

    ServiceResult cancelOrder(OrdersDTO ordersDTO);

    List<OrdersDTO> getDataExport(OrdersDTO ordersDTO);
}
