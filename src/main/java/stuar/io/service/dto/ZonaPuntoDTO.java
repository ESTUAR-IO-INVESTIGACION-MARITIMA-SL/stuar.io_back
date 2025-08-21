package stuar.io.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link stuar.io.domain.ZonaPunto} entity.
 */
@Schema(description = "Vertex for a Zone polygon (stores a single point with ordering).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ZonaPuntoDTO implements Serializable {

    private Long id;

    @NotNull
    @Schema(description = "X coordinate (longitude or projected X).", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double x;

    @NotNull
    @Schema(description = "Y coordinate (latitude or projected Y).", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double y;

    @NotNull
    @Min(value = 0)
    @Schema(description = "Vertex order to reconstruct the polygon reliably (0-based).", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderIndex;

    @NotNull
    private ZonaDTO zona;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
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
        if (!(o instanceof ZonaPuntoDTO)) {
            return false;
        }

        ZonaPuntoDTO zonaPuntoDTO = (ZonaPuntoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, zonaPuntoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ZonaPuntoDTO{" +
            "id=" + getId() +
            ", x=" + getX() +
            ", y=" + getY() +
            ", orderIndex=" + getOrderIndex() +
            ", zona=" + getZona() +
            "}";
    }
}
