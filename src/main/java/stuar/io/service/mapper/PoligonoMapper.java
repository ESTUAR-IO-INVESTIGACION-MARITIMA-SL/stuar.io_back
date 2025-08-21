package stuar.io.service.mapper;

import org.mapstruct.*;
import stuar.io.domain.Poligono;
import stuar.io.domain.Zona;
import stuar.io.service.dto.PoligonoDTO;
import stuar.io.service.dto.ZonaDTO;

/**
 * Mapper for the entity {@link Poligono} and its DTO {@link PoligonoDTO}.
 */
@Mapper(componentModel = "spring")
public interface PoligonoMapper extends EntityMapper<PoligonoDTO, Poligono> {
    @Mapping(target = "zona", source = "zona", qualifiedByName = "zonaName")
    PoligonoDTO toDto(Poligono s);

    @Named("zonaName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ZonaDTO toDtoZonaName(Zona zona);
}
