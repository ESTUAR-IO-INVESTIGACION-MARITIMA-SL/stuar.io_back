package stuar.io.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import stuar.io.domain.Poligono;

/**
 * Spring Data JPA repository for the Poligono entity.
 */
@Repository
public interface PoligonoRepository extends JpaRepository<Poligono, Long> {
    default Optional<Poligono> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Poligono> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Poligono> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select poligono from Poligono poligono left join fetch poligono.zona",
        countQuery = "select count(poligono) from Poligono poligono"
    )
    Page<Poligono> findAllWithToOneRelationships(Pageable pageable);

    @Query("select poligono from Poligono poligono left join fetch poligono.zona")
    List<Poligono> findAllWithToOneRelationships();

    @Query("select poligono from Poligono poligono left join fetch poligono.zona where poligono.id =:id")
    Optional<Poligono> findOneWithToOneRelationships(@Param("id") Long id);
}
