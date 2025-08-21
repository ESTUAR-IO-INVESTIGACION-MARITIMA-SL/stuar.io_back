package stuar.io.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Vertex for a Zone polygon (stores a single point with ordering).
 */
@Entity
@Table(name = "zona_punto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ZonaPunto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * X coordinate (longitude or projected X).
     */
    @NotNull
    @Column(name = "x", nullable = false)
    private Double x;

    /**
     * Y coordinate (latitude or projected Y).
     */
    @NotNull
    @Column(name = "y", nullable = false)
    private Double y;

    /**
     * Vertex order to reconstruct the polygon reliably (0-based).
     */
    @NotNull
    @Min(value = 0)
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "poligonos", "points" }, allowSetters = true)
    private Zona zona;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ZonaPunto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getX() {
        return this.x;
    }

    public ZonaPunto x(Double x) {
        this.setX(x);
        return this;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return this.y;
    }

    public ZonaPunto y(Double y) {
        this.setY(y);
        return this;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getOrderIndex() {
        return this.orderIndex;
    }

    public ZonaPunto orderIndex(Integer orderIndex) {
        this.setOrderIndex(orderIndex);
        return this;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Zona getZona() {
        return this.zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public ZonaPunto zona(Zona zona) {
        this.setZona(zona);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZonaPunto)) {
            return false;
        }
        return getId() != null && getId().equals(((ZonaPunto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ZonaPunto{" +
            "id=" + getId() +
            ", x=" + getX() +
            ", y=" + getY() +
            ", orderIndex=" + getOrderIndex() +
            "}";
    }
}
