package stuar.io.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stuar.io.domain.ZonaPunto;

/**
 * Spring Data JPA repository for the ZonaPunto entity.
 */
@Repository
public interface ZonaPuntoRepository extends JpaRepository<ZonaPunto, Long> {
    default Optional<ZonaPunto> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ZonaPunto> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ZonaPunto> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select zonaPunto from ZonaPunto zonaPunto left join fetch zonaPunto.zona",
        countQuery = "select count(zonaPunto) from ZonaPunto zonaPunto"
    )
    Page<ZonaPunto> findAllWithToOneRelationships(Pageable pageable);

    @Query("select zonaPunto from ZonaPunto zonaPunto left join fetch zonaPunto.zona")
    List<ZonaPunto> findAllWithToOneRelationships();

    @Query("select zonaPunto from ZonaPunto zonaPunto left join fetch zonaPunto.zona where zonaPunto.id =:id")
    Optional<ZonaPunto> findOneWithToOneRelationships(@Param("id") Long id);
}
