package locnv.haui.service.impl;

import java.util.Optional;
import locnv.haui.domain.ProductsPrice;
import locnv.haui.repository.ProductsPriceRepository;
import locnv.haui.service.ProductsPriceService;
import locnv.haui.service.dto.ProductsPriceDTO;
import locnv.haui.service.mapper.ProductsPriceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProductsPrice}.
 */
@Service
@Transactional
public class ProductsPriceServiceImpl implements ProductsPriceService {

    private final Logger log = LoggerFactory.getLogger(ProductsPriceServiceImpl.class);

    private final ProductsPriceRepository productsPriceRepository;

    private final ProductsPriceMapper productsPriceMapper;

    public ProductsPriceServiceImpl(ProductsPriceRepository productsPriceRepository, ProductsPriceMapper productsPriceMapper) {
        this.productsPriceRepository = productsPriceRepository;
        this.productsPriceMapper = productsPriceMapper;
    }

    @Override
    public ProductsPriceDTO save(ProductsPriceDTO productsPriceDTO) {
        log.debug("Request to save ProductsPrice : {}", productsPriceDTO);
        ProductsPrice productsPrice = productsPriceMapper.toEntity(productsPriceDTO);
        productsPrice = productsPriceRepository.save(productsPrice);
        return productsPriceMapper.toDto(productsPrice);
    }

    @Override
    public Optional<ProductsPriceDTO> partialUpdate(ProductsPriceDTO productsPriceDTO) {
        log.debug("Request to partially update ProductsPrice : {}", productsPriceDTO);

        return productsPriceRepository
            .findById(productsPriceDTO.getId())
            .map(existingProductsPrice -> {
                productsPriceMapper.partialUpdate(existingProductsPrice, productsPriceDTO);

                return existingProductsPrice;
            })
            .map(productsPriceRepository::save)
            .map(productsPriceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductsPriceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductsPrices");
        return productsPriceRepository.findAll(pageable).map(productsPriceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductsPriceDTO> findOne(Long id) {
        log.debug("Request to get ProductsPrice : {}", id);
        return productsPriceRepository.findById(id).map(productsPriceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProductsPrice : {}", id);
        productsPriceRepository.deleteById(id);
    }
}
