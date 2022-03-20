package locnv.haui.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;

/**
 * A Products.
 */
@Entity
@Table(name = "products")
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "create_date")
    private ZonedDateTime createDate;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "last_modified_date")
    private ZonedDateTime lastModifiedDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "image")
    private String image;

    @Column(name = "detail_images")
    private String detailImages;

    @Column(name = "brand")
    private String brand;

    @Column(name = "product_details")
    private String productDetails;

    @Column(name = "description_document")
    private String descriptionDocument;

    @Column(name = "video")
    private String video;

    @Column(name = "catalog_id")
    private Long catalogId;

    @Column(name = "status")
    private Boolean status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getId() {
        return this.id;
    }

    public Products id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Products code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Products name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getCreateDate() {
        return this.createDate;
    }

    public Products createDate(ZonedDateTime createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public Products createBy(String createBy) {
        this.setCreateBy(createBy);
        return this;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public ZonedDateTime getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Products lastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Products lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getImage() {
        return this.image;
    }

    public Products image(String image) {
        this.setImage(image);
        return this;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDetailImages() {
        return this.detailImages;
    }

    public Products detailImages(String detailImages) {
        this.setDetailImages(detailImages);
        return this;
    }

    public void setDetailImages(String detailImages) {
        this.detailImages = detailImages;
    }

    public String getBrand() {
        return this.brand;
    }

    public Products brand(String brand) {
        this.setBrand(brand);
        return this;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProductDetails() {
        return this.productDetails;
    }

    public Products productDetails(String productDetails) {
        this.setProductDetails(productDetails);
        return this;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public String getDescriptionDocument() {
        return this.descriptionDocument;
    }

    public Products descriptionDocument(String descriptionDocument) {
        this.setDescriptionDocument(descriptionDocument);
        return this;
    }

    public void setDescriptionDocument(String descriptionDocument) {
        this.descriptionDocument = descriptionDocument;
    }

    public String getVideo() {
        return this.video;
    }

    public Products video(String video) {
        this.setVideo(video);
        return this;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Long getCatalogId() {
        return this.catalogId;
    }

    public Products catalogId(Long catalogId) {
        this.setCatalogId(catalogId);
        return this;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Products)) {
            return false;
        }
        return id != null && id.equals(((Products) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Products{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", createBy='" + getCreateBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", image='" + getImage() + "'" +
            ", detailImages='" + getDetailImages() + "'" +
            ", brand='" + getBrand() + "'" +
            ", productDetails='" + getProductDetails() + "'" +
            ", descriptionDocument='" + getDescriptionDocument() + "'" +
            ", video='" + getVideo() + "'" +
            ", catalogId=" + getCatalogId() +
            "}";
    }
}
