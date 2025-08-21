package stuar.io.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link stuar.io.domain.Poligono} entity.
 */
@Schema(description = "Polygon belonging to a single zone; contains many rafts and many polygon points.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoligonoDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 140)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull
    private ZonaDTO zona;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonaDTO getZona() {
        return zona;
    }

    public void setZona(ZonaDTO zona) {
        this.zona = zona;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PoligonoDTO)) {
            return false;
        }

        PoligonoDTO poligonoDTO = (PoligonoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, poligonoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PoligonoDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", zona=" + getZona() +
            "}";
    }
}
