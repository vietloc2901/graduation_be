package locnv.haui.service.impl;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import locnv.haui.domain.OrderItems;
import locnv.haui.domain.Orders;
import locnv.haui.domain.User;
import locnv.haui.repository.OrderItemsRepository;
import locnv.haui.repository.OrdersCustomRepository;
import locnv.haui.repository.OrdersRepository;
import locnv.haui.service.OrdersService;
import locnv.haui.service.UserService;
import locnv.haui.service.dto.DataDTO;
import locnv.haui.service.dto.OrderItemsDTO;
import locnv.haui.service.dto.OrdersDTO;
import locnv.haui.service.dto.ServiceResult;
import locnv.haui.service.mapper.OrderItemsMapper;
import locnv.haui.service.mapper.OrdersMapper;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Orders}.
 */
@Service
@Transactional
public class OrdersServiceImpl implements OrdersService {

    private final Logger log = LoggerFactory.getLogger(OrdersServiceImpl.class);

    private final OrdersRepository ordersRepository;

    private final OrdersMapper ordersMapper;

    private final UserService userService;

    private final OrderItemsRepository orderItemsRepository;

    private final OrderItemsMapper orderItemsMapper;

    private final OrdersCustomRepository ordersCustomRepository;

    public OrdersServiceImpl(OrdersRepository ordersRepository, OrdersMapper ordersMapper, UserService userService, OrderItemsRepository orderItemsRepository, OrderItemsMapper orderItemsMapper, OrdersCustomRepository ordersCustomRepository) {
        this.ordersRepository = ordersRepository;
        this.ordersMapper = ordersMapper;
        this.userService = userService;
        this.orderItemsRepository = orderItemsRepository;
        this.orderItemsMapper = orderItemsMapper;
        this.ordersCustomRepository = ordersCustomRepository;
    }

    @Override
    public OrdersDTO save(OrdersDTO ordersDTO) {
        log.debug("Request to save Orders : {}", ordersDTO);
        Orders orders = ordersMapper.toEntity(ordersDTO);
        orders = ordersRepository.save(orders);
        return ordersMapper.toDto(orders);
    }

    @Override
    public Optional<OrdersDTO> partialUpdate(OrdersDTO ordersDTO) {
        log.debug("Request to partially update Orders : {}", ordersDTO);

        return ordersRepository
            .findById(ordersDTO.getId())
            .map(existingOrders -> {
                ordersMapper.partialUpdate(existingOrders, ordersDTO);

                return existingOrders;
            })
            .map(ordersRepository::save)
            .map(ordersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdersDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return ordersRepository.findAll(pageable).map(ordersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdersDTO> findOne(Long id) {
        log.debug("Request to get Orders : {}", id);
        return ordersRepository.findById(id).map(ordersMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Orders : {}", id);
        ordersRepository.deleteById(id);
    }

    @Override
    public ServiceResult create(OrdersDTO ordersDTO) {
        Optional<User> login = userService.getUserWithAuthorities();
        Orders order = ordersMapper.toEntity(ordersDTO);
        if(login.isPresent()){
            order.setUserId(login.get().getId());
            order.setCreateBy(login.get().getFullName());
            order.setLastModifiedBy(login.get().getFullName());
            order.setName(login.get().getFullName());
            order.setPhone(login.get().getPhone());
        }else{
            order.setCreateBy("Khách không tài khoản");
            order.setLastModifiedBy("Khách không tài khoản");
        }
        order.setCreateDate(ZonedDateTime.now());
        order.setLastModifiedDate(ZonedDateTime.now());
        order.setStatus("WAITING");
        order = ordersRepository.save(order);
        for (OrderItemsDTO item : ordersDTO.getListItem()){
            OrderItems newO = orderItemsMapper.toEntity(item);
            newO.setOrderId(order.getId());
            newO.setCreateDate(ZonedDateTime.now());
            orderItemsRepository.save(newO);
        }

        return new ServiceResult(null, HttpStatus.OK, "Đặt hàng thành công");
    }

    @Override
    public DataDTO search(OrdersDTO ordersDTO, int page, int pageSize) {
        DataDTO dataDTO = new DataDTO();
        List<OrdersDTO> list = ordersCustomRepository.search(ordersDTO, page, pageSize);
        BigInteger total = ordersCustomRepository.totalRecord(ordersDTO);
        dataDTO.setData(list);
        dataDTO.setPage(page);
        dataDTO.setPageSize(pageSize);
        dataDTO.setTotal(total.intValue());
        return dataDTO;
    }

    @Override
    public OrdersDTO findById(OrdersDTO ordersDTO) {
        List<OrdersDTO> rsl = ordersCustomRepository.search(ordersDTO, 0, 1);
        if(rsl.isEmpty()){
            return null;
        }
        OrdersDTO rs = rsl.get(0);
        rs.setListItem(ordersCustomRepository.getByOrderId(ordersDTO.getId()));
        return rs;
    }

    @Override
    public ServiceResult<OrdersDTO> changeStatus(OrdersDTO ordersDTO) {
        if(Objects.isNull(ordersDTO.getId())){
            return new ServiceResult<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "Không có ID");
        }
        if(Objects.isNull(ordersDTO.getStatus())){
            return new ServiceResult<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "Không có Status");
        }
        Optional<Orders> o = ordersRepository.findById(ordersDTO.getId());
        if(o.isEmpty()){
            return new ServiceResult<>(null, HttpStatus.NOT_FOUND, "Không còn tồn tại");
        }
        Orders edit = o.get();
        edit.setStatus(ordersDTO.getStatus());
        edit.setNote(ordersDTO.getNote());
        Optional<User> login = userService.getUserWithAuthorities();
        if(login.isPresent()){
            edit.setLastModifiedBy(login.get().getLogin());
        }
        edit.setLastModifiedDate(ZonedDateTime.now());
        try{
            ordersRepository.save(edit);
        }catch (Exception e){
            return new ServiceResult<>(null, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new ServiceResult<>(ordersDTO, HttpStatus.OK, "Thành công");
    }
}
