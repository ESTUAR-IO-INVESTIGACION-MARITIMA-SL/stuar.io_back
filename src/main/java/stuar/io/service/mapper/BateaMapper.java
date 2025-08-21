package stuar.io.service.mapper;

import org.mapstruct.*;
import stuar.io.domain.Batea;
import stuar.io.domain.Poligono;
import stuar.io.service.dto.BateaDTO;
import stuar.io.service.dto.PoligonoDTO;

/**
 * Mapper for the entity {@link Batea} and its DTO {@link BateaDTO}.
 */
@Mapper(componentModel = "spring")
public interface BateaMapper extends EntityMapper<BateaDTO, Batea> {
    @Mapping(target = "poligono", source = "poligono", qualifiedByName = "poligonoName")
    BateaDTO toDto(Batea s);

    @Named("poligonoName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PoligonoDTO toDtoPoligonoName(Poligono poligono);
}
