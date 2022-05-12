package locnv.haui.repository.impl;

import locnv.haui.repository.StatisticRepository;
import locnv.haui.service.dto.ChartDTO;
import locnv.haui.service.dto.OrdersDTO;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class StatisticRepositoryImpl implements StatisticRepository {

    private final EntityManager entityManager;

    public StatisticRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ChartDTO> quantityPerProducts(ZonedDateTime startDate, ZonedDateTime endDate) {
        String sql = "select p.id, p.code as name, COALESCE(sum(oi.quantity), 0) as valueQuantity from order_items oi join orders o on oi.order_id = o.id right join products p on p.id = oi.product_id\n" +
            " and o.create_date between :startDate and :endDate " +
            " group by p.id, p.code";

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql);

        query
            .addScalar("name", new StringType())
            .addScalar("valueQuantity", new IntegerType())
            .setResultTransformer(Transformers.aliasToBean(ChartDTO.class));

        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<ChartDTO> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<ChartDTO> revenuePerProducts(ZonedDateTime startDate, ZonedDateTime endDate) {
        String sql = "select p.id, p.code as name, COALESCE(sum(oi.quantity * oi.price), 0) as valueRevenue from order_items oi join orders o on oi.order_id = o.id right join products p on p.id = oi.product_id\n" +
            " and o.create_date between :startDate and :endDate " +
            " group by p.id, p.code";

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql);

        query
            .addScalar("name", new StringType())
            .addScalar("valueRevenue", new BigDecimalType())
            .setResultTransformer(Transformers.aliasToBean(ChartDTO.class));

        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<ChartDTO> resultList = query.getResultList();
        return resultList;
    }
}
