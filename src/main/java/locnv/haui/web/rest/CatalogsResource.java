package locnv.haui.web.rest;

import locnv.haui.commons.*;
import locnv.haui.domain.User;
import locnv.haui.repository.CatalogsRepository;
import locnv.haui.service.CatalogsService;
import locnv.haui.service.UserService;
import locnv.haui.service.dto.CatalogsDTO;
import locnv.haui.service.dto.ExcelColumn;
import locnv.haui.service.dto.ExcelTitle;
import locnv.haui.service.dto.ServiceResult;
import locnv.haui.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * REST controller for managing {@link locnv.haui.domain.Catalogs}.
 */
@RestController
@RequestMapping("/api")
public class CatalogsResource {

    private final Logger log = LoggerFactory.getLogger(CatalogsResource.class);

    private static final String ENTITY_NAME = "catalogs";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Value("${import-file.subFolder}")
    private String subFolder;

    @Value("${import-file.folder}")
    private String folder;

    private final CatalogsService catalogsService;

    private final CatalogsRepository catalogsRepository;

    @Autowired
    private final UserService userService;

    private final ExportUtils exportUtils;

    private final FileExportUtil fileExportUtil;

    public CatalogsResource(CatalogsService catalogsService, CatalogsRepository catalogsRepository, UserService userService, ExportUtils exportUtils, FileExportUtil fileExportUtil) {
        this.catalogsService = catalogsService;
        this.catalogsRepository = catalogsRepository;
        this.userService = userService;
        this.exportUtils = exportUtils;
        this.fileExportUtil = fileExportUtil;
    }

