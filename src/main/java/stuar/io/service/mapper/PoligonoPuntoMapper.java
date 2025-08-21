package stuar.io.service.mapper;

import org.mapstruct.*;
import stuar.io.domain.Poligono;
import stuar.io.domain.PoligonoPunto;
import stuar.io.service.dto.PoligonoDTO;
import stuar.io.service.dto.PoligonoPuntoDTO;

/**
 * Mapper for the entity {@link PoligonoPunto} and its DTO {@link PoligonoPuntoDTO}.
 */
@Mapper(componentModel = "spring")
public interface PoligonoPuntoMapper extends EntityMapper<PoligonoPuntoDTO, PoligonoPunto> {
    @Mapping(target = "poligono", source = "poligono", qualifiedByName = "poligonoName")
    PoligonoPuntoDTO toDto(PoligonoPunto s);

    @Named("poligonoName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PoligonoDTO toDtoPoligonoName(Poligono poligono);
}
