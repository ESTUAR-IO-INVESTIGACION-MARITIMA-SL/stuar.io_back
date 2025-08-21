package stuar.io.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link stuar.io.domain.PoligonoPunto} entity.
 */
@Schema(description = "Vertex for a Polygon (stores a single point with ordering).")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoligonoPuntoDTO implements Serializable {

    private Long id;

    @NotNull
    private Double x;

    @NotNull
    private Double y;

    @NotNull
    @Min(value = 0)
    private Integer orderIndex;

    @NotNull
    private PoligonoDTO poligono;

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
        if (!(o instanceof PoligonoPuntoDTO)) {
            return false;
        }

        PoligonoPuntoDTO poligonoPuntoDTO = (PoligonoPuntoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, poligonoPuntoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PoligonoPuntoDTO{" +
            "id=" + getId() +
            ", x=" + getX() +
            ", y=" + getY() +
            ", orderIndex=" + getOrderIndex() +
            ", poligono=" + getPoligono() +
            "}";
    }
}
