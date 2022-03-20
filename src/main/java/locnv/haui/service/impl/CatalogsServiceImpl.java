package locnv.haui.service.impl;

import java.util.*;

import liquibase.pro.packaged.E;
import locnv.haui.domain.Catalogs;
import locnv.haui.domain.Products;
import locnv.haui.repository.CatalogsCustomRepository;
import locnv.haui.repository.CatalogsRepository;
import locnv.haui.repository.ProductsRepository;
import locnv.haui.service.CatalogsService;
import locnv.haui.service.dto.CatalogsDTO;
import locnv.haui.service.dto.ServiceResult;
import locnv.haui.service.mapper.CatalogsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Catalogs}.
 */
@Service
@Transactional
public class CatalogsServiceImpl implements CatalogsService {

    private final Logger log = LoggerFactory.getLogger(CatalogsServiceImpl.class);

    private final CatalogsRepository catalogsRepository;

    @Autowired
    private final CatalogsCustomRepository catalogsCustomRepository;

    private final CatalogsMapper catalogsMapper;

    @Autowired
    private final ProductsRepository productsRepository;

    public CatalogsServiceImpl(CatalogsRepository catalogsRepository, CatalogsCustomRepository catalogsCustomRepository, CatalogsMapper catalogsMapper, ProductsRepository productsRepository) {
        this.catalogsRepository = catalogsRepository;
        this.catalogsCustomRepository = catalogsCustomRepository;
        this.catalogsMapper = catalogsMapper;
        this.productsRepository = productsRepository;
    }

    @Override
    public CatalogsDTO save(CatalogsDTO catalogsDTO) {
        log.debug("Request to save Catalogs : {}", catalogsDTO);
        Catalogs catalogs = catalogsMapper.toEntity(catalogsDTO);
        catalogs = catalogsRepository.save(catalogs);
        return catalogsMapper.toDto(catalogs);
    }

    @Override
    public ServiceResult<CatalogsDTO> create(CatalogsDTO catalogsDTO) {
        if(Objects.nonNull(catalogsRepository.findByCodeIgnoreCase(catalogsDTO.getCode()))){
            return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, "Mã danh mục đã tồn tại");
        }
        try{
            Catalogs catalogs = catalogsRepository.save(catalogsMapper.toEntity(catalogsDTO));
            return new ServiceResult<>(catalogsMapper.toDto(catalogs), HttpStatus.OK, "Thêm mới thành công");
        }catch (Exception e){
            return new ServiceResult<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lưu danh mục. Thử lại sau");
        }
    }

    @Override
    public Optional<CatalogsDTO> partialUpdate(CatalogsDTO catalogsDTO) {
        log.debug("Request to partially update Catalogs : {}", catalogsDTO);

        return catalogsRepository
            .findById(catalogsDTO.getId())
            .map(existingCatalogs -> {
                catalogsMapper.partialUpdate(existingCatalogs, catalogsDTO);

                return existingCatalogs;
            })
            .map(catalogsRepository::save)
            .map(catalogsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Catalogs");
        return catalogsRepository.findAll(pageable).map(catalogsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CatalogsDTO> findOne(Long id) {
        log.debug("Request to get Catalogs : {}", id);
        return catalogsRepository.findById(id).map(catalogsMapper::toDto);
    }

    @Override
    public ServiceResult delete(Long id) {
        log.debug("Request to delete Catalogs : {}", id);
        List<Products> listByCatalog = productsRepository.findAllByCatalogId(id);
        if(!listByCatalog.isEmpty()){
            return new ServiceResult(null, HttpStatus.BAD_REQUEST, "Danh mục đã có sản phẩm. Không thể xóa!");
        }
        List<Catalogs> catalogsList = catalogsRepository.findAllByParentId(id);
        if(!catalogsList.isEmpty()){
            return new ServiceResult(null, HttpStatus.BAD_REQUEST, "Danh mục đã có danh mục con phụ thuộc. Không thể xóa!");
        }
        try{
            catalogsRepository.deleteById(id);
            return new ServiceResult(null, HttpStatus.OK, "Xóa danh mục thành công!");
        }catch (Exception e){
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi không xác định. Vui lòng thử lại sau");
        }
    }

    @Override
    public ServiceResult<CatalogsDTO> update(CatalogsDTO catalogsDTO) {
        Optional<Catalogs> catalogs = catalogsRepository.findById(catalogsDTO.getId());
        if(catalogs.isEmpty()){
            return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, "Danh mục đã bị xóa!");
        }
        try{
            Catalogs updated = catalogsRepository.save(catalogsMapper.toEntity(catalogsDTO));
            return new ServiceResult<>(catalogsMapper.toDto(updated), HttpStatus.OK, "Cập nhật thành công!");
        }catch (Exception e){
            return new ServiceResult(null, HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi không xác định. Vui lòng thử lại sau");
        }
    }

    @Override
    public List<CatalogsDTO> getCatalogsForTree(CatalogsDTO catalogsDTO) {
        List<CatalogsDTO> rs = catalogsCustomRepository.getCatalogsForTree(catalogsDTO);
        List<CatalogsDTO> lsRoot = new ArrayList<>();
        for (CatalogsDTO dto : rs) {
            if (null == dto.getParentId() || 0L == dto.getParentId()) {
                lsRoot.add(dto);
                lsRoot.sort(Comparator.comparing(CatalogsDTO::getSortOrder));
            }
            for (CatalogsDTO dtoChild : rs) {
                if (dto.getId().equals(dtoChild.getParentId())) {
                    if (null == dto.getChildren()) {
                        List<CatalogsDTO> myChildrens = new ArrayList<>();
                        myChildrens.add(dtoChild);
                        dto.setChildren(myChildrens);
                        dto.getChildren().sort(Comparator.comparing(CatalogsDTO::getSortOrder));
                    } else {
                        dto.getChildren().add(dtoChild);
                        dto.getChildren().sort(Comparator.comparing(CatalogsDTO::getSortOrder));
                    }
                }
            }
        }
        return lsRoot;
    }

    @Override
    public ServiceResult<CatalogsDTO> checkExist(String code) {
        Catalogs isExist = catalogsRepository.findByCodeIgnoreCase(code);
        if(isExist != null){
            return new ServiceResult<>(catalogsMapper.toDto(isExist), HttpStatus.BAD_REQUEST,"Mã danh mục đã tồn tại");
        }else{
            return new ServiceResult<>(null,HttpStatus.OK,null);
        }
    }
}
