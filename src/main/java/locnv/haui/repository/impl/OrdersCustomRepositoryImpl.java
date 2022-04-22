package locnv.haui.repository.impl;

import locnv.haui.repository.OrdersCustomRepository;
import locnv.haui.service.dto.OrdersDTO;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Repository
public class OrdersCustomRepositoryImpl implements OrdersCustomRepository {

    private final EntityManager entityManager;

    public OrdersCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<OrdersDTO> search(OrdersDTO ordersDTO, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT o.id, o.name, o.phone, o.user_id userId, o.receiver_name receiverName, o.receiver_phone receiverPhone, ");
        sql.append("o.last_modified_date lastModifiedDate, o.last_modified_by lastModifiedBy, o.email, o.address, o.discount, o.note, o.create_date createDate, sum(oi.price * oi.quantity) as sumPrice ");
        sql.append(" from orders o left join order_items oi on o.id = oi.order_id where true ");
        if(Objects.nonNull(ordersDTO.getStatus())){
            sql.append(" and o.status =:status ");
        }
        if(Objects.nonNull(ordersDTO.getCreateDate())){
            sql.append(" and o.create_date =:createDate ");
        }
        sql.append(" group by o.id order by o.create_date  ");
        if(page != 0 && pageSize != 0){
            int offset;
            if(page <= 1){
                offset = 0;
            }else{
                offset = (page -1) *pageSize;
            }
            sql.append(" limit ").append(offset).append(",").append(pageSize).append(" ");
        }

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        query
            .addScalar("id", new LongType())
            .addScalar("userId", new LongType())
            .addScalar("name", new StringType())
            .addScalar("phone", new StringType())
            .addScalar("receiverName", new StringType())
            .addScalar("receiverPhone", new StringType())
            .addScalar("email", new StringType())
            .addScalar("address", new StringType())
            .addScalar("discount", new FloatType())
            .addScalar("note", new StringType())
            .addScalar("createDate", new ZonedDateTimeType())
            .addScalar("sumPrice", new BigDecimalType())
            .addScalar("lastModifiedDate", new ZonedDateTimeType())
            .addScalar("lastModifiedBy", new StringType())
            .setResultTransformer(Transformers.aliasToBean(OrdersDTO.class));
        if(Objects.nonNull(ordersDTO.getStatus())){
            query.setParameter("status", ordersDTO.getStatus());
        }
        if(Objects.nonNull(ordersDTO.getCreateDate())){
            query.setParameter("createDate", ordersDTO.getCreateDate());
        }
        List<OrdersDTO> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public BigInteger totalRecord(OrdersDTO ordersDTO) {
        StringBuilder sql = new StringBuilder("SELECT count(*) from orders o where true ");
        if(Objects.nonNull(ordersDTO.getStatus())){
            sql.append(" and o.status =:status ");
        }
        if(Objects.nonNull(ordersDTO.getCreateDate())){
            sql.append(" and o.create_date =:createDate ");
        }

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        if(Objects.nonNull(ordersDTO.getStatus())){
            query.setParameter("status", ordersDTO.getStatus());
        }
        if(Objects.nonNull(ordersDTO.getCreateDate())){
            query.setParameter("createDate", ordersDTO.getCreateDate());
        }
        return (BigInteger) query.uniqueResult();
    }
}
