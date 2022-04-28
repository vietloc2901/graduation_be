package locnv.haui.service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SheetConfigDto {
	private List<?> list;
	private String[] headers;
	private List<CellConfigDto> cellConfigList;
	private List<CellConfigDto> cellCustomList;
	private String sheetName;
	private boolean hasIndex = true;
	private int rowStart = 0;
	private boolean hasBorder;
	private int exportType;
    private List<DataDTO> dataDTOs;

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public List<CellConfigDto> getCellConfigList() {
        return cellConfigList;
    }

    public void setCellConfigList(List<CellConfigDto> cellConfigList) {
        this.cellConfigList = cellConfigList;
    }

    public List<CellConfigDto> getCellCustomList() {
        return cellCustomList;
    }

    public void setCellCustomList(List<CellConfigDto> cellCustomList) {
        this.cellCustomList = cellCustomList;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public boolean isHasIndex() {
        return hasIndex;
    }

    public void setHasIndex(boolean hasIndex) {
        this.hasIndex = hasIndex;
    }

    public int getRowStart() {
        return rowStart;
    }

    public void setRowStart(int rowStart) {
        this.rowStart = rowStart;
    }

    public boolean isHasBorder() {
        return hasBorder;
    }

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

    public int getExportType() {
        return exportType;
    }

    public void setExportType(int exportType) {
        this.exportType = exportType;
    }

    public List<DataDTO> getDataDTOs() {
        return dataDTOs;
    }

    public void setDataDTOs(List<DataDTO> dataDTOs) {
        this.dataDTOs = dataDTOs;
    }
}
