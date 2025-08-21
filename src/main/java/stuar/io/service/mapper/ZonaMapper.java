package stuar.io.service.mapper;

import org.mapstruct.*;
import stuar.io.domain.Zona;
import stuar.io.service.dto.ZonaDTO;

/**
 * Mapper for the entity {@link Zona} and its DTO {@link ZonaDTO}.
 */
@Mapper(componentModel = "spring")
public interface ZonaMapper extends EntityMapper<ZonaDTO, Zona> {}
