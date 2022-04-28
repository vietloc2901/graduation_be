package locnv.haui.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class ExportDTO implements Serializable {
    private Integer recordNo;

    private String messageStr;

    private List<String> fieldErr = new ArrayList<>();

    private List<String> messageErr = new ArrayList<>();

    public Integer getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(Integer recordNo) {
        this.recordNo = recordNo;
    }

    public String getMessageStr() {
        return messageStr;
    }

    public void setMessageStr(String messageStr) {
        this.messageStr = messageStr;
    }

    public List<String> getFieldErr() {
        return fieldErr;
    }

    public void setFieldErr(List<String> fieldErr) {
        this.fieldErr = fieldErr;
    }

    public List<String> getMessageErr() {
        return messageErr;
    }

    public void setMessageErr(List<String> messageErr) {
        this.messageErr = messageErr;
    }
}
