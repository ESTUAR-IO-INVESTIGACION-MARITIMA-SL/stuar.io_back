package stuar.io.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Top-level area containing many polygons and many zone points.
 */
@Entity
@Table(name = "zona")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Zona implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Business name shown in UIs; unique within this context.
     */
    @NotNull
    @Size(min = 2, max = 140)
    @Column(name = "name", length = 140, nullable = false, unique = true)
    private String name;

    /**
     * Optional longer description.
     */
    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "zona")
    @JsonIgnoreProperties(value = { "bateas", "points", "zona" }, allowSetters = true)
    private Set<Poligono> poligonos = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "zona")
    @JsonIgnoreProperties(value = { "zona" }, allowSetters = true)
    private Set<ZonaPunto> points = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Zona id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Zona name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Zona description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Poligono> getPoligonos() {
        return this.poligonos;
    }

    public void setPoligonos(Set<Poligono> poligonos) {
        if (this.poligonos != null) {
            this.poligonos.forEach(i -> i.setZona(null));
        }
        if (poligonos != null) {
            poligonos.forEach(i -> i.setZona(this));
        }
        this.poligonos = poligonos;
    }

    public Zona poligonos(Set<Poligono> poligonos) {
        this.setPoligonos(poligonos);
        return this;
    }

    public Zona addPoligonos(Poligono poligono) {
        this.poligonos.add(poligono);
        poligono.setZona(this);
        return this;
    }

    public Zona removePoligonos(Poligono poligono) {
        this.poligonos.remove(poligono);
        poligono.setZona(null);
        return this;
    }

    public Set<ZonaPunto> getPoints() {
        return this.points;
    }

    public void setPoints(Set<ZonaPunto> zonaPuntos) {
        if (this.points != null) {
            this.points.forEach(i -> i.setZona(null));
        }
        if (zonaPuntos != null) {
            zonaPuntos.forEach(i -> i.setZona(this));
        }
        this.points = zonaPuntos;
    }

    public Zona points(Set<ZonaPunto> zonaPuntos) {
        this.setPoints(zonaPuntos);
        return this;
    }

    public Zona addPoints(ZonaPunto zonaPunto) {
        this.points.add(zonaPunto);
        zonaPunto.setZona(this);
        return this;
    }

    public Zona removePoints(ZonaPunto zonaPunto) {
        this.points.remove(zonaPunto);
        zonaPunto.setZona(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Zona)) {
            return false;
        }
        return getId() != null && getId().equals(((Zona) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Zona{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
