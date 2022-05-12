package locnv.haui.repository;

import locnv.haui.domain.Orders;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.ZonedDateTime;

/**
 * Spring Data SQL repository for the Orders entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query(value = "SELECT count(*) from orders  o where upper(o.status) = Upper(?1) and o.create_date between ?2 and ?3", nativeQuery = true)
    Integer getCountOrdersByStatus(String status, ZonedDateTime start, ZonedDateTime end);

    @Query(value = "SELECT count(*) from orders  o where o.create_date between ?1 and ?2", nativeQuery = true)
    Integer getCountOrders(ZonedDateTime start, ZonedDateTime end);

}
