package locnv.haui.repository;

import locnv.haui.domain.ProductsPrice;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProductsPrice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductsPriceRepository extends JpaRepository<ProductsPrice, Long> {}
