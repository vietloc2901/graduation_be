package locnv.haui.service.impl;

import locnv.haui.domain.Catalogs;
import locnv.haui.domain.ProductSpecs;
import locnv.haui.domain.Products;
import locnv.haui.domain.ProductsPrice;
import locnv.haui.repository.*;
import locnv.haui.service.AmazonClient;
import locnv.haui.service.ProductsService;
import locnv.haui.service.dto.*;
import locnv.haui.service.mapper.ProductSpecsMapper;
import locnv.haui.service.mapper.ProductsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Products}.
 */
@Service
@Transactional
public class ProductsServiceImpl implements ProductsService {

    private final Logger log = LoggerFactory.getLogger(ProductsServiceImpl.class);

    private final ProductsRepository productsRepository;

    private final ProductsMapper productsMapper;

    private final ProductsPriceRepository productsPriceRepository;

    private final AmazonClient amazonClient;

    private final ProductSpecsRepository productSpecsRepository;

    private final ProductSpecsMapper specsMapper;

    private final CatalogsRepository catalogsRepository;

    @Autowired
    private final ProductsCustomRepository productsCustomRepository;

    public ProductsServiceImpl(ProductsRepository productsRepository, ProductsMapper productsMapper, ProductsPriceRepository productsPriceRepository, AmazonClient amazonClient, ProductSpecsRepository productSpecsRepository, ProductSpecsMapper specsMapper, CatalogsRepository catalogsRepository, ProductsCustomRepository productsCustomRepository) {
        this.productsRepository = productsRepository;
        this.productsMapper = productsMapper;
        this.productsPriceRepository = productsPriceRepository;
        this.amazonClient = amazonClient;
        this.productSpecsRepository = productSpecsRepository;
        this.specsMapper = specsMapper;
        this.catalogsRepository = catalogsRepository;
        this.productsCustomRepository = productsCustomRepository;
    }

    @Override
    public ProductsDTO save(ProductsDTO productsDTO) {
        log.debug("Request to save Products : {}", productsDTO);
        Products products = productsMapper.toEntity(productsDTO);
        products = productsRepository.save(products);
        return productsMapper.toDto(products);
    }

    @Override
    public Optional<ProductsDTO> partialUpdate(ProductsDTO productsDTO) {
        log.debug("Request to partially update Products : {}", productsDTO);

        return productsRepository
            .findById(productsDTO.getId())
            .map(existingProducts -> {
                productsMapper.partialUpdate(existingProducts, productsDTO);

                return existingProducts;
            })
            .map(productsRepository::save)
            .map(productsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productsRepository.findAll(pageable).map(productsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductsDTO> findOne(Long id) {
        log.debug("Request to get Products : {}", id);
        return productsRepository.findById(id).map(productsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Products : {}", id);
        productsRepository.deleteById(id);
    }

    @Override
    public DataDTO<ProductFullDataDTO> search(ProductsDTO productsDTO, int page, int pageSize) {
        DataDTO<ProductFullDataDTO> data =new DataDTO<>();
        List<ProductFullDataDTO> productsDTOList = productsCustomRepository.search(productsDTO, page, pageSize);
        data.setPage(page);
        data.setPageSize(pageSize);
        data.setTotal(productsCustomRepository.totalRecord(productsDTO).intValue());
        data.setData(productsDTOList);
        return data;
    }

    @Override
    public DataDTO<ProductFullDataDTO> searchForViewPage(ProductsDTO productsDTO, int page, int pageSize) {
        DataDTO<ProductFullDataDTO> data =new DataDTO<>();
        List<ProductFullDataDTO> productsDTOList = productsCustomRepository.searchForViewProduct(productsDTO, page, pageSize);
        data.setPage(page);
        data.setPageSize(pageSize);
        data.setTotal(productsCustomRepository.totalRecordSearchForView(productsDTO).intValue());
        data.setData(productsDTOList);
        return data;
    }

    @Override
    public ServiceResult create(MultipartFile image, ProductsDTO productsDTO, List<ProductSpecsDTO> spec) {
        Products isExist = productsRepository.findByCode(productsDTO.getCode());
        if(Objects.nonNull(isExist)){
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR, "Mã sản phẩm đã tồn tại");
        }
        String path = "";
        try{
            path = amazonClient.uploadFile(image);
        }catch (Exception e){
            log.debug(e.getMessage());
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR,"Error save image to AWS");
        }
        productsDTO.setImage(path);
        Products save = productsMapper.toEntity(productsDTO);
        save.setStatus(productsDTO.getStatus());
        save = productsRepository.save(save);

        ProductsPrice price = new ProductsPrice();
        price.setPrice(BigDecimal.valueOf(productsDTO.getPrice()));
        price.setProductId(save.getId());
        price.setCreateBy(save.getCreateBy());
        price.setApplyDate(ZonedDateTime.now());
        try{
            productsPriceRepository.save(price);
        }catch (Exception e){
            log.debug(e.getMessage());
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR, "Error save price");
        }
        for (ProductSpecsDTO item: spec) {
            item.setProductId(save.getId());
        }
        try{
            productSpecsRepository.saveAll(specsMapper.toEntity(spec));
        }catch (Exception e){
            log.debug(e.getMessage());
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR, "Error save specs");
        }
        return new ServiceResult(save, HttpStatus.OK, "Thêm mới thành công!");
    }

