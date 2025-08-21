package stuar.io.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Polygon belonging to a single zone; contains many rafts and many polygon points.
 */
@Entity
@Table(name = "poligono")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Poligono implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poligono")
    @JsonIgnoreProperties(value = { "poligono" }, allowSetters = true)
    private Set<Batea> bateas = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poligono")
    @JsonIgnoreProperties(value = { "poligono" }, allowSetters = true)
    private Set<PoligonoPunto> points = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "poligonos", "points" }, allowSetters = true)
    private Zona zona;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Poligono id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Poligono name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Poligono description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Batea> getBateas() {
        return this.bateas;
    }

    public void setBateas(Set<Batea> bateas) {
        if (this.bateas != null) {
            this.bateas.forEach(i -> i.setPoligono(null));
        }
        if (bateas != null) {
            bateas.forEach(i -> i.setPoligono(this));
        }
        this.bateas = bateas;
    }

    public Poligono bateas(Set<Batea> bateas) {
        this.setBateas(bateas);
        return this;
    }

    public Poligono addBateas(Batea batea) {
        this.bateas.add(batea);
        batea.setPoligono(this);
        return this;
    }

    public Poligono removeBateas(Batea batea) {
        this.bateas.remove(batea);
        batea.setPoligono(null);
        return this;
    }

    public Set<PoligonoPunto> getPoints() {
        return this.points;
    }

    public void setPoints(Set<PoligonoPunto> poligonoPuntos) {
        if (this.points != null) {
            this.points.forEach(i -> i.setPoligono(null));
        }
        if (poligonoPuntos != null) {
            poligonoPuntos.forEach(i -> i.setPoligono(this));
        }
        this.points = poligonoPuntos;
    }

    public Poligono points(Set<PoligonoPunto> poligonoPuntos) {
        this.setPoints(poligonoPuntos);
        return this;
    }

    public Poligono addPoints(PoligonoPunto poligonoPunto) {
        this.points.add(poligonoPunto);
        poligonoPunto.setPoligono(this);
        return this;
    }

    public Poligono removePoints(PoligonoPunto poligonoPunto) {
        this.points.remove(poligonoPunto);
        poligonoPunto.setPoligono(null);
        return this;
    }

    public Zona getZona() {
        return this.zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public Poligono zona(Zona zona) {
        this.setZona(zona);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Poligono)) {
            return false;
        }
        return getId() != null && getId().equals(((Poligono) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Poligono{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
