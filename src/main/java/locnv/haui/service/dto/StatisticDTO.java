package locnv.haui.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class StatisticDTO implements Serializable {

    private Integer totalOrders;

    private Integer totalCancelOrders;

    private Integer totalDoneOrders;

    private Integer totalWaitingOrders;

    private Integer totalTransferOrders;

    private Integer totalPreparingOrders;

    private BigDecimal revenue;

    List<ChartDTO> quantityPerProducts;

    List<ChartDTO> revenuePerProducts;

    private String monthForStatistic;

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getTotalCancelOrders() {
        return totalCancelOrders;
    }

    public void setTotalCancelOrders(Integer totalCancelOrders) {
        this.totalCancelOrders = totalCancelOrders;
    }

    public Integer getTotalDoneOrders() {
        return totalDoneOrders;
    }

    public void setTotalDoneOrders(Integer totalDoneOrders) {
        this.totalDoneOrders = totalDoneOrders;
    }

    public Integer getTotalWaitingOrders() {
        return totalWaitingOrders;
    }

    public void setTotalWaitingOrders(Integer totalWaitingOrders) {
        this.totalWaitingOrders = totalWaitingOrders;
    }

    public Integer getTotalTransferOrders() {
        return totalTransferOrders;
    }

    public void setTotalTransferOrders(Integer totalTransferOrders) {
        this.totalTransferOrders = totalTransferOrders;
    }

    public Integer getTotalPreparingOrders() {
        return totalPreparingOrders;
    }

    public void setTotalPreparingOrders(Integer totalPreparingOrders) {
        this.totalPreparingOrders = totalPreparingOrders;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public List<ChartDTO> getQuantityPerProducts() {
        return quantityPerProducts;
    }

    public void setQuantityPerProducts(List<ChartDTO> quantityPerProducts) {
        this.quantityPerProducts = quantityPerProducts;
    }

    public List<ChartDTO> getRevenuePerProducts() {
        return revenuePerProducts;
    }

    public void setRevenuePerProducts(List<ChartDTO> revenuePerProducts) {
        this.revenuePerProducts = revenuePerProducts;
    }

    public String getMonthForStatistic() {
        return monthForStatistic;
    }

    public void setMonthForStatistic(String monthForStatistic) {
        this.monthForStatistic = monthForStatistic;
    }
}
