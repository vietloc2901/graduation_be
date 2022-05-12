package locnv.haui.repository;

import locnv.haui.service.dto.ChartDTO;
import locnv.haui.service.dto.StatisticDTO;

import java.time.ZonedDateTime;
import java.util.List;

public interface StatisticRepository {

    List<ChartDTO> quantityPerProducts(ZonedDateTime startDate, ZonedDateTime endDate);

    List<ChartDTO> revenuePerProducts(ZonedDateTime startDate, ZonedDateTime endDate);
}
