package org.semprotdb.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DBConfig.
 */
@Entity
@Table(name = "db_config")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DBConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "habilitado")
    private Boolean habilitado;

    @Column(name = "vstring")
    private String vstring;

    @Column(name = "vbol")
    private Boolean vbol;

    @Column(name = "vdate")
    private Instant vdate;

    @Column(name = "vint")
    private Integer vint;

    @Lob
    @Column(name = "vtext")
    private String vtext;

    @Lob
    @Column(name = "vimg")
    private byte[] vimg;

    @Column(name = "vimg_content_type")
    private String vimgContentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DBConfig id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public DBConfig key(String key) {
        this.setKey(key);
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getHabilitado() {
        return this.habilitado;
    }

    public DBConfig habilitado(Boolean habilitado) {
        this.setHabilitado(habilitado);
        return this;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getVstring() {
        return this.vstring;
    }

    public DBConfig vstring(String vstring) {
        this.setVstring(vstring);
        return this;
    }

    public void setVstring(String vstring) {
        this.vstring = vstring;
    }

    public Boolean getVbol() {
        return this.vbol;
    }

    public DBConfig vbol(Boolean vbol) {
        this.setVbol(vbol);
        return this;
    }

    public void setVbol(Boolean vbol) {
        this.vbol = vbol;
    }

    public Instant getVdate() {
        return this.vdate;
    }

    public DBConfig vdate(Instant vdate) {
        this.setVdate(vdate);
        return this;
    }

    public void setVdate(Instant vdate) {
        this.vdate = vdate;
    }

    public Integer getVint() {
        return this.vint;
    }

    public DBConfig vint(Integer vint) {
        this.setVint(vint);
        return this;
    }

    public void setVint(Integer vint) {
        this.vint = vint;
    }

    public String getVtext() {
        return this.vtext;
    }

    public DBConfig vtext(String vtext) {
        this.setVtext(vtext);
        return this;
    }

    public void setVtext(String vtext) {
        this.vtext = vtext;
    }

    public byte[] getVimg() {
        return this.vimg;
    }

    public DBConfig vimg(byte[] vimg) {
        this.setVimg(vimg);
        return this;
    }

    public void setVimg(byte[] vimg) {
        this.vimg = vimg;
    }

    public String getVimgContentType() {
        return this.vimgContentType;
    }

    public DBConfig vimgContentType(String vimgContentType) {
        this.vimgContentType = vimgContentType;
        return this;
    }

    public void setVimgContentType(String vimgContentType) {
        this.vimgContentType = vimgContentType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DBConfig)) {
            return false;
        }
        return getId() != null && getId().equals(((DBConfig) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DBConfig{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", habilitado='" + getHabilitado() + "'" +
            ", vstring='" + getVstring() + "'" +
            ", vbol='" + getVbol() + "'" +
            ", vdate='" + getVdate() + "'" +
            ", vint=" + getVint() +
            ", vtext='" + getVtext() + "'" +
            ", vimg='" + getVimg() + "'" +
            ", vimgContentType='" + getVimgContentType() + "'" +
            "}";
    }
}
