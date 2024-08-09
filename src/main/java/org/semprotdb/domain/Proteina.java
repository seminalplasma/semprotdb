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
 * A Proteina.
 */
@Entity
@Table(name = "proteina")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Proteina implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "tamanho")
    private Integer tamanho;

    @Column(name = "massa")
    private String massa;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "proteinas", "genes" }, allowSetters = true)
    private Curadoria curadoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "proteinas", "cargas" }, allowSetters = true)
    private Versao versao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "curadoria", "organismo", "proteinas" }, allowSetters = true)
    private Gene gene;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_proteina__referencia",
        joinColumns = @JoinColumn(name = "proteina_id"),
        inverseJoinColumns = @JoinColumn(name = "referencia_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "proteinas" }, allowSetters = true)
    private Set<Referencia> referencias = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_proteina__recurso",
        joinColumns = @JoinColumn(name = "proteina_id"),
        inverseJoinColumns = @JoinColumn(name = "recurso_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "proteinas" }, allowSetters = true)
    private Set<Recurso> recursos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Proteina id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Proteina nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getTamanho() {
        return this.tamanho;
    }

    public Proteina tamanho(Integer tamanho) {
        this.setTamanho(tamanho);
        return this;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    public String getMassa() {
        return this.massa;
    }

    public Proteina massa(String massa) {
        this.setMassa(massa);
        return this;
    }

    public void setMassa(String massa) {
        this.massa = massa;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public Proteina descricao(String descricao) {
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

    public Proteina curadoria(Curadoria curadoria) {
        this.setCuradoria(curadoria);
        return this;
    }

    public Versao getVersao() {
        return this.versao;
    }

    public void setVersao(Versao versao) {
        this.versao = versao;
    }

    public Proteina versao(Versao versao) {
        this.setVersao(versao);
        return this;
    }

    public Gene getGene() {
        return this.gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public Proteina gene(Gene gene) {
        this.setGene(gene);
        return this;
    }

    public Set<Referencia> getReferencias() {
        return this.referencias;
    }

    public void setReferencias(Set<Referencia> referencias) {
        this.referencias = referencias;
    }

    public Proteina referencias(Set<Referencia> referencias) {
        this.setReferencias(referencias);
        return this;
    }

    public Proteina addReferencia(Referencia referencia) {
        this.referencias.add(referencia);
        return this;
    }

    public Proteina removeReferencia(Referencia referencia) {
        this.referencias.remove(referencia);
        return this;
    }

    public Set<Recurso> getRecursos() {
        return this.recursos;
    }

    public void setRecursos(Set<Recurso> recursos) {
        this.recursos = recursos;
    }

    public Proteina recursos(Set<Recurso> recursos) {
        this.setRecursos(recursos);
        return this;
    }

    public Proteina addRecurso(Recurso recurso) {
        this.recursos.add(recurso);
        return this;
    }

    public Proteina removeRecurso(Recurso recurso) {
        this.recursos.remove(recurso);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Proteina)) {
            return false;
        }
        return getId() != null && getId().equals(((Proteina) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Proteina{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", tamanho=" + getTamanho() +
            ", massa='" + getMassa() + "'" +
            ", descricao='" + getDescricao() + "'" +
            "}";
    }
}
