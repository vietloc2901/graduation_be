package locnv.haui.repository;

import locnv.haui.domain.Products;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the Products entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {
    List<Products> findAllByCatalogId(Long catalogId);

    Products findByCode(String code);
}
