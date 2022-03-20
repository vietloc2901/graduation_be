package locnv.haui.repository;

import locnv.haui.domain.CartItems;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the CartItems entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    List<CartItems> findAllByCartId(Long cartId);
}
