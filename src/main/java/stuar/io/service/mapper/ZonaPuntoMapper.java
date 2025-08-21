package stuar.io.service.mapper;

import org.mapstruct.*;
import stuar.io.domain.Zona;
import stuar.io.domain.ZonaPunto;
import stuar.io.service.dto.ZonaDTO;
import stuar.io.service.dto.ZonaPuntoDTO;

/**
 * Mapper for the entity {@link ZonaPunto} and its DTO {@link ZonaPuntoDTO}.
 */
@Mapper(componentModel = "spring")
public interface ZonaPuntoMapper extends EntityMapper<ZonaPuntoDTO, ZonaPunto> {
    @Mapping(target = "zona", source = "zona", qualifiedByName = "zonaName")
    ZonaPuntoDTO toDto(ZonaPunto s);

    @Named("zonaName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ZonaDTO toDtoZonaName(Zona zona);
}
