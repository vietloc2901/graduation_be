package locnv.haui.service.impl;

import java.util.Optional;
import locnv.haui.domain.ProductSpecs;
import locnv.haui.repository.ProductSpecsRepository;
import locnv.haui.service.ProductSpecsService;
import locnv.haui.service.dto.ProductSpecsDTO;
import locnv.haui.service.mapper.ProductSpecsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProductSpecs}.
 */
@Service
@Transactional
public class ProductSpecsServiceImpl implements ProductSpecsService {

    private final Logger log = LoggerFactory.getLogger(ProductSpecsServiceImpl.class);

    private final ProductSpecsRepository productSpecsRepository;

    private final ProductSpecsMapper productSpecsMapper;

    public ProductSpecsServiceImpl(ProductSpecsRepository productSpecsRepository, ProductSpecsMapper productSpecsMapper) {
        this.productSpecsRepository = productSpecsRepository;
        this.productSpecsMapper = productSpecsMapper;
    }

    @Override
    public ProductSpecsDTO save(ProductSpecsDTO productSpecsDTO) {
        log.debug("Request to save ProductSpecs : {}", productSpecsDTO);
        ProductSpecs productSpecs = productSpecsMapper.toEntity(productSpecsDTO);
        productSpecs = productSpecsRepository.save(productSpecs);
        return productSpecsMapper.toDto(productSpecs);
    }

    @Override
    public Optional<ProductSpecsDTO> partialUpdate(ProductSpecsDTO productSpecsDTO) {
        log.debug("Request to partially update ProductSpecs : {}", productSpecsDTO);

        return productSpecsRepository
            .findById(productSpecsDTO.getId())
            .map(existingProductSpecs -> {
                productSpecsMapper.partialUpdate(existingProductSpecs, productSpecsDTO);

                return existingProductSpecs;
            })
            .map(productSpecsRepository::save)
            .map(productSpecsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSpecsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductSpecs");
        return productSpecsRepository.findAll(pageable).map(productSpecsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductSpecsDTO> findOne(Long id) {
        log.debug("Request to get ProductSpecs : {}", id);
        return productSpecsRepository.findById(id).map(productSpecsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProductSpecs : {}", id);
        productSpecsRepository.deleteById(id);
    }
}
