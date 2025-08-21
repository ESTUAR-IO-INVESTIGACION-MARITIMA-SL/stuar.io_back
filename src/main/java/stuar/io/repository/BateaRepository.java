package stuar.io.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stuar.io.domain.Batea;

/**
 * Spring Data JPA repository for the Batea entity.
 */
@Repository
public interface BateaRepository extends JpaRepository<Batea, Long> {
    default Optional<Batea> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Batea> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Batea> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select batea from Batea batea left join fetch batea.poligono", countQuery = "select count(batea) from Batea batea")
    Page<Batea> findAllWithToOneRelationships(Pageable pageable);

    @Query("select batea from Batea batea left join fetch batea.poligono")
    List<Batea> findAllWithToOneRelationships();

    @Query("select batea from Batea batea left join fetch batea.poligono where batea.id =:id")
    Optional<Batea> findOneWithToOneRelationships(@Param("id") Long id);
}
