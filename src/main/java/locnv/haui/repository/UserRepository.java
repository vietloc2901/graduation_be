package locnv.haui.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import locnv.haui.domain.Authority;
import locnv.haui.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByLogin(String login);

    Optional<User> findOneByLoginIgnoreCase(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    @Query(value = "SELECT DISTINCT u.* FROM jhi_user u join jhi_user_authority a on u.id = a.user_id where (UPPER(u.login) like CONCAT('%',UPPER(?1),'%') " +
        " or UPPER(u.full_name) like CONCAT('%',UPPER(?1),'%')) and a.authority_name = UPPER(?2) and u.id not in " +
        "(SELECT DISTINCT u1.id from jhi_user u1 join jhi_user_authority ua1 on u1.id = ua1.user_id where ua1.authority_name = UPPER(?3)) limit ?4,?5" , nativeQuery = true)
    List<User> findAllByKey(String key, String role, String roleAdmin, int page, int pageSize);

    @Query(value = "SELECT count(*) from (SELECT DISTINCT u.* FROM jhi_user u join jhi_user_authority a on u.id = a.user_id where (UPPER(u.login) like CONCAT('%',UPPER(?1),'%') " +
        " or UPPER(u.full_name) like CONCAT('%',UPPER(?1),'%')) and a.authority_name = UPPER(?2) and u.id not in " +
        " (SELECT DISTINCT u1.id from jhi_user u1 join jhi_user_authority ua1 on u1.id = ua1.user_id where ua1.authority_name = UPPER(?3))) as t" , nativeQuery = true)
    Integer totalRecordAllUser(String key, String role, String roleAdmin);

}
