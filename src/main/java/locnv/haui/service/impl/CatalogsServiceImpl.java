package locnv.haui.service.impl;

import java.util.Optional;
import locnv.haui.domain.Catalogs;
import locnv.haui.repository.CatalogsRepository;
import locnv.haui.service.CatalogsService;
import locnv.haui.service.dto.CatalogsDTO;
import locnv.haui.service.mapper.CatalogsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final CatalogsMapper catalogsMapper;

    public CatalogsServiceImpl(CatalogsRepository catalogsRepository, CatalogsMapper catalogsMapper) {
        this.catalogsRepository = catalogsRepository;
        this.catalogsMapper = catalogsMapper;
    }

    @Override
    public CatalogsDTO save(CatalogsDTO catalogsDTO) {
        log.debug("Request to save Catalogs : {}", catalogsDTO);
        Catalogs catalogs = catalogsMapper.toEntity(catalogsDTO);
        catalogs = catalogsRepository.save(catalogs);
        return catalogsMapper.toDto(catalogs);
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
    public void delete(Long id) {
        log.debug("Request to delete Catalogs : {}", id);
        catalogsRepository.deleteById(id);
    }
}
