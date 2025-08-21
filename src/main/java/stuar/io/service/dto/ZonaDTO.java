package stuar.io.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link stuar.io.domain.Zona} entity.
 */
@Schema(description = "Top-level area containing many polygons and many zone points.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ZonaDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 140)
    @Schema(description = "Business name shown in UIs; unique within this context.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = 1000)
    @Schema(description = "Optional longer description.")
    private String description;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZonaDTO)) {
            return false;
        }

        ZonaDTO zonaDTO = (ZonaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, zonaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ZonaDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