    /**
     * {@code POST  /catalogs} : Create a new catalogs.
     *
     * @param catalogsDTO the catalogsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new catalogsDTO, or with status {@code 400 (Bad Request)} if the catalogs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/management/catalogs")
    public ResponseEntity<?> createCatalogs(@RequestBody CatalogsDTO catalogsDTO) throws URISyntaxException {
        log.debug("REST request to save Catalogs : {}", catalogsDTO);
        if (catalogsDTO.getId() != null) {
            throw new BadRequestAlertException("A new catalogs cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<User> user = userService.getUserWithAuthorities();
        if(user.isPresent()){
            catalogsDTO.setCreateBy(user.get().getLogin());
            catalogsDTO.setCreateDate(ZonedDateTime.now());
            catalogsDTO.setLastModifiedBy(user.get().getLogin());
            catalogsDTO.setLastModifiedDate(ZonedDateTime.now());
        }
        return ResponseEntity.ok(catalogsService.create(catalogsDTO));
    }

    /**
     * {@code PUT  /catalogs/:id} : Updates an existing catalogs.
     *
     * @param id the id of the catalogsDTO to save.
     * @param catalogsDTO the catalogsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogsDTO,
     * or with status {@code 400 (Bad Request)} if the catalogsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the catalogsDTO couldn't be updated.
     */
    @PutMapping("/catalogs/{id}")
    public ResponseEntity<CatalogsDTO> updateCatalogs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CatalogsDTO catalogsDTO
    ) {
        log.debug("REST request to update Catalogs : {}, {}", id, catalogsDTO);
        if (catalogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!catalogsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CatalogsDTO result = catalogsService.save(catalogsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, catalogsDTO.getId().toString()))
            .body(result);
    }


    @PostMapping("/management/catalogs/update")
    public ResponseEntity<?> updateCatalogs(@RequestBody CatalogsDTO catalogsDTO){
        if (catalogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<User> user = userService.getUserWithAuthorities();
        if(user.isPresent()){
            catalogsDTO.setLastModifiedDate(ZonedDateTime.now());
            catalogsDTO.setLastModifiedBy(user.get().getLogin());
        }
        return ResponseEntity.ok(catalogsService.update(catalogsDTO));
    }

    /**
     * {@code PATCH  /catalogs/:id} : Partial updates given fields of an existing catalogs, field will ignore if it is null
     *
     * @param id the id of the catalogsDTO to save.
     * @param catalogsDTO the catalogsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated catalogsDTO,
     * or with status {@code 400 (Bad Request)} if the catalogsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the catalogsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the catalogsDTO couldn't be updated.
     */
    @PatchMapping(value = "/catalogs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CatalogsDTO> partialUpdateCatalogs(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CatalogsDTO catalogsDTO
    ) {
        log.debug("REST request to partial update Catalogs partially : {}, {}", id, catalogsDTO);
        if (catalogsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, catalogsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!catalogsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CatalogsDTO> result = catalogsService.partialUpdate(catalogsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, catalogsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /catalogs} : get all the catalogs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of catalogs in body.
     */
    @GetMapping("/catalogs")
    public ResponseEntity<List<CatalogsDTO>> getAllCatalogs(Pageable pageable) {
        log.debug("REST request to get a page of Catalogs");
        Page<CatalogsDTO> page = catalogsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /catalogs/:id} : get the "id" catalogs.
     *
     * @param id the id of the catalogsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the catalogsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/catalogs/{id}")
    public ResponseEntity<CatalogsDTO> getCatalogs(@PathVariable Long id) {
        log.debug("REST request to get Catalogs : {}", id);
        Optional<CatalogsDTO> catalogsDTO = catalogsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(catalogsDTO);
    }

    /**
     * {@code DELETE  /catalogs/:id} : delete the "id" catalogs.
     *
     * @param catalogsDTO is the catalogsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PostMapping("/management/catalogs/delete")
    public ResponseEntity<?> deleteCatalogs(@RequestBody CatalogsDTO catalogsDTO) {
        log.debug("REST request to delete Catalogs : {}", catalogsDTO.getName());
        return ResponseEntity.ok(catalogsService.delete(catalogsDTO.getId()));
    }

    @PostMapping("/catalogs/searchForTree")
    public  ResponseEntity<List<CatalogsDTO>> getCatalogsForTree(@RequestBody CatalogsDTO catalogsDTO){
        List<CatalogsDTO> rs;
        rs = catalogsService.getCatalogsForTree(catalogsDTO);
        return ResponseEntity.ok(rs);
    }

    @PostMapping("/management/catalogs/checkExist")
    public ResponseEntity<?> checkExistDocumentary(@RequestBody CatalogsDTO catalogsDTO){
        return ResponseEntity.ok().body(catalogsService.checkExist(catalogsDTO.getCode()));
    }

    @PostMapping("/catalogs/exportExcel")
    public ResponseEntity<?> exportExcel(@RequestBody CatalogsDTO catalogsDTO) throws NullPointerException, IllegalArgumentException,
        IOException, IllegalAccessException {

        List<CatalogsDTO> listExport = catalogsService.getDataExport(catalogsDTO);
        List<ExcelColumn> listColumn = buildColumnExport();
        String title = Translator.toLocale("catalog.title.export");
        ExcelTitle excelTitle = new ExcelTitle(title,title,"");
        ByteArrayInputStream byteArrayInputStream = exportUtils.onExport(listColumn, listExport, 3, 0, excelTitle, true);
        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
        return ResponseEntity.ok()
            .contentLength(byteArrayInputStream.available())
            .contentType(MediaType.parseMediaType("application/octet-stream"))
            .body(resource);
    }

    @PostMapping("/catalogs/getSampleFile")
    public ResponseEntity<?> getSampleFile() throws Exception {
        log.debug("REST request to download file sample");
        byte[] fileData = catalogsService.getSampleFile();
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstants.YYYYMMDDHHSS);
        String fileName = "DS_danhmucsanpham" + dateFormat.format(new Date()) + AppConstants.DOT
            + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }

    @PostMapping("/catalogs/importFile")
    public ResponseEntity<?> importFile(@RequestParam(value = "file") MultipartFile file, @RequestParam(required = false) Integer isAddNew){
        log.debug("REST request to import file catalogs");
        try {
            String fileName = file.getOriginalFilename();
            String fileInputPath = FileUtils.writeFileToServer(file.getInputStream(), fileName, this.subFolder, this.folder);
            List<CatalogsDTO> result = catalogsService.importFile(file, fileInputPath, isAddNew);
            ServiceResult<List<CatalogsDTO>> response;
            if(result.isEmpty()){
                response = new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg_format_file_not_allow"));
                return ResponseEntity.ok(response);
            }
            if (result.size() == 1) {
                if (result.get(0).getListError().isEmpty()) {
                    response = new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg_file_import_fail"));
                    return ResponseEntity.ok(response);
                }else{
                    if (result.get(0).getListError().size() == 1) {
                        response = new ServiceResult<>(result, HttpStatus.BAD_REQUEST, result.get(0).getListError().get(0).getDetailError());
                        return ResponseEntity.ok(response);
                    }
                    response = new ServiceResult<>(result, HttpStatus.BAD_REQUEST, Translator.toLocale("msg_no_record_success"));
                    return ResponseEntity.ok(response);
                }
            }
            response = new ServiceResult<>(result, HttpStatus.OK, Translator.toLocale("msg_import_success"));
            return ResponseEntity.ok(response);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/catalogs/downloadErrorFile")
    public ResponseEntity<?> downloadExcelError(@RequestBody CatalogsDTO catalogsDTO) {
        log.debug("REST request to download file error classroom ");
        try {
            ByteArrayInputStream bais = catalogsService.downloadExcelError(catalogsDTO);
            InputStreamResource resource = new InputStreamResource(bais);
            return ResponseEntity.ok()
                .contentLength(bais.available())
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(resource);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private List<ExcelColumn> buildColumnExport() {
        List<ExcelColumn> listColumn = new ArrayList<>();
        listColumn.add(new ExcelColumn("id", Translator.toLocale("catalog.excelTitle.id"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("code", Translator.toLocale("catalog.excelTitle.code"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("name", Translator.toLocale("catalog.excelTitle.name"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("parentCode", Translator.toLocale("catalog.excelTitle.parentCode"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("parentName", Translator.toLocale("catalog.excelTitle.parentName"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("createDateString", Translator.toLocale("catalog.excelTitle.createDate"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("createBy", Translator.toLocale("catalog.excelTitle.createBy"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("lastModifiedDateString", Translator.toLocale("catalog.excelTitle.modifiedDate"), ExcelColumn.ALIGN_MENT.LEFT));
        listColumn.add(new ExcelColumn("lastModifiedBy", Translator.toLocale("catalog.excelTitle.modifiedBy"), ExcelColumn.ALIGN_MENT.LEFT));
        return listColumn;
    }

    List<CatalogsDTO> getListExport(List<CatalogsDTO> list){
        List<CatalogsDTO> rs = new ArrayList<>();
        for (CatalogsDTO c : list){
            rs.add(c);
            if(!c.getChildren().isEmpty()){
                rs.addAll(getListExport(c.getChildren()));
            }
        }
        return rs;
    }
}
