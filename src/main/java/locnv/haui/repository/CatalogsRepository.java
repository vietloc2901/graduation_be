package locnv.haui.repository;

import locnv.haui.domain.Catalogs;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Catalogs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CatalogsRepository extends JpaRepository<Catalogs, Long> {}
