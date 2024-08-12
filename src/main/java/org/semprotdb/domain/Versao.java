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
import org.semprotdb.domain.enumeration.Status;

/**
 * A Versao.
 */
@Entity
@Table(name = "versao")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Versao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "detalhes")
    private String detalhes;

    @Column(name = "release")
    private Instant release;

    @Column(name = "label")
    private String label;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull
    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "logo")
    private String logo;

    @Lob
    @Column(name = "log")
    private String log;

    @Lob
    @Column(name = "texto")
    private String texto;

    @Lob
    @Column(name = "imagem")
    private byte[] imagem;

    @Column(name = "imagem_content_type")
    private String imagemContentType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "versao", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "curadoria", "versao", "gene", "referencias", "recursos" }, allowSetters = true)
    private Set<Proteina> proteinas = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "versao", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "versao" }, allowSetters = true)
    private Set<Carga> cargas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Versao id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Versao nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDetalhes() {
        return this.detalhes;
    }

    public Versao detalhes(String detalhes) {
        this.setDetalhes(detalhes);
        return this;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public Instant getRelease() {
        return this.release;
    }

    public Versao release(Instant release) {
        this.setRelease(release);
        return this;
    }

    public void setRelease(Instant release) {
        this.release = release;
    }

    public String getLabel() {
        return this.label;
    }

    public Versao label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Status getStatus() {
        return this.status;
    }

    public Versao status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getNumero() {
        return this.numero;
    }

    public Versao numero(Integer numero) {
        this.setNumero(numero);
        return this;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getLogo() {
        return this.logo;
    }

    public Versao logo(String logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLog() {
        return this.log;
    }

    public Versao log(String log) {
        this.setLog(log);
        return this;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getTexto() {
        return this.texto;
    }

    public Versao texto(String texto) {
        this.setTexto(texto);
        return this;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public byte[] getImagem() {
        return this.imagem;
    }

    public Versao imagem(byte[] imagem) {
        this.setImagem(imagem);
        return this;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public String getImagemContentType() {
        return this.imagemContentType;
    }

    public Versao imagemContentType(String imagemContentType) {
        this.imagemContentType = imagemContentType;
        return this;
    }

    public void setImagemContentType(String imagemContentType) {
        this.imagemContentType = imagemContentType;
    }

    public Set<Proteina> getProteinas() {
        return this.proteinas;
    }

    public void setProteinas(Set<Proteina> proteinas) {
        if (this.proteinas != null) {
            this.proteinas.forEach(i -> i.setVersao(null));
        }
        if (proteinas != null) {
            proteinas.forEach(i -> i.setVersao(this));
        }
        this.proteinas = proteinas;
    }

    public Versao proteinas(Set<Proteina> proteinas) {
        this.setProteinas(proteinas);
        return this;
    }

    public Versao addProteina(Proteina proteina) {
        this.proteinas.add(proteina);
        proteina.setVersao(this);
        return this;
    }

    public Versao removeProteina(Proteina proteina) {
        this.proteinas.remove(proteina);
        proteina.setVersao(null);
        return this;
    }

    public Set<Carga> getCargas() {
        return this.cargas;
    }

    public void setCargas(Set<Carga> cargas) {
        if (this.cargas != null) {
            this.cargas.forEach(i -> i.setVersao(null));
        }
        if (cargas != null) {
            cargas.forEach(i -> i.setVersao(this));
        }
        this.cargas = cargas;
    }

    public Versao cargas(Set<Carga> cargas) {
        this.setCargas(cargas);
        return this;
    }

    public Versao addCarga(Carga carga) {
        this.cargas.add(carga);
        carga.setVersao(this);
        return this;
    }

    public Versao removeCarga(Carga carga) {
        this.cargas.remove(carga);
        carga.setVersao(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Versao)) {
            return false;
        }
        return getId() != null && getId().equals(((Versao) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Versao{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", detalhes='" + getDetalhes() + "'" +
            ", release='" + getRelease() + "'" +
            ", label='" + getLabel() + "'" +
            ", status='" + getStatus() + "'" +
            ", numero=" + getNumero() +
            ", logo='" + getLogo() + "'" +
            ", log='" + getLog() + "'" +
            ", texto='" + getTexto() + "'" +
            ", imagem='" + getImagem() + "'" +
            ", imagemContentType='" + getImagemContentType() + "'" +
            "}";
    }

    public Versao addLog(String s) {
        setLog(this.log == null || this.log.isEmpty() ? s : (this.log + "\n" + s));
        return this;
    }

    public String identfy() {
        return ("Versao{" + "id=" + getId() + ", nome='" + getNome() + "'" + ", numero=" + getNumero() + ", status='" + getStatus() + "'}");
    }
}
