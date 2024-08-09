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
 * A Referencia.
 */
@Entity
@Table(name = "referencia")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Referencia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "citacao", nullable = false)
    private String citacao;

    @Column(name = "link")
    private String link;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "autores")
    private String autores;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "referencias")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "curadoria", "versao", "gene", "referencias", "recursos" }, allowSetters = true)
    private Set<Proteina> proteinas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Referencia id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCitacao() {
        return this.citacao;
    }

    public Referencia citacao(String citacao) {
        this.setCitacao(citacao);
        return this;
    }

    public void setCitacao(String citacao) {
        this.citacao = citacao;
    }

    public String getLink() {
        return this.link;
    }

    public Referencia link(String link) {
        this.setLink(link);
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getAno() {
        return this.ano;
    }

    public Referencia ano(Integer ano) {
        this.setAno(ano);
        return this;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getAutores() {
        return this.autores;
    }

    public Referencia autores(String autores) {
        this.setAutores(autores);
        return this;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    public Set<Proteina> getProteinas() {
        return this.proteinas;
    }

    public void setProteinas(Set<Proteina> proteinas) {
        if (this.proteinas != null) {
            this.proteinas.forEach(i -> i.removeReferencia(this));
        }
        if (proteinas != null) {
            proteinas.forEach(i -> i.addReferencia(this));
        }
        this.proteinas = proteinas;
    }

    public Referencia proteinas(Set<Proteina> proteinas) {
        this.setProteinas(proteinas);
        return this;
    }

    public Referencia addProteina(Proteina proteina) {
        this.proteinas.add(proteina);
        proteina.getReferencias().add(this);
        return this;
    }

    public Referencia removeProteina(Proteina proteina) {
        this.proteinas.remove(proteina);
        proteina.getReferencias().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Referencia)) {
            return false;
        }
        return getId() != null && getId().equals(((Referencia) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Referencia{" +
            "id=" + getId() +
            ", citacao='" + getCitacao() + "'" +
            ", link='" + getLink() + "'" +
            ", ano=" + getAno() +
            ", autores='" + getAutores() + "'" +
            "}";
    }
}
