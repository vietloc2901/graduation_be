package locnv.haui.repository;

import locnv.haui.domain.ProductSpecs;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProductSpecs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductSpecsRepository extends JpaRepository<ProductSpecs, Long> {}
