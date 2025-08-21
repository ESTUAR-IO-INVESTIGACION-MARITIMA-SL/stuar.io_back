package stuar.io.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stuar.io.domain.PoligonoPunto;

/**
 * Spring Data JPA repository for the PoligonoPunto entity.
 */
@Repository
public interface PoligonoPuntoRepository extends JpaRepository<PoligonoPunto, Long> {
    default Optional<PoligonoPunto> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PoligonoPunto> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PoligonoPunto> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select poligonoPunto from PoligonoPunto poligonoPunto left join fetch poligonoPunto.poligono",
        countQuery = "select count(poligonoPunto) from PoligonoPunto poligonoPunto"
    )
    Page<PoligonoPunto> findAllWithToOneRelationships(Pageable pageable);

    @Query("select poligonoPunto from PoligonoPunto poligonoPunto left join fetch poligonoPunto.poligono")
    List<PoligonoPunto> findAllWithToOneRelationships();

    @Query("select poligonoPunto from PoligonoPunto poligonoPunto left join fetch poligonoPunto.poligono where poligonoPunto.id =:id")
    Optional<PoligonoPunto> findOneWithToOneRelationships(@Param("id") Long id);
}
