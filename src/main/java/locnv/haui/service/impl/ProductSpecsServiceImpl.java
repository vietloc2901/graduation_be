package locnv.haui.service.impl;

import java.util.Objects;
import java.util.Optional;
import locnv.haui.domain.ProductSpecs;
import locnv.haui.repository.ProductSpecsRepository;
import locnv.haui.service.ProductSpecsService;
import locnv.haui.service.dto.ProductSpecsDTO;
import locnv.haui.service.dto.ServiceResult;
import locnv.haui.service.mapper.ProductSpecsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @Override
    public ServiceResult deleteProductSpec(ProductSpecsDTO productSpecsDTO) {
        if(Objects.isNull(productSpecsDTO.getId())){
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR, "Cannot delete a non Id objects");
        }
        try{
            productSpecsRepository.deleteById(productSpecsDTO.getId());
            return new ServiceResult(null, HttpStatus.OK, "Xóa thành công");
        }catch (Exception e){
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi xóa thuộc tính. Thử lại sau");
        }
    }
}
