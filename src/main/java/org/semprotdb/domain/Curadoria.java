package org.semprotdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Curadoria.
 */
@Entity
@Table(name = "curadoria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Curadoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "data", nullable = false)
    private Instant data;

    @Column(name = "anotacoes")
    private String anotacoes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "curadoria")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "curadoria", "versao", "gene", "referencias", "recursos" }, allowSetters = true)
    private Set<Proteina> proteinas = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "curadoria")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "curadoria", "organismo", "proteinas" }, allowSetters = true)
    private Set<Gene> genes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Curadoria id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public Curadoria email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getData() {
        return this.data;
    }

    public Curadoria data(Instant data) {
        this.setData(data);
        return this;
    }

    public void setData(Instant data) {
        this.data = data;
    }

    public String getAnotacoes() {
        return this.anotacoes;
    }

    public Curadoria anotacoes(String anotacoes) {
        this.setAnotacoes(anotacoes);
        return this;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public Set<Proteina> getProteinas() {
        return this.proteinas;
    }

    public void setProteinas(Set<Proteina> proteinas) {
        if (this.proteinas != null) {
            this.proteinas.forEach(i -> i.setCuradoria(null));
        }
        if (proteinas != null) {
            proteinas.forEach(i -> i.setCuradoria(this));
        }
        this.proteinas = proteinas;
    }

    public Curadoria proteinas(Set<Proteina> proteinas) {
        this.setProteinas(proteinas);
        return this;
    }

    public Curadoria addProteina(Proteina proteina) {
        this.proteinas.add(proteina);
        proteina.setCuradoria(this);
        return this;
    }

    public Curadoria removeProteina(Proteina proteina) {
        this.proteinas.remove(proteina);
        proteina.setCuradoria(null);
        return this;
    }

    public Set<Gene> getGenes() {
        return this.genes;
    }

    public void setGenes(Set<Gene> genes) {
        if (this.genes != null) {
            this.genes.forEach(i -> i.setCuradoria(null));
        }
        if (genes != null) {
            genes.forEach(i -> i.setCuradoria(this));
        }
        this.genes = genes;
    }

    public Curadoria genes(Set<Gene> genes) {
        this.setGenes(genes);
        return this;
    }

    public Curadoria addGene(Gene gene) {
        this.genes.add(gene);
        gene.setCuradoria(this);
        return this;
    }

    public Curadoria removeGene(Gene gene) {
        this.genes.remove(gene);
        gene.setCuradoria(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Curadoria)) {
            return false;
        }
        return getId() != null && getId().equals(((Curadoria) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Curadoria{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", data='" + getData() + "'" +
            ", anotacoes='" + getAnotacoes() + "'" +
            "}";
    }
}
