package org.semprotdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Formato;
import org.semprotdb.domain.enumeration.Tipo;

/**
 * A Carga.
 */
@Entity
@Table(name = "carga")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Carga implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    private String status;

    @Column(name = "ordem")
    private Integer ordem;

    @Lob
    @Column(name = "planilha")
    private byte[] planilha;

    @Column(name = "planilha_content_type")
    private String planilhaContentType;

    @NotNull
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "caminho")
    private String caminho;

    @Column(name = "validado")
    private Boolean validado;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private Tipo tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "formato", nullable = false)
    private Formato formato;

    @Enumerated(EnumType.STRING)
    @Column(name = "destino")
    private Destino destino;

    @Column(name = "linhas")
    private Integer linhas;

    @Column(name = "checksum")
    private String checksum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "proteinas", "cargas" }, allowSetters = true)
    private Versao versao;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Carga id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public Carga status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOrdem() {
        return this.ordem;
    }

    public Carga ordem(Integer ordem) {
        this.setOrdem(ordem);
        return this;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public byte[] getPlanilha() {
        return this.planilha;
    }

    public Carga planilha(byte[] planilha) {
        this.setPlanilha(planilha);
        return this;
    }

    public void setPlanilha(byte[] planilha) {
        this.planilha = planilha;
    }

    public String getPlanilhaContentType() {
        return this.planilhaContentType;
    }

    public Carga planilhaContentType(String planilhaContentType) {
        this.planilhaContentType = planilhaContentType;
        return this;
    }

    public void setPlanilhaContentType(String planilhaContentType) {
        this.planilhaContentType = planilhaContentType;
    }

    public String getNome() {
        return this.nome;
    }

    public Carga nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCaminho() {
        return this.caminho;
    }

    public Carga caminho(String caminho) {
        this.setCaminho(caminho);
        return this;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public Boolean getValidado() {
        return this.validado;
    }

    public Carga validado(Boolean validado) {
        this.setValidado(validado);
        return this;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public Tipo getTipo() {
        return this.tipo;
    }

    public Carga tipo(Tipo tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Formato getFormato() {
        return this.formato;
    }

    public Carga formato(Formato formato) {
        this.setFormato(formato);
        return this;
    }

    public void setFormato(Formato formato) {
        this.formato = formato;
    }

    public Destino getDestino() {
        return this.destino;
    }

    public Carga destino(Destino destino) {
        this.setDestino(destino);
        return this;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }

    public Integer getLinhas() {
        return this.linhas;
    }

    public Carga linhas(Integer linhas) {
        this.setLinhas(linhas);
        return this;
    }

    public void setLinhas(Integer linhas) {
        this.linhas = linhas;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public Carga checksum(String checksum) {
        this.setChecksum(checksum);
        return this;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Versao getVersao() {
        return this.versao;
    }

    public void setVersao(Versao versao) {
        this.versao = versao;
    }

    public Carga versao(Versao versao) {
        this.setVersao(versao);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Carga)) {
            return false;
        }
        return getId() != null && getId().equals(((Carga) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Carga{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", ordem=" + getOrdem() +
            ", planilha='" + getPlanilha() + "'" +
            ", planilhaContentType='" + getPlanilhaContentType() + "'" +
            ", nome='" + getNome() + "'" +
            ", caminho='" + getCaminho() + "'" +
            ", validado='" + getValidado() + "'" +
            ", tipo='" + getTipo() + "'" +
            ", formato='" + getFormato() + "'" +
            ", destino='" + getDestino() + "'" +
            ", linhas=" + getLinhas() +
            ", checksum='" + getChecksum() + "'" +
            "}";
    }
}
