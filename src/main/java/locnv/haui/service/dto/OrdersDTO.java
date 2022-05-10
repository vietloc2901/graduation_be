package locnv.haui.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link locnv.haui.domain.Orders} entity.
 */
public class OrdersDTO implements Serializable {

    private Long id;

    private Long userId;

    private String status;

    private String name;

    private String receiverName;

    private String phone;

    private String receiverPhone;

    private String email;

    private String address;

    private Float discount;

    private String note;

    private ZonedDateTime createDate;

    private String createBy;

    private ZonedDateTime lastModifiedDate;

    private String lastModifiedBy;

    private List<OrderItemsDTO> listItem;

    private BigDecimal sumPrice;

    private String createDateString;

    private String statusString;

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public String getCreateDateString() {
        return createDateString;
    }

    public void setCreateDateString(String createDateString) {
        this.createDateString = createDateString;
    }

    public List<OrderItemsDTO> getListItem() {
        return listItem;
    }

    public BigDecimal getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(BigDecimal sumPrice) {
        this.sumPrice = sumPrice;
    }

    public void setListItem(List<OrderItemsDTO> listItem) {
        this.listItem = listItem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
        if (!(o instanceof OrdersDTO)) {
            return false;
        }

        OrdersDTO ordersDTO = (OrdersDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ordersDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrdersDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", status='" + getStatus() + "'" +
            ", name='" + getName() + "'" +
            ", receiverName='" + getReceiverName() + "'" +
            ", phone='" + getPhone() + "'" +
            ", receiverPhone='" + getReceiverPhone() + "'" +
            ", email='" + getEmail() + "'" +
            ", address='" + getAddress() + "'" +
            ", discount=" + getDiscount() +
            ", note='" + getNote() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", createBy='" + getCreateBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            "}";
    }
}
