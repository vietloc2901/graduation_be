package locnv.haui.service.impl;

import java.util.Optional;
import locnv.haui.domain.OrderItems;
import locnv.haui.repository.OrderItemsRepository;
import locnv.haui.service.OrderItemsService;
import locnv.haui.service.dto.OrderItemsDTO;
import locnv.haui.service.mapper.OrderItemsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OrderItems}.
 */
@Service
@Transactional
public class OrderItemsServiceImpl implements OrderItemsService {

    private final Logger log = LoggerFactory.getLogger(OrderItemsServiceImpl.class);

    private final OrderItemsRepository orderItemsRepository;

    private final OrderItemsMapper orderItemsMapper;

    public OrderItemsServiceImpl(OrderItemsRepository orderItemsRepository, OrderItemsMapper orderItemsMapper) {
        this.orderItemsRepository = orderItemsRepository;
        this.orderItemsMapper = orderItemsMapper;
    }

    @Override
    public OrderItemsDTO save(OrderItemsDTO orderItemsDTO) {
        log.debug("Request to save OrderItems : {}", orderItemsDTO);
        OrderItems orderItems = orderItemsMapper.toEntity(orderItemsDTO);
        orderItems = orderItemsRepository.save(orderItems);
        return orderItemsMapper.toDto(orderItems);
    }

    @Override
    public Optional<OrderItemsDTO> partialUpdate(OrderItemsDTO orderItemsDTO) {
        log.debug("Request to partially update OrderItems : {}", orderItemsDTO);

        return orderItemsRepository
            .findById(orderItemsDTO.getId())
            .map(existingOrderItems -> {
                orderItemsMapper.partialUpdate(existingOrderItems, orderItemsDTO);

                return existingOrderItems;
            })
            .map(orderItemsRepository::save)
            .map(orderItemsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderItems");
        return orderItemsRepository.findAll(pageable).map(orderItemsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItemsDTO> findOne(Long id) {
        log.debug("Request to get OrderItems : {}", id);
        return orderItemsRepository.findById(id).map(orderItemsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete OrderItems : {}", id);
        orderItemsRepository.deleteById(id);
    }
}
