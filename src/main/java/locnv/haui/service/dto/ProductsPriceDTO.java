package locnv.haui.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link locnv.haui.domain.ProductsPrice} entity.
 */
public class ProductsPriceDTO implements Serializable {

    private Long id;

    private Long productId;

    private BigDecimal price;

    private ZonedDateTime applyDate;

    private String createBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ZonedDateTime getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(ZonedDateTime applyDate) {
        this.applyDate = applyDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductsPriceDTO)) {
            return false;
        }

        ProductsPriceDTO productsPriceDTO = (ProductsPriceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productsPriceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductsPriceDTO{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", price=" + getPrice() +
            ", applyDate='" + getApplyDate() + "'" +
            ", createBy='" + getCreateBy() + "'" +
            "}";
    }
}
