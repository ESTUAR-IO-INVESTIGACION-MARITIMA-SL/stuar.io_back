package stuar.io.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Single raft (batea) located at a single (x,y) coordinate, inside one polygon.
 */
@Entity
@Table(name = "batea")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Batea implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 140)
    @Column(name = "name", length = 140, nullable = false, unique = true)
    private String name;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Owner's display name (not linked to User for now).
     */
    @NotNull
    @Size(min = 2, max = 140)
    @Column(name = "owner_name", length = 140, nullable = false)
    private String ownerName;

    /**
     * Coordinates for the single point location.
     */
    @NotNull
    @Column(name = "x", nullable = false)
    private Double x;

    @NotNull
    @Column(name = "y", nullable = false)
    private Double y;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "bateas", "points", "zona" }, allowSetters = true)
    private Poligono poligono;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Batea id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Batea name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Batea description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public Batea ownerName(String ownerName) {
        this.setOwnerName(ownerName);
        return this;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Double getX() {
        return this.x;
    }

    public Batea x(Double x) {
        this.setX(x);
        return this;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return this.y;
    }

    public Batea y(Double y) {
        this.setY(y);
        return this;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Poligono getPoligono() {
        return this.poligono;
    }

    public void setPoligono(Poligono poligono) {
        this.poligono = poligono;
    }

    public Batea poligono(Poligono poligono) {
        this.setPoligono(poligono);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Batea)) {
            return false;
        }
        return getId() != null && getId().equals(((Batea) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Batea{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", ownerName='" + getOwnerName() + "'" +
            ", x=" + getX() +
            ", y=" + getY() +
            "}";
    }
}
