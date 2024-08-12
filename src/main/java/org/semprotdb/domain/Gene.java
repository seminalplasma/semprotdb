package org.semprotdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Gene.
 */
@Entity
@Table(name = "gene")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Gene implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "proteinas", "genes" }, allowSetters = true)
    private Curadoria curadoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "genes" }, allowSetters = true)
    private Organismo organismo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gene", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "curadoria", "versao", "gene", "referencias", "recursos" }, allowSetters = true)
    private Set<Proteina> proteinas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Gene id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Gene nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public Gene descricao(String descricao) {
        this.setDescricao(descricao);
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Curadoria getCuradoria() {
        return this.curadoria;
    }

    public void setCuradoria(Curadoria curadoria) {
        this.curadoria = curadoria;
    }

    public Gene curadoria(Curadoria curadoria) {
        this.setCuradoria(curadoria);
        return this;
    }

    public Organismo getOrganismo() {
        return this.organismo;
    }

    public void setOrganismo(Organismo organismo) {
        this.organismo = organismo;
    }

    public Gene organismo(Organismo organismo) {
        this.setOrganismo(organismo);
        return this;
    }

    public Set<Proteina> getProteinas() {
        return this.proteinas;
    }

    public void setProteinas(Set<Proteina> proteinas) {
        if (this.proteinas != null) {
            this.proteinas.forEach(i -> i.setGene(null));
        }
        if (proteinas != null) {
            proteinas.forEach(i -> i.setGene(this));
        }
        this.proteinas = proteinas;
    }

    public Gene proteinas(Set<Proteina> proteinas) {
        this.setProteinas(proteinas);
        return this;
    }

    public Gene addProteina(Proteina proteina) {
        this.proteinas.add(proteina);
        proteina.setGene(this);
        return this;
    }

    public Gene removeProteina(Proteina proteina) {
        this.proteinas.remove(proteina);
        proteina.setGene(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Gene)) {
            return false;
        }
        return getId() != null && getId().equals(((Gene) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Gene{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", descricao='" + getDescricao() + "'" +
            "}";
    }
}
