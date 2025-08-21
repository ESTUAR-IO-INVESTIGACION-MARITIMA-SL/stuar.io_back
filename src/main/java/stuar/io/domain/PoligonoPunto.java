package stuar.io.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Vertex for a Polygon (stores a single point with ordering).
 */
@Entity
@Table(name = "poligono_punto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PoligonoPunto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "x", nullable = false)
    private Double x;

    @NotNull
    @Column(name = "y", nullable = false)
    private Double y;

    @NotNull
    @Min(value = 0)
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bateas", "points", "zona" }, allowSetters = true)
    private Poligono poligono;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PoligonoPunto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getX() {
        return this.x;
    }

    public PoligonoPunto x(Double x) {
        this.setX(x);
        return this;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return this.y;
    }

    public PoligonoPunto y(Double y) {
        this.setY(y);
        return this;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Integer getOrderIndex() {
        return this.orderIndex;
    }

    public PoligonoPunto orderIndex(Integer orderIndex) {
        this.setOrderIndex(orderIndex);
        return this;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Poligono getPoligono() {
        return this.poligono;
    }

    public void setPoligono(Poligono poligono) {
        this.poligono = poligono;
    }

    public PoligonoPunto poligono(Poligono poligono) {
        this.setPoligono(poligono);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PoligonoPunto)) {
            return false;
        }
        return getId() != null && getId().equals(((PoligonoPunto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PoligonoPunto{" +
            "id=" + getId() +
            ", x=" + getX() +
            ", y=" + getY() +
            ", orderIndex=" + getOrderIndex() +
            "}";
    }
}
