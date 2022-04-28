package locnv.haui.repository.impl;

import locnv.haui.repository.OrdersCustomRepository;
import locnv.haui.service.dto.OrderItemsDTO;
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
        StringBuilder sql = new StringBuilder("SELECT o.id, o.name, o.phone, o.user_id userId, o.receiver_name receiverName, o.receiver_phone receiverPhone, o.status, ");
        sql.append("o.last_modified_date lastModifiedDate, o.last_modified_by lastModifiedBy, o.email, o.address, o.discount, o.note, o.create_date createDate, sum(oi.price * oi.quantity) as sumPrice ");
        sql.append(" from orders o left join order_items oi on o.id = oi.order_id where true ");
        if(Objects.nonNull(ordersDTO.getStatus())){
            sql.append(" and o.status =:status ");
        }
        if(Objects.nonNull(ordersDTO.getCreateDateString())){
            sql.append(" and o.create_date like :createDate ");
        }
        if(Objects.nonNull(ordersDTO.getId())){
            sql.append(" and o.id = :id ");
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
            .addScalar("status", new StringType())
            .setResultTransformer(Transformers.aliasToBean(OrdersDTO.class));
        if(Objects.nonNull(ordersDTO.getStatus())){
            query.setParameter("status", ordersDTO.getStatus());
        }
        if(Objects.nonNull(ordersDTO.getCreateDateString())){
            query.setParameter("createDate", "%" + ordersDTO.getCreateDateString() + "%");
        }
        if(Objects.nonNull(ordersDTO.getId())){
            query.setParameter("id", ordersDTO.getId());
        }
        List<OrdersDTO> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<OrderItemsDTO> getByOrderId(Long id) {
        StringBuilder sql = new StringBuilder("SELECT oi.price price, oi.product_id productId, oi.quantity quantity, oi.id, o.user_id userId, o.status status, o.name name, o.receiver_name receiverName, ");
        sql.append(" o.email email, o.address address, o.discount discount, o.note note, o.create_date createDate, o.create_by createBy, ");
        sql.append(" o.last_modified_date lastModifiedDate, o.last_modified_by lastModifiedBy, p.name productName, p.image productImage ");
        sql.append(" from order_items oi join orders o on oi.order_id = o.id join products p on oi.product_id = p.id where o.id = :id" );

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        query
            .addScalar("id", new LongType())
            .addScalar("price", new BigDecimalType())
            .addScalar("quantity", new IntegerType())
            .addScalar("productId", new LongType())
            .addScalar("productName", new StringType())
            .addScalar("productImage", new StringType())
            .setResultTransformer(Transformers.aliasToBean(OrderItemsDTO.class));
        query.setParameter("id",id);
        List<OrderItemsDTO> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public BigInteger totalRecord(OrdersDTO ordersDTO) {
        StringBuilder sql = new StringBuilder("SELECT count(*) from orders o where true ");
        if(Objects.nonNull(ordersDTO.getStatus())){
            sql.append(" and o.status =:status ");
        }
        if(Objects.nonNull(ordersDTO.getCreateDateString())){
            sql.append(" and o.create_date like :createDate ");
        }

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        if(Objects.nonNull(ordersDTO.getStatus())){
            query.setParameter("status", ordersDTO.getStatus());
        }
        if(Objects.nonNull(ordersDTO.getCreateDateString())){
            query.setParameter("createDate", "%" + ordersDTO.getCreateDateString() + "%");
        }
        return (BigInteger) query.uniqueResult();
    }
}