    @Override
    public ServiceResult searchProduct(Long id) {
        ProductsDTO productsDTO = new ProductsDTO();
        productsDTO.setId(id);
        List<ProductFullDataDTO> fullDataDTO = productsCustomRepository.search(productsDTO,0,10);
        if(fullDataDTO.isEmpty()){
            return new ServiceResult(null, HttpStatus.NOT_FOUND, "Không tìm thấy");
        }
        ProductFullDataDTO rs = fullDataDTO.get(0);
        Products products = productsRepository.findById(id).get();
        if(Objects.nonNull(products)){
            rs.setBrand(products.getBrand());
            rs.setImage(products.getImage());
            rs.setDetailImages(products.getDetailImages());
        }
        List<ProductSpecsDTO> specsDTOList = specsMapper.toDto(productSpecsRepository.findAllByProductId(rs.getId()));
        rs.setSpecsDTOList(specsDTOList);
        return new ServiceResult(rs, HttpStatus.OK, "Tìm thấy nè");
    }

    @Override
    public ServiceResult update(MultipartFile image, ProductsDTO productsDTO, List<ProductSpecsDTO> spec) {
        if(Objects.isNull(productsDTO.getId())){
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR,"Cannot update a non id object");
        }
        String path = "";
        if(Objects.nonNull(image)){
            try{
                path = amazonClient.uploadFile(image);
                productsDTO.setImage(path);
            }catch (Exception e){
                log.debug(e.getMessage());
                return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR,"Error save image to AWS");
            }
        }
        Optional<Products> edit = productsRepository.findById(productsDTO.getId());
        if(edit.isEmpty()){
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR,"Sản phẩm không còn tồn tại");
        }
        Products editProduct = edit.get();
        editProduct.setStatus(productsDTO.getStatus());
        editProduct.setName(productsDTO.getName());
        editProduct.setBrand(productsDTO.getBrand());
        editProduct.setProductDetails(productsDTO.getProductDetails());
        editProduct.setDescriptionDocument(productsDTO.getDescriptionDocument());
        editProduct.setCatalogId(productsDTO.getCatalogId());
        editProduct.setLastModifiedDate(ZonedDateTime.now());
        editProduct.setLastModifiedBy(productsDTO.getLastModifiedBy());
        if(Objects.nonNull(image)){
            editProduct.setImage(path);
        }
        editProduct = productsRepository.save(editProduct);

        ProductsPrice price = new ProductsPrice();
        price.setPrice(BigDecimal.valueOf(productsDTO.getPrice()));
        price.setProductId(editProduct.getId());
        price.setCreateBy(editProduct.getLastModifiedBy());
        price.setApplyDate(ZonedDateTime.now());
        try{
            productsPriceRepository.save(price);
        }catch (Exception e){
            log.debug(e.getMessage());
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR, "Error save price");
        }
        for(ProductSpecsDTO item : spec){
            if(Objects.nonNull(item.getId())){
                ProductSpecs p = productSpecsRepository.findById(item.getId()).get();
                p.setKey(item.getKey());
                p.setValue(item.getValue());
                productSpecsRepository.save(p);
            }else{
                item.setProductId(editProduct.getId());
                productSpecsRepository.save(specsMapper.toEntity(item));
            }
        }
        return new ServiceResult(editProduct, HttpStatus.OK, "Cập nhật thành công!");
    }

    @Override
    public DataDTO<ProductFullDataDTO> searchForView(ProductsDTO productsDTO) {
        DataDTO<ProductFullDataDTO> data =new DataDTO<>();
        List<ProductFullDataDTO> productsDTOList = productsCustomRepository.searchForView(productsDTO);
        data.setData(productsDTOList);
        return data;
    }

    @Override
    public DataDTO<ProductFullDataDTO> searchByCatalogNoChild() {
        DataDTO<ProductFullDataDTO> data =new DataDTO<>();
        List<Catalogs> listNoChild = catalogsRepository.findAllByParentId(null);
        return data;
    }
}
