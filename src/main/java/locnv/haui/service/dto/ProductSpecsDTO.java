package locnv.haui.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link locnv.haui.domain.ProductSpecs} entity.
 */
public class ProductSpecsDTO implements Serializable {

    private Long id;

    private String key;

    private String value;

    private Long productId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductSpecsDTO)) {
            return false;
        }

        ProductSpecsDTO productSpecsDTO = (ProductSpecsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productSpecsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductSpecsDTO{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", value='" + getValue() + "'" +
            ", productId=" + getProductId() +
            "}";
    }
}
