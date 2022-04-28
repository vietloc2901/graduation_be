package locnv.haui.service.impl;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import locnv.haui.domain.Cart;
import locnv.haui.domain.CartItems;
import locnv.haui.domain.User;
import locnv.haui.repository.CartItemsRepository;
import locnv.haui.repository.CartRepository;
import locnv.haui.service.CartItemsService;
import locnv.haui.service.UserService;
import locnv.haui.service.dto.CartItemsDTO;
import locnv.haui.service.dto.ServiceResult;
import locnv.haui.service.mapper.CartItemsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CartItems}.
 */
@Service
@Transactional
public class CartItemsServiceImpl implements CartItemsService {

    private final Logger log = LoggerFactory.getLogger(CartItemsServiceImpl.class);

    private final CartItemsRepository cartItemsRepository;

    private final CartItemsMapper cartItemsMapper;

    private final UserService userService;

    private final CartRepository cartRepository;

    public CartItemsServiceImpl(CartItemsRepository cartItemsRepository, CartItemsMapper cartItemsMapper, UserService userService, CartRepository cartRepository) {
        this.cartItemsRepository = cartItemsRepository;
        this.cartItemsMapper = cartItemsMapper;
        this.userService = userService;
        this.cartRepository = cartRepository;
    }

    @Override
    public CartItemsDTO save(CartItemsDTO cartItemsDTO) {
        log.debug("Request to save CartItems : {}", cartItemsDTO);
        CartItems cartItems = cartItemsMapper.toEntity(cartItemsDTO);
        cartItems = cartItemsRepository.save(cartItems);
        return cartItemsMapper.toDto(cartItems);
    }

    @Override
    public Optional<CartItemsDTO> partialUpdate(CartItemsDTO cartItemsDTO) {
        log.debug("Request to partially update CartItems : {}", cartItemsDTO);

        return cartItemsRepository
            .findById(cartItemsDTO.getId())
            .map(existingCartItems -> {
                cartItemsMapper.partialUpdate(existingCartItems, cartItemsDTO);

                return existingCartItems;
            })
            .map(cartItemsRepository::save)
            .map(cartItemsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartItemsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CartItems");
        return cartItemsRepository.findAll(pageable).map(cartItemsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CartItemsDTO> findOne(Long id) {
        log.debug("Request to get CartItems : {}", id);
        return cartItemsRepository.findById(id).map(cartItemsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CartItems : {}", id);
        cartItemsRepository.deleteById(id);
    }

    @Override
    public ServiceResult create(CartItemsDTO cartItemsDTO) {
        ServiceResult rs = new ServiceResult();
        Optional<User> login = userService.getUserWithAuthorities();
        if(login.isEmpty()){
            return new ServiceResult(null, HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }
        Cart cart = cartRepository.findByUserIdAndStatus(login.get().getId(), true);
        if(Objects.isNull(cart)){
            cart = new Cart();
            cart.setCreateDate(ZonedDateTime.now());
            cart.setUserId(login.get().getId());
            cart.setStatus(true);
            cart = cartRepository.save(cart);
        }
        CartItems save = cartItemsMapper.toEntity(cartItemsDTO);
        save.setCartId(cart.getId());
        cartItemsRepository.save(save);
        return new ServiceResult();
    }
}
