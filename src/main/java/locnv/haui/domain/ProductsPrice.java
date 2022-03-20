package locnv.haui.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.persistence.*;

/**
 * A ProductsPrice.
 */
@Entity
@Table(name = "product_prices")
public class ProductsPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "price", precision = 21, scale = 2)
    private BigDecimal price;

    @Column(name = "apply_date")
    private ZonedDateTime applyDate;

    @Column(name = "create_by")
    private String createBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductsPrice id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return this.productId;
    }

    public ProductsPrice productId(Long productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public ProductsPrice price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ZonedDateTime getApplyDate() {
        return this.applyDate;
    }

    public ProductsPrice applyDate(ZonedDateTime applyDate) {
        this.setApplyDate(applyDate);
        return this;
    }

    public void setApplyDate(ZonedDateTime applyDate) {
        this.applyDate = applyDate;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public ProductsPrice createBy(String createBy) {
        this.setCreateBy(createBy);
        return this;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductsPrice)) {
            return false;
        }
        return id != null && id.equals(((ProductsPrice) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductsPrice{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", price=" + getPrice() +
            ", applyDate='" + getApplyDate() + "'" +
            ", createBy='" + getCreateBy() + "'" +
            "}";
    }
}
