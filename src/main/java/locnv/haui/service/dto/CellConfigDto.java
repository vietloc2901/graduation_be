package locnv.haui.service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CellConfigDto {
	private String fieldName;
	private String textAlight = "LEFT";
	private String cellType = "STRING";
	private boolean hyperLinkEmail;
	private boolean formatNumber = Boolean.TRUE;
	private Integer row;
	private Integer colmn;
	private String cellValue;
	private int firstRow;
	private int lastRow;
	private int firstCol;
	private int lastCol;
	private String[] arrData;

	public CellConfigDto(String fieldName) {
		this.fieldName = fieldName;
	}

	public CellConfigDto(String fieldName, String textAlight, String cellType ) {
		super();
		this.fieldName = fieldName;
		this.textAlight = textAlight;
		this.cellType = cellType;
	}

    public CellConfigDto(String fieldName, String textAlight, String cellType, Boolean hyperLinkEmail ) {
        super();
        this.fieldName = fieldName;
        this.textAlight = textAlight;
        this.cellType = cellType;
        this.hyperLinkEmail = hyperLinkEmail;
    }

    public CellConfigDto(String fieldName, String textAlight, String[] arrData, int firstRow, int lastRow, int firstCol, int lastCol ) {
        super();
        this.fieldName = fieldName;
        this.textAlight = textAlight;
        this.arrData = arrData;
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.firstCol = firstCol;
        this.lastCol = lastCol;
    }

	public CellConfigDto(String fieldName, String textAlight, String cellType, boolean formatNumber ) {
		this.fieldName = fieldName;
		this.textAlight = textAlight;
		this.cellType = cellType;
		this.formatNumber = formatNumber;
	}

	public CellConfigDto(Integer row, Integer colmn, String textAlight, String cellValue, String cellType) {
		this.row = row;
		this.colmn = colmn;
		this.cellValue = cellValue;
		this.textAlight = textAlight;
		this.cellType = cellType;
	}

	public CellConfigDto(Integer row, Integer colmn, String textAlight, String cellValue, String cellType,
                         boolean formatNumber) {
		this.row = row;
		this.colmn = colmn;
		this.cellValue = cellValue;
		this.textAlight = textAlight;
		this.cellType = cellType;
		this.formatNumber = formatNumber;
	}

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getTextAlight() {
        return textAlight;
    }

    public void setTextAlight(String textAlight) {
        this.textAlight = textAlight;
    }

    public String getCellType() {
        return cellType;
    }

    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    public boolean isHyperLinkEmail() {
        return hyperLinkEmail;
    }

    public void setHyperLinkEmail(boolean hyperLinkEmail) {
        this.hyperLinkEmail = hyperLinkEmail;
    }

    public boolean isFormatNumber() {
        return formatNumber;
    }

    public void setFormatNumber(boolean formatNumber) {
        this.formatNumber = formatNumber;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColmn() {
        return colmn;
    }

    public void setColmn(Integer colmn) {
        this.colmn = colmn;
    }

    public String getCellValue() {
        return cellValue;
    }

    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public void setLastRow(int lastRow) {
        this.lastRow = lastRow;
    }

    public int getFirstCol() {
        return firstCol;
    }

    public void setFirstCol(int firstCol) {
        this.firstCol = firstCol;
    }

    public int getLastCol() {
        return lastCol;
    }

    public void setLastCol(int lastCol) {
        this.lastCol = lastCol;
    }

    public String[] getArrData() {
        return arrData;
    }

    public void setArrData(String[] arrData) {
        this.arrData = arrData;
    }
}
