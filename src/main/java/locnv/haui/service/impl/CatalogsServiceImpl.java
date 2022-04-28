package locnv.haui.service.impl;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.amazonaws.services.lightsail.model.UnauthenticatedException;
import locnv.haui.commons.*;
import locnv.haui.domain.Catalogs;
import locnv.haui.domain.Products;
import locnv.haui.domain.User;
import locnv.haui.repository.CatalogsCustomRepository;
import locnv.haui.repository.CatalogsRepository;
import locnv.haui.repository.ProductsRepository;
import locnv.haui.service.CatalogsService;
import locnv.haui.service.UserService;
import locnv.haui.service.dto.*;
import locnv.haui.service.mapper.CatalogsMapper;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    private final FileExportUtil fileExportUtil;

    private final UserService userService;

    public CatalogsServiceImpl(CatalogsRepository catalogsRepository, CatalogsCustomRepository catalogsCustomRepository, CatalogsMapper catalogsMapper, ProductsRepository productsRepository, FileExportUtil fileExportUtil, UserService userService) {
        this.catalogsRepository = catalogsRepository;
        this.catalogsCustomRepository = catalogsCustomRepository;
        this.catalogsMapper = catalogsMapper;
        this.productsRepository = productsRepository;
        this.fileExportUtil = fileExportUtil;
        this.userService = userService;
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
                lsRoot.sort(Comparator.comparing(CatalogsDTO::getSortOrder, Comparator.nullsFirst(Comparator.naturalOrder())));
            }
            for (CatalogsDTO dtoChild : rs) {
                if (dto.getId().equals(dtoChild.getParentId())) {
                    if (null == dto.getChildren()) {
                        List<CatalogsDTO> myChildrens = new ArrayList<>();
                        myChildrens.add(dtoChild);
                        dto.setChildren(myChildrens);
                        dto.getChildren().sort(Comparator.comparing(CatalogsDTO::getSortOrder, Comparator.nullsFirst(Comparator.naturalOrder())));
                    } else {
                        dto.getChildren().add(dtoChild);
                        dto.getChildren().sort(Comparator.comparing(CatalogsDTO::getSortOrder, Comparator.nullsFirst(Comparator.naturalOrder())));
                    }
                }
            }
        }
        return lsRoot;
    }

    @Override
    public List<CatalogsDTO> getDataExport(CatalogsDTO catalogsDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm");
        List<CatalogsDTO> rs = catalogsCustomRepository.getDataExport(catalogsDTO);
        for(CatalogsDTO c : rs){
            c.setCreateDateString(formatter.format(c.getCreateDate()));
            c.setLastModifiedDateString(formatter.format(c.getLastModifiedDate()));
        }
        return rs;
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

    @Override
    public byte[] getSampleFile() throws Exception{
        List<SheetConfigDto> sheetConfigList = new ArrayList<>();
        List<CatalogsDTO> catalogsDTOS = new ArrayList<>();
        sheetConfigList = getDataForExcelSample("sheet1", catalogsDTOS, sheetConfigList);
        try{
            return fileExportUtil.exportXLSX(true, sheetConfigList, null);
        }catch (IOException e){
            throw new Exception(e.getMessage());
        }
    }

    private List<SheetConfigDto> getDataForExcelSample(
        String sheetName,
        List<CatalogsDTO> listDataSheet,
        List<SheetConfigDto> sheetConfigDtos
    ){
        SheetConfigDto configDto = new SheetConfigDto();
        String[] headerArr = new String[]{
            "recordNo",
            "catalog.header.code",
            "catalog.header.name",
            "catalog.header.sortOrder",
            "catalog.header.parentCode"
        };
        configDto.setExportType(0);
        configDto.setSheetName(sheetName);
        configDto.setHeaders(headerArr);
        List<Catalogs> lsCatalogs = catalogsRepository.findAll();
        List<String> lstCatalogsCode = lsCatalogs.stream().map(Catalogs::getCode).collect(Collectors.toList());
        CatalogsDTO template = new CatalogsDTO(1,"LTACER", "Máy tính laptop Acer", 1 , "MTLT");
        listDataSheet.add(template);
        for(int i = 2; i < 100; i++){
            CatalogsDTO c = new CatalogsDTO();
            c.setRecordNo(i);
            listDataSheet.add(c);
        }
        List<CellConfigDto> cellConfigDtosCustom = new ArrayList<>();
        cellConfigDtosCustom.add(new CellConfigDto("code", AppConstants.ALIGN_RIGHT, lstCatalogsCode.toArray(new String[0]), 1, 99, 4, 4));

        configDto.setCellCustomList(cellConfigDtosCustom);

        List<CellConfigDto> cellConfigDtos = new ArrayList<>();
        configDto.setCellConfigList(cellConfigDtos);
        configDto.setList(listDataSheet);
        cellConfigDtos.add(new CellConfigDto("recordNo", AppConstants.ALIGN_RIGHT, AppConstants.NUMBER));
        cellConfigDtos.add(new CellConfigDto("code", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigDtos.add(new CellConfigDto("name", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigDtos.add(new CellConfigDto("sortOrder", AppConstants.ALIGN_LEFT, AppConstants.NUMBER));
        cellConfigDtos.add(new CellConfigDto("parentCode", AppConstants.ALIGN_LEFT, AppConstants.STRING));

        configDto.setHasIndex(false);
        configDto.setHasBorder(true);
        configDto.setCellConfigList(cellConfigDtos);
        sheetConfigDtos.add(configDto);
        return sheetConfigDtos;
    }


    List<ExcelDynamicDTO> lstError;

    @Override
    public List<CatalogsDTO> importFile(MultipartFile file, String fileInputName, Integer isAddNew) throws GeneralSecurityException {
        List<CatalogsDTO> result = new ArrayList<>();
        List<Integer> listRowSuccess = new ArrayList<>();
        Optional<User> user = userService.getUserWithAuthorities();
        if(user.isEmpty()){
            return result;
        }
        lstError = new ArrayList<>();
        Long countSuccess = 0L;
        Long item = 0L;
        ExcelDynamicDTO errorFormat = null;
        try{
            String fileName = file.getOriginalFilename();
            int validFile = FileUtils.validateAttachFileExcel(file, fileName);
            if (validFile != 0) {
                log.error("The specified file is not Excel file");
                return result;
            }
            Workbook workbook =  WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            int aa = 0;
            for (int q = sheet.getLastRowNum(); q > 1; q--) {
                aa = 0;
                for (Cell myCell : sheet.getRow(q)) {
                    if (formatter.formatCellValue(myCell).length() != 0) {
                        aa = 1;
                        break;
                    }
                }
                if (aa == 0) {
                    removeRow(sheet, q);
                } else {
                    break;
                }
            }

            int count = 0;
            for (Row row : sheet) {
                count++;
                if (count < 2) continue;
                item++;
                boolean checkColumn1 = true;
                boolean checkColumn2 = true;
                boolean checkColumn3 = true;
                boolean checkColumn4 = true;

                String catalogCode="" ;
                String catalogName="";
                String sortOrder="";
                String parentCode="";


                for (Cell cell : row) {
                    errorFormat = new ExcelDynamicDTO();
                    if (cell.getColumnIndex() == 1) {
                        catalogCode = formatter.formatCellValue(cell).trim();
                        checkColumn1 = checkDataFromFileExel(catalogCode, count, cell.getColumnIndex(), errorFormat, isAddNew, catalogCode);
                    }
                    if (cell.getColumnIndex() == 2) {
                        catalogName = formatter.formatCellValue(cell).trim();
                        checkColumn2 = checkDataFromFileExel(catalogName, count, cell.getColumnIndex(), errorFormat, isAddNew, catalogCode);
                    }
                    if (cell.getColumnIndex() == 3) {
                        sortOrder = formatter.formatCellValue(cell).trim();
                        checkColumn3 = checkDataFromFileExel(sortOrder, count, cell.getColumnIndex(), errorFormat, isAddNew, catalogCode);
                    }
                    if (cell.getColumnIndex() == 4) {
                        parentCode = formatter.formatCellValue(cell).trim();
                        checkColumn4 = checkDataFromFileExel(parentCode, count, cell.getColumnIndex(), errorFormat, isAddNew, catalogCode);
                    }
                }

                if (checkColumn1 && checkColumn2 && checkColumn3 && checkColumn4) {
                    Catalogs newCatalog;
                    if(isAddNew.equals(1)){
                        newCatalog = new Catalogs();
                    }else{
                        newCatalog = catalogsRepository.findByCodeIgnoreCase(catalogCode);
                    }
                    newCatalog.setCode(catalogCode);
                    newCatalog.setName(catalogName);
                    if(StringUtils.isNotNullOrEmpty(sortOrder)){
                        newCatalog.setSortOrder(Integer.parseInt(sortOrder));
                    }
                    if(StringUtils.isNotNullOrEmpty(parentCode)){
                        Catalogs c = catalogsRepository.findByCodeIgnoreCase(parentCode);
                        newCatalog.setParentId(c.getId());
                    }

                    if(isAddNew.equals(1)){
                        newCatalog.setCreateDate(ZonedDateTime.now());
                        newCatalog.setCreateBy(user.get().getLogin());
                    }
                    newCatalog.setLastModifiedBy(user.get().getLogin());
                    newCatalog.setLastModifiedDate(ZonedDateTime.now());
                    catalogsRepository.save(newCatalog);
                    // Check update or create
                    // Save or update
                    countSuccess++;
                    listRowSuccess.add(row.getRowNum());
                    result.add(catalogsMapper.toDto(newCatalog));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        String filePathError = EncryptUtils.encryptFileUploadPath(fileInputName);
        CatalogsDTO objError = new CatalogsDTO();
        objError.setListError(lstError);
        objError.setTotalSuccess(countSuccess);
        objError.setTotalFail(item - countSuccess);
        objError.setFilePathError(filePathError);
        objError.setLineSuccess(listRowSuccess);
        result.add(objError);
        return result;
    }

    @Override
    public ByteArrayInputStream downloadExcelError(CatalogsDTO catalogsDTO) throws GeneralSecurityException, IOException {
        String filePath = EncryptUtils.decryptFileUploadPath(catalogsDTO.getFilePathError());
        InputStream file = new BufferedInputStream(new FileInputStream(filePath));
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);

        CellStyle styleErr = workbook.createCellStyle();
        styleErr.setBorderTop(BorderStyle.MEDIUM);
        styleErr.setTopBorderColor(IndexedColors.RED.getIndex());
        styleErr.setBorderBottom(BorderStyle.MEDIUM);
        styleErr.setBottomBorderColor(IndexedColors.RED.getIndex());
        styleErr.setBorderRight(BorderStyle.MEDIUM);
        styleErr.setRightBorderColor(IndexedColors.RED.getIndex());
        styleErr.setBorderLeft(BorderStyle.MEDIUM);
        styleErr.setLeftBorderColor(IndexedColors.RED.getIndex());


        CellStyle style3 = workbook.createCellStyle();
        style3.setBorderLeft(BorderStyle.THIN);
        style3.setBorderBottom(BorderStyle.THIN);
        style3.setBorderRight(BorderStyle.THIN);
        style3.setBorderTop(BorderStyle.THIN);

        DataFormatter formatter = new DataFormatter();
        int aa = 0;
        for (int q = sheet.getLastRowNum(); q > 1; q--) {
            aa = 0;
            for (Cell myCell : sheet.getRow(q)) {
                if (formatter.formatCellValue(myCell).length() != 0) {
                    aa = 1;
                    break;
                }
            }
            if (aa == 0) {
                removeRow(sheet, q);
            } else {
                break;
            }
        }

        int count = 0;
        for (Row row : sheet) {
            count++;
            if (count < 2) continue;
            for (Cell cell : row) {
                cell.setCellStyle(style3);
            }
        }

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        style.setFont(font);
        style.setWrapText(true);

        style.setBorderTop(BorderStyle.MEDIUM);
        style.setTopBorderColor(IndexedColors.RED.getIndex());
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBottomBorderColor(IndexedColors.RED.getIndex());
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setRightBorderColor(IndexedColors.RED.getIndex());
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setLeftBorderColor(IndexedColors.RED.getIndex());


        CellStyle styleTitleErr = workbook.createCellStyle();
        Font fontTitleErr = workbook.createFont();
        fontTitleErr.setColor(IndexedColors.RED.getIndex());
        fontTitleErr.setBold(true);
        styleTitleErr.setFont(fontTitleErr);
        styleTitleErr.setVerticalAlignment(VerticalAlignment.CENTER);
        styleTitleErr.setAlignment(HorizontalAlignment.CENTER);

        styleTitleErr.setBorderTop(BorderStyle.MEDIUM);
        styleTitleErr.setTopBorderColor(IndexedColors.RED.getIndex());
        styleTitleErr.setBorderBottom(BorderStyle.MEDIUM);
        styleTitleErr.setBottomBorderColor(IndexedColors.RED.getIndex());
        styleTitleErr.setBorderRight(BorderStyle.MEDIUM);
        styleTitleErr.setRightBorderColor(IndexedColors.RED.getIndex());
        styleTitleErr.setBorderLeft(BorderStyle.MEDIUM);
        styleTitleErr.setLeftBorderColor(IndexedColors.RED.getIndex());


        for (Row r : sheet) {
            Cell cell = r.getCell(8);
            if (cell != null) {
                cell.setCellValue("");
            }
        }

        for (int i = 0; i < catalogsDTO.getListError().size(); i++) {
            Row row = sheet.getRow(Integer.parseInt(catalogsDTO.getListError().get(i).getLineError()) - 1);
            if (row == null) {
                row = sheet.createRow(Integer.parseInt(catalogsDTO.getListError().get(i).getLineError()) - 1);
            }
            Cell cellErr = row.getCell(Integer.valueOf(catalogsDTO.getListError().get(i).getColumnError()) - 1);
            cellErr.setCellStyle(styleErr);
            Cell cell = row.getCell(8);
            if (cell == null) {
                cell = row.createCell(8);
            }
            cell.setCellStyle(style);
            sheet.setColumnWidth(8, 10000);
            if (!cell.getStringCellValue().isEmpty()) {
                cell.setCellValue(cell.getStringCellValue() + "\n" + catalogsDTO.getListError().get(i).getDetailError());
            } else {
                cell.setCellValue(catalogsDTO.getListError().get(i).getDetailError());
            }
        }

        for (int i = 0; i < catalogsDTO.getTotalSuccess(); i++) {
            removeRow(sheet, catalogsDTO.getLineSuccess().get(i)-i);
        }

        int stt = 0;
        //Update STT
        for (Row r : sheet) {
            stt++;
            if (stt == 1) continue;

            Cell cell = r.getCell(0);
            if (cell != null) {
                cell.setCellValue(stt - 1);
            }
        }

        Row rowTitle = sheet.getRow(0);
        Cell cellTittle = rowTitle.createCell(8);
        cellTittle.setCellValue(Translator.toLocale("title.excel.error.download"));
        cellTittle.setCellStyle(styleTitleErr);
        sheet.setColumnWidth(8, 10000);


        file.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }

    public boolean checkDataFromFileExel(String data, int rowIndex, int columnIndex, ExcelDynamicDTO errorFormat, Integer isAddNew, String checkData) {

        if (columnIndex == 1) {
            if (data.trim().isEmpty()) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError(Translator.toLocale("catalog.error.code.empty"));
                lstError.add(errorFormat);
                return false;
            }

            if(!StringUtils.checkVIAndSpecialCharacter(data)){
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError(Translator.toLocale("catalog.error.not.specialCharactor"));
                lstError.add(errorFormat);
                return false;
            }

            if (data.trim().length() > 50) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError(Translator.toLocale("catalog.error.code.maxLength"));
                lstError.add(errorFormat);
                return false;
            }

            Catalogs isExist = catalogsRepository.findByCodeIgnoreCase(data);
            if(Objects.nonNull(isExist) && isAddNew.equals(1)){
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError(Translator.toLocale("catalog.error.code.exists"));
                lstError.add(errorFormat);
                return false;
            }

            if(Objects.isNull(isExist) && isAddNew.equals(0)){
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError(Translator.toLocale("catalog.error.not.exists"));
                lstError.add(errorFormat);
                return false;
            }
        }

        if (columnIndex == 2) {
            if (data.trim().isEmpty()) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError(Translator.toLocale("catalog.error.name.empty"));
                lstError.add(errorFormat);
                return false;
            }
            if (data.trim().length() > 250) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError(Translator.toLocale("catalog.error.name.maxLength"));
                lstError.add(errorFormat);
                return false;
            }
        }
        if (columnIndex == 3) {
            if(!data.trim().isBlank()){
                try {
                    Integer.parseInt(data);
                }catch (Exception e){
                    errorFormat.setLineError(String.valueOf(rowIndex));
                    errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                    errorFormat.setDetailError(Translator.toLocale("catalog.error.sortOrder.format"));
                    lstError.add(errorFormat);
                    return false;
                }
            }
        }
        if (columnIndex == 4) {
            if(!data.trim().isBlank()){
                Catalogs isExist = catalogsRepository.findByCodeIgnoreCase(data);
                if(Objects.isNull(isExist)){
                    errorFormat.setLineError(String.valueOf(rowIndex));
                    errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                    errorFormat.setDetailError(Translator.toLocale("catalog.error.not.exists"));
                    lstError.add(errorFormat);
                    return false;
                }
                CatalogsDTO a = new CatalogsDTO();
                a.setCode(data);
                List<CatalogsDTO> checks = getCatalogsForTree(a);
                if(!checks.stream().filter(x -> x.getCode().equalsIgnoreCase(checkData)).collect(Collectors.toList()).isEmpty()){
                    errorFormat.setLineError(String.valueOf(rowIndex));
                    errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                    errorFormat.setDetailError(Translator.toLocale("catalog.error.not.invalid"));
                    lstError.add(errorFormat);
                    return false;
                }
            }
        }
        return true;
    }

    private static void removeRow(Sheet sheet, int rowIndex) {
        int lastRowNum=sheet.getLastRowNum();
        if(rowIndex>=0&&rowIndex<lastRowNum){
            sheet.shiftRows(rowIndex+1,lastRowNum, -1);
        }
        if(rowIndex==lastRowNum){
            Row removingRow=sheet.getRow(rowIndex);
            if(removingRow!=null){
                sheet.removeRow(removingRow);
            }
        }
    }
}
