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
 * A Organismo.
 */
@Entity
@Table(name = "organismo")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Organismo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "sigla")
    private String sigla;

    @Column(name = "apelido")
    private String apelido;

    @Lob
    @Column(name = "silhueta")
    private byte[] silhueta;

    @Column(name = "silhueta_content_type")
    private String silhuetaContentType;

    @Column(name = "icone")
    private String icone;

    @Column(name = "pos")
    private String pos;

    @Column(name = "imagem")
    private String imagem;

    @Column(name = "descricao")
    private String descricao;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organismo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "curadoria", "organismo", "proteinas" }, allowSetters = true)
    private Set<Gene> genes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Organismo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Organismo nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return this.sigla;
    }

    public Organismo sigla(String sigla) {
        this.setSigla(sigla);
        return this;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getApelido() {
        return this.apelido;
    }

    public Organismo apelido(String apelido) {
        this.setApelido(apelido);
        return this;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public byte[] getSilhueta() {
        return this.silhueta;
    }

    public Organismo silhueta(byte[] silhueta) {
        this.setSilhueta(silhueta);
        return this;
    }

    public void setSilhueta(byte[] silhueta) {
        this.silhueta = silhueta;
    }

    public String getSilhuetaContentType() {
        return this.silhuetaContentType;
    }

    public Organismo silhuetaContentType(String silhuetaContentType) {
        this.silhuetaContentType = silhuetaContentType;
        return this;
    }

    public void setSilhuetaContentType(String silhuetaContentType) {
        this.silhuetaContentType = silhuetaContentType;
    }

    public String getIcone() {
        return this.icone;
    }

    public Organismo icone(String icone) {
        this.setIcone(icone);
        return this;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getPos() {
        return this.pos;
    }

    public Organismo pos(String pos) {
        this.setPos(pos);
        return this;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getImagem() {
        return this.imagem;
    }

    public Organismo imagem(String imagem) {
        this.setImagem(imagem);
        return this;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public Organismo descricao(String descricao) {
        this.setDescricao(descricao);
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Gene> getGenes() {
        return this.genes;
    }

    public void setGenes(Set<Gene> genes) {
        if (this.genes != null) {
            this.genes.forEach(i -> i.setOrganismo(null));
        }
        if (genes != null) {
            genes.forEach(i -> i.setOrganismo(this));
        }
        this.genes = genes;
    }

    public Organismo genes(Set<Gene> genes) {
        this.setGenes(genes);
        return this;
    }

    public Organismo addGene(Gene gene) {
        this.genes.add(gene);
        gene.setOrganismo(this);
        return this;
    }

    public Organismo removeGene(Gene gene) {
        this.genes.remove(gene);
        gene.setOrganismo(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organismo)) {
            return false;
        }
        return getId() != null && getId().equals(((Organismo) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organismo{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", sigla='" + getSigla() + "'" +
            ", apelido='" + getApelido() + "'" +
            ", silhueta='" + getSilhueta() + "'" +
            ", silhuetaContentType='" + getSilhuetaContentType() + "'" +
            ", icone='" + getIcone() + "'" +
            ", pos='" + getPos() + "'" +
            ", imagem='" + getImagem() + "'" +
            ", descricao='" + getDescricao() + "'" +
            "}";
    }
}
