package stuar.io.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link stuar.io.domain.Batea} entity.
 */
@Schema(description = "Single raft (batea) located at a single (x,y) coordinate, inside one polygon.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BateaDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 140)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull
    @Size(min = 2, max = 140)
    @Schema(description = "Owner's display name (not linked to User for now).", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ownerName;

    @NotNull
    @Schema(description = "Coordinates for the single point location.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double x;

    @NotNull
    private Double y;

    @NotNull
    private PoligonoDTO poligono;

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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public PoligonoDTO getPoligono() {
        return poligono;
    }

    public void setPoligono(PoligonoDTO poligono) {
        this.poligono = poligono;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BateaDTO)) {
            return false;
        }

        BateaDTO bateaDTO = (BateaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bateaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BateaDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", ownerName='" + getOwnerName() + "'" +
            ", x=" + getX() +
            ", y=" + getY() +
            ", poligono=" + getPoligono() +
            "}";
    }
}
