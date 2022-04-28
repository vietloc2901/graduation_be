package locnv.haui.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link locnv.haui.domain.Catalogs} entity.
 */
public class CatalogsDTO extends ExportDTO implements Serializable {

    private Long id;

    private String code;

    private String name;

    private Integer sortOrder;

    private Long parentId;

    private ZonedDateTime createDate;

    private String createBy;

    private ZonedDateTime lastModifiedDate;

    private String lastModifiedBy;

    private String parentCode;

    private String parentName;

    private String createDateString;

    private String lastModifiedDateString;

    private String filePathError;

    private transient List<ExcelDynamicDTO> listError;

    private List<Integer> lineSuccess;

    private Long totalSuccess;

    private Long totalFail;

    public Long getTotalSuccess() {
        return totalSuccess;
    }

    public void setTotalSuccess(Long totalSuccess) {
        this.totalSuccess = totalSuccess;
    }

    public Long getTotalFail() {
        return totalFail;
    }

    public void setTotalFail(Long totalFail) {
        this.totalFail = totalFail;
    }

    public String getFilePathError() {
        return filePathError;
    }

    public void setFilePathError(String filePathError) {
        this.filePathError = filePathError;
    }

    public List<ExcelDynamicDTO> getListError() {
        return listError;
    }

    public void setListError(List<ExcelDynamicDTO> listError) {
        this.listError = listError;
    }

    public List<Integer> getLineSuccess() {
        return lineSuccess;
    }

    public void setLineSuccess(List<Integer> lineSuccess) {
        this.lineSuccess = lineSuccess;
    }

    public String getCreateDateString() {
        return createDateString;
    }

    public void setCreateDateString(String createDateString) {
        this.createDateString = createDateString;
    }

    public String getLastModifiedDateString() {
        return lastModifiedDateString;
    }

    public void setLastModifiedDateString(String lastModifiedDateString) {
        this.lastModifiedDateString = lastModifiedDateString;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    private List<CatalogsDTO> children;

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public List<CatalogsDTO> getChildren() {
        return children;
    }

    public void setChildren(List<CatalogsDTO> children) {
        this.children = children;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CatalogsDTO)) {
            return false;
        }

        CatalogsDTO catalogsDTO = (CatalogsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, catalogsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public CatalogsDTO(){

    }

    public CatalogsDTO(Integer recordNo, String code, String name, Integer sortOrder, String parentCode){
        this.setRecordNo(recordNo);
        this.code = code;
        this.name = name;
        this.parentCode = parentCode;
        this.sortOrder = sortOrder;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CatalogsDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", parentId=" + getParentId() +
            ", createDate='" + getCreateDate() + "'" +
            ", createBy='" + getCreateBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            "}";
    }
}
