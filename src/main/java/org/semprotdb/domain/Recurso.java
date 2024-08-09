package org.semprotdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.semprotdb.domain.enumeration.BioDB;

/**
 * A Recurso.
 */
@Entity
@Table(name = "recurso")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Recurso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "uid", nullable = false)
    private String uid;

    @Enumerated(EnumType.STRING)
    @Column(name = "db")
    private BioDB db;

    @Column(name = "link")
    private String link;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "recursos")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "curadoria", "versao", "gene", "referencias", "recursos" }, allowSetters = true)
    private Set<Proteina> proteinas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Recurso id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return this.uid;
    }

    public Recurso uid(String uid) {
        this.setUid(uid);
        return this;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public BioDB getDb() {
        return this.db;
    }

    public Recurso db(BioDB db) {
        this.setDb(db);
        return this;
    }

    public void setDb(BioDB db) {
        this.db = db;
    }

    public String getLink() {
        return this.link;
    }

    public Recurso link(String link) {
        this.setLink(link);
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Set<Proteina> getProteinas() {
        return this.proteinas;
    }

    public void setProteinas(Set<Proteina> proteinas) {
        if (this.proteinas != null) {
            this.proteinas.forEach(i -> i.removeRecurso(this));
        }
        if (proteinas != null) {
            proteinas.forEach(i -> i.addRecurso(this));
        }
        this.proteinas = proteinas;
    }

    public Recurso proteinas(Set<Proteina> proteinas) {
        this.setProteinas(proteinas);
        return this;
    }

    public Recurso addProteina(Proteina proteina) {
        this.proteinas.add(proteina);
        proteina.getRecursos().add(this);
        return this;
    }

    public Recurso removeProteina(Proteina proteina) {
        this.proteinas.remove(proteina);
        proteina.getRecursos().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recurso)) {
            return false;
        }
        return getId() != null && getId().equals(((Recurso) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Recurso{" +
            "id=" + getId() +
            ", uid='" + getUid() + "'" +
            ", db='" + getDb() + "'" +
            ", link='" + getLink() + "'" +
            "}";
    }
}
