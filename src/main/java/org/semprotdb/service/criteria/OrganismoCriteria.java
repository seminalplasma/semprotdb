package org.semprotdb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.semprotdb.domain.Organismo} entity. This class is used
 * in {@link org.semprotdb.web.rest.OrganismoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /organismos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrganismoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private StringFilter sigla;

    private StringFilter apelido;

    private StringFilter icone;

    private StringFilter pos;

    private StringFilter imagem;

    private StringFilter descricao;

    private LongFilter geneId;

    private Boolean distinct;

    public OrganismoCriteria() {}

    public OrganismoCriteria(OrganismoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nome = other.optionalNome().map(StringFilter::copy).orElse(null);
        this.sigla = other.optionalSigla().map(StringFilter::copy).orElse(null);
        this.apelido = other.optionalApelido().map(StringFilter::copy).orElse(null);
        this.icone = other.optionalIcone().map(StringFilter::copy).orElse(null);
        this.pos = other.optionalPos().map(StringFilter::copy).orElse(null);
        this.imagem = other.optionalImagem().map(StringFilter::copy).orElse(null);
        this.descricao = other.optionalDescricao().map(StringFilter::copy).orElse(null);
        this.geneId = other.optionalGeneId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OrganismoCriteria copy() {
        return new OrganismoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNome() {
        return nome;
    }

    public Optional<StringFilter> optionalNome() {
        return Optional.ofNullable(nome);
    }

    public StringFilter nome() {
        if (nome == null) {
            setNome(new StringFilter());
        }
        return nome;
    }

    public void setNome(StringFilter nome) {
        this.nome = nome;
    }

    public StringFilter getSigla() {
        return sigla;
    }

    public Optional<StringFilter> optionalSigla() {
        return Optional.ofNullable(sigla);
    }

    public StringFilter sigla() {
        if (sigla == null) {
            setSigla(new StringFilter());
        }
        return sigla;
    }

    public void setSigla(StringFilter sigla) {
        this.sigla = sigla;
    }

    public StringFilter getApelido() {
        return apelido;
    }

    public Optional<StringFilter> optionalApelido() {
        return Optional.ofNullable(apelido);
    }

    public StringFilter apelido() {
        if (apelido == null) {
            setApelido(new StringFilter());
        }
        return apelido;
    }

    public void setApelido(StringFilter apelido) {
        this.apelido = apelido;
    }

    public StringFilter getIcone() {
        return icone;
    }

    public Optional<StringFilter> optionalIcone() {
        return Optional.ofNullable(icone);
    }

    public StringFilter icone() {
        if (icone == null) {
            setIcone(new StringFilter());
        }
        return icone;
    }

    public void setIcone(StringFilter icone) {
        this.icone = icone;
    }

    public StringFilter getPos() {
        return pos;
    }

    public Optional<StringFilter> optionalPos() {
        return Optional.ofNullable(pos);
    }

    public StringFilter pos() {
        if (pos == null) {
            setPos(new StringFilter());
        }
        return pos;
    }

    public void setPos(StringFilter pos) {
        this.pos = pos;
    }

    public StringFilter getImagem() {
        return imagem;
    }

    public Optional<StringFilter> optionalImagem() {
        return Optional.ofNullable(imagem);
    }

    public StringFilter imagem() {
        if (imagem == null) {
            setImagem(new StringFilter());
        }
        return imagem;
    }

    public void setImagem(StringFilter imagem) {
        this.imagem = imagem;
    }

    public StringFilter getDescricao() {
        return descricao;
    }

    public Optional<StringFilter> optionalDescricao() {
        return Optional.ofNullable(descricao);
    }

    public StringFilter descricao() {
        if (descricao == null) {
            setDescricao(new StringFilter());
        }
        return descricao;
    }

    public void setDescricao(StringFilter descricao) {
        this.descricao = descricao;
    }

    public LongFilter getGeneId() {
        return geneId;
    }

    public Optional<LongFilter> optionalGeneId() {
        return Optional.ofNullable(geneId);
    }

    public LongFilter geneId() {
        if (geneId == null) {
            setGeneId(new LongFilter());
        }
        return geneId;
    }

    public void setGeneId(LongFilter geneId) {
        this.geneId = geneId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrganismoCriteria that = (OrganismoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(sigla, that.sigla) &&
            Objects.equals(apelido, that.apelido) &&
            Objects.equals(icone, that.icone) &&
            Objects.equals(pos, that.pos) &&
            Objects.equals(imagem, that.imagem) &&
            Objects.equals(descricao, that.descricao) &&
            Objects.equals(geneId, that.geneId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, sigla, apelido, icone, pos, imagem, descricao, geneId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrganismoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNome().map(f -> "nome=" + f + ", ").orElse("") +
            optionalSigla().map(f -> "sigla=" + f + ", ").orElse("") +
            optionalApelido().map(f -> "apelido=" + f + ", ").orElse("") +
            optionalIcone().map(f -> "icone=" + f + ", ").orElse("") +
            optionalPos().map(f -> "pos=" + f + ", ").orElse("") +
            optionalImagem().map(f -> "imagem=" + f + ", ").orElse("") +
            optionalDescricao().map(f -> "descricao=" + f + ", ").orElse("") +
            optionalGeneId().map(f -> "geneId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
