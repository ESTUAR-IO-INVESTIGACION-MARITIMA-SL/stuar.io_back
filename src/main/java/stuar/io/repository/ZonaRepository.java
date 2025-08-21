package stuar.io.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import stuar.io.domain.Zona;

/**
 * Spring Data JPA repository for the Zona entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ZonaRepository extends JpaRepository<Zona, Long> {}
