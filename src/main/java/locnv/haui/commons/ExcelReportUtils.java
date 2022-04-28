package locnv.haui.commons;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ExcelReportUtils {

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;
    public static final int WIDTH = 255;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String FONT_TIMES_NEW_ROMAN = "Times New Roman";

    public <E> ByteArrayInputStream export(
        List<E> obj,
        List<String> header,
        List<String> column,
        String title,
        List<Integer> lsSize,
        List<Integer> lsAlign,
        String... underHeader
    ) throws IOException, IllegalAccessException {
        return createWorkbook("Data", obj, header, column, title, lsSize, lsAlign, underHeader);
    }

    public <E> ByteArrayInputStream export(
        String sheetName,
        List<E> obj,
        List<String> header,
        List<String> column,
        String title,
        List<Integer> lsSize,
        List<Integer> lsAlign,
        String... underHeader
    ) throws IOException, IllegalAccessException {
        return createWorkbook(sheetName, obj, header, column, title, lsSize, lsAlign, underHeader);
    }

    public <E> ByteArrayInputStream createWorkbook(
        String sheetName,
        List<E> obj,
        List<String> header,
        List<String> column,
        String title,
        List<Integer> lsSize,
        List<Integer> lsAlign,
        String... underHeader
    ) throws IOException, IllegalAccessException {
        // 1. Set Title
        XSSFWorkbook myWorkbook = new XSSFWorkbook();
        XSSFSheet mySheet = myWorkbook.createSheet(sheetName);

        // Set column width
        for (int i = 0; i < header.size(); i++) {
            mySheet.setColumnWidth(1 + i, lsSize.get(i));
        }

        CellStyle styleTitle = myWorkbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setFillForegroundColor(IndexedColors.GREEN.index);
        styleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont fontTitle = myWorkbook.createFont();
        fontTitle.setFontHeightInPoints((short) 14);
        fontTitle.setBold(true);
        fontTitle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        fontTitle.setFontName(FONT_TIMES_NEW_ROMAN);
        styleTitle.setFont(fontTitle);

        XSSFFont fontHeader1 = myWorkbook.createFont();
        fontHeader1.setFontHeightInPoints((short) 12);
        fontHeader1.setFontName(FONT_TIMES_NEW_ROMAN);

        XSSFFont fontHeader = myWorkbook.createFont();
        fontHeader.setFontHeightInPoints((short) 12);
        fontHeader.setBold(true);
        fontHeader.setFontName(FONT_TIMES_NEW_ROMAN);


        XSSFCellStyle styleHeader = myWorkbook.createCellStyle();
        ExcelUtils.buildCellStyle(styleHeader);
        byte[] rgb = new byte[3];
        rgb[0] = (byte) 220;
        rgb[1] = (byte) 220;
        rgb[2] = (byte) 220;
        XSSFColor color = new XSSFColor(rgb, new DefaultIndexedColorMap());
        styleHeader.setFillForegroundColor(color);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setFont(fontHeader);
        styleHeader.setWrapText(true);

        CellStyle styleParam = myWorkbook.createCellStyle();
        styleParam.setAlignment(HorizontalAlignment.CENTER);
        styleParam.setFont(fontHeader);
        styleParam.setWrapText(true);

        CellStyle styleNormal = myWorkbook.createCellStyle();
        styleNormal.setBorderRight(BorderStyle.THIN);
        styleNormal.setBorderLeft(BorderStyle.THIN);
        styleNormal.setBorderBottom(BorderStyle.THIN);
        styleNormal.setBorderTop(BorderStyle.THIN);
        styleNormal.setVerticalAlignment(VerticalAlignment.CENTER);
        styleNormal.setWrapText(true);
        styleNormal.setFont(fontHeader1);

        CellStyle styleNumber = myWorkbook.createCellStyle();
        styleNumber.setAlignment(HorizontalAlignment.CENTER);
        styleNumber.setBorderRight(BorderStyle.THIN);
        styleNumber.setBorderLeft(BorderStyle.THIN);
        styleNumber.setBorderBottom(BorderStyle.THIN);
        styleNumber.setBorderTop(BorderStyle.THIN);
        styleNumber.setVerticalAlignment(VerticalAlignment.CENTER);
        styleNumber.setFont(fontHeader1);

        styleHeader.setWrapText(true);

        CellStyle styleNumberRight = myWorkbook.createCellStyle();
        styleNumberRight.setAlignment(HorizontalAlignment.RIGHT);
        styleNumberRight.setBorderRight(BorderStyle.THIN);
        styleNumberRight.setBorderLeft(BorderStyle.THIN);
        styleNumberRight.setBorderBottom(BorderStyle.THIN);
        styleNumberRight.setBorderTop(BorderStyle.THIN);
        styleNumberRight.setVerticalAlignment(VerticalAlignment.CENTER);
        styleNumberRight.setWrapText(true);
        styleNumberRight.setFont(fontHeader1);

        Row row = mySheet.createRow((short) 0);
        Cell cell = row.createCell((short) 0);
        cell.setCellValue(title);
        cell.setCellStyle(styleTitle);

        //Date export
        styleParam.setAlignment(HorizontalAlignment.CENTER);
        styleParam.setFont(fontHeader);
        styleParam.setWrapText(true);
        CellStyle dateExportStyle = myWorkbook.createCellStyle();
        dateExportStyle.setAlignment(HorizontalAlignment.CENTER);
        dateExportStyle.setFont(fontHeader);
        dateExportStyle.setWrapText(true);
        row = mySheet.createRow((short) 1);
        cell = row.createCell(0);
        cell.setCellStyle(dateExportStyle);
        cell.setCellValue(Translator.toLocale("common.dateExport") + ": " + LocalDate.now().format(DATE_FORMAT));

        mySheet.addMergedRegion(new CellRangeAddress(0, 0, 0, column.size()));
        mySheet.addMergedRegion(new CellRangeAddress(1, 1, 0, column.size()));

        //set param
        for (int i = 0; i < underHeader.length; i++) {
            Row rowP = mySheet.createRow((short) 1 + i);
            Cell cellP = rowP.createCell((short) 0);
            cellP.setCellValue(underHeader[i]);
            cellP.setCellStyle(styleParam);
            mySheet.addMergedRegion(new CellRangeAddress(2 + i, 2 + i, 0, column.size()));
        }

        // 2. Set Header
        // So thu tu
        row = mySheet.createRow((short) 2 + underHeader.length);
        setHeaderCell(header, styleHeader, row);

        // 3. Set detail
        for (int k = 0; k < obj.size(); k++) {
            row = mySheet.createRow((short) k + 3 + underHeader.length);

            // STT
            cell = row.createCell(0);
            int stt = k + 1;
            cell.setCellValue(stt);
            cell.setCellStyle(styleNumber);

            /////////////////////
            Object valueObj = obj.get(k);
            Class<?> c1 = valueObj.getClass();
            Field[] valueObjFields = c1.getDeclaredFields();

            for (int c = 0; c < column.size(); c++) {
                for (int i = 0; i < valueObjFields.length; i++) {
                    String fieldName = valueObjFields[i].getName();
                    if (fieldName.equals(column.get(c))) {
                        valueObjFields[i].setAccessible(true);
                        Object newObj = valueObjFields[i].get(valueObj);
                        try {
                            cell = row.createCell(c + 1);
                            cell.setCellValue(DataUtil.nvl(newObj, ""));
                        } catch (Exception ex) {
                            try {
                                BigDecimal d = new BigDecimal(newObj.toString());
                                cell = row.createCell(c + 1);
                                DecimalFormat df2 = new DecimalFormat("###,###,###,###.####");
                                cell.setCellValue(df2.format(d));
                            } catch (Exception e) {
                                cell = row.createCell(c + 1);
                                cell.setCellValue(DataUtil.nvl(newObj, ""));
                            }
                        }
                        if (lsAlign.get(c) == 0) {
                            cell.setCellStyle(styleNormal);
                        }
                        if (lsAlign.get(c) == 1) {
                            cell.setCellStyle(styleNumber);
                        }
                        if (lsAlign.get(c) == 2) {
                            cell.setCellStyle(styleNumberRight);
                        }

                        break;
                    }
                }
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        myWorkbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static void setHeaderCell(List<String> header, XSSFCellStyle styleHeader, Row row) {
        Cell cell;
        cell = row.createCell(0);
        cell.setCellValue("STT");
        cell.setCellStyle(styleHeader);
        for (int i = 0; i < header.size(); i++) {
            cell = row.createCell(i + 1);
            cell.setCellValue(header.get(i));
            cell.setCellStyle(styleHeader);
        }
    }
}
