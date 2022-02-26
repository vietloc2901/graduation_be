package locnv.haui.service.impl;

import java.util.Optional;
import locnv.haui.domain.CartItems;
import locnv.haui.repository.CartItemsRepository;
import locnv.haui.service.CartItemsService;
import locnv.haui.service.dto.CartItemsDTO;
import locnv.haui.service.mapper.CartItemsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public CartItemsServiceImpl(CartItemsRepository cartItemsRepository, CartItemsMapper cartItemsMapper) {
        this.cartItemsRepository = cartItemsRepository;
        this.cartItemsMapper = cartItemsMapper;
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
}
