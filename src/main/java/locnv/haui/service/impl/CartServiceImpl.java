package locnv.haui.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import locnv.haui.domain.Cart;
import locnv.haui.domain.CartItems;
import locnv.haui.domain.User;
import locnv.haui.repository.CartItemsRepository;
import locnv.haui.repository.CartRepository;
import locnv.haui.repository.ProductsCustomRepository;
import locnv.haui.service.CartService;
import locnv.haui.service.UserService;
import locnv.haui.service.dto.*;
import locnv.haui.service.mapper.CartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Cart}.
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;

    private final CartMapper cartMapper;

    private final UserService userService;

    private final CartItemsRepository cartItemsRepository;

    private final ProductsCustomRepository productsCustomRepository;

    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper, UserService userService, CartItemsRepository cartItemsRepository, ProductsCustomRepository productsCustomRepository) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.userService = userService;
        this.cartItemsRepository = cartItemsRepository;
        this.productsCustomRepository = productsCustomRepository;
    }

    @Override
    public CartDTO save(CartDTO cartDTO) {
        log.debug("Request to save Cart : {}", cartDTO);
        Cart cart = cartMapper.toEntity(cartDTO);
        cart = cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    @Override
    public Optional<CartDTO> partialUpdate(CartDTO cartDTO) {
        log.debug("Request to partially update Cart : {}", cartDTO);

        return cartRepository
            .findById(cartDTO.getId())
            .map(existingCart -> {
                cartMapper.partialUpdate(existingCart, cartDTO);

                return existingCart;
            })
            .map(cartRepository::save)
            .map(cartMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Carts");
        return cartRepository.findAll(pageable).map(cartMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CartDTO> findOne(Long id) {
        log.debug("Request to get Cart : {}", id);
        return cartRepository.findById(id).map(cartMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Cart : {}", id);
        cartRepository.deleteById(id);
    }

    @Override
    public ServiceResult getCart() {
        Optional<User> login = userService.getUserWithAuthorities();
        if(login.isEmpty()){
            return new ServiceResult(null, HttpStatus.UNAUTHORIZED, "Chưa đăng nhập");
        }
        Cart cart = cartRepository.findByUserIdAndStatus(login.get().getId(), true);
        if(Objects.isNull(cart)){
            return new ServiceResult(null, HttpStatus.OK, "Giỏ hàng chưa tạo");
        }
        List<CartItems> list = cartItemsRepository.findAllByCartId(cart.getId());
        CartDTO  rs = cartMapper.toDto(cart);
        List<CartItemsDTO> listRes = new ArrayList<>();
        for(CartItems item : list){
            ProductsDTO temp = new ProductsDTO();
            temp.setId(item.getProductId());
            List<ProductFullDataDTO> a = productsCustomRepository.search(temp,0,5);
            CartItemsDTO add = new CartItemsDTO();
            add.setId(item.getId());
            add.setCartId(cart.getId());
            add.setPrice(a.get(0).getPrice());
            add.setQuantity(item.getQuantity());
            add.setProductImage(a.get(0).getImage());
            add.setProductName(a.get(0).getName());
            listRes.add(add);
        }
        rs.setListItem(listRes);
        return new ServiceResult(rs, HttpStatus.OK, "Data nè");
    }
}
