package locnv.haui.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ChartDTO implements Serializable {

    private String name;

    private String value;

    private BigDecimal valueRevenue;

    private Integer valueQuantity;

    public BigDecimal getValueRevenue() {
        return valueRevenue;
    }

    public void setValueRevenue(BigDecimal valueRevenue) {
        this.valueRevenue = valueRevenue;
    }

    public Integer getValueQuantity() {
        return valueQuantity;
    }

    public void setValueQuantity(Integer valueQuantity) {
        this.valueQuantity = valueQuantity;
    }


    public ChartDTO(){

    }

    public ChartDTO(String name, String value, BigDecimal valueRevenue, Integer valueQuantity) {
        this.name = name;
        this.value = value;
        this.valueRevenue = valueRevenue;
        this.valueQuantity = valueQuantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
