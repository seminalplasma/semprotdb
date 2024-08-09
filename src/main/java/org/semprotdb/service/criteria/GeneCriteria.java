package org.semprotdb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.semprotdb.domain.Gene} entity. This class is used
 * in {@link org.semprotdb.web.rest.GeneResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /genes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GeneCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private StringFilter descricao;

    private LongFilter curadoriaId;

    private LongFilter organismoId;

    private LongFilter proteinaId;

    private Boolean distinct;

    public GeneCriteria() {}

    public GeneCriteria(GeneCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nome = other.optionalNome().map(StringFilter::copy).orElse(null);
        this.descricao = other.optionalDescricao().map(StringFilter::copy).orElse(null);
        this.curadoriaId = other.optionalCuradoriaId().map(LongFilter::copy).orElse(null);
        this.organismoId = other.optionalOrganismoId().map(LongFilter::copy).orElse(null);
        this.proteinaId = other.optionalProteinaId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public GeneCriteria copy() {
        return new GeneCriteria(this);
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

    public LongFilter getCuradoriaId() {
        return curadoriaId;
    }

    public Optional<LongFilter> optionalCuradoriaId() {
        return Optional.ofNullable(curadoriaId);
    }

    public LongFilter curadoriaId() {
        if (curadoriaId == null) {
            setCuradoriaId(new LongFilter());
        }
        return curadoriaId;
    }

    public void setCuradoriaId(LongFilter curadoriaId) {
        this.curadoriaId = curadoriaId;
    }

    public LongFilter getOrganismoId() {
        return organismoId;
    }

    public Optional<LongFilter> optionalOrganismoId() {
        return Optional.ofNullable(organismoId);
    }

    public LongFilter organismoId() {
        if (organismoId == null) {
            setOrganismoId(new LongFilter());
        }
        return organismoId;
    }

    public void setOrganismoId(LongFilter organismoId) {
        this.organismoId = organismoId;
    }

    public LongFilter getProteinaId() {
        return proteinaId;
    }

    public Optional<LongFilter> optionalProteinaId() {
        return Optional.ofNullable(proteinaId);
    }

    public LongFilter proteinaId() {
        if (proteinaId == null) {
            setProteinaId(new LongFilter());
        }
        return proteinaId;
    }

    public void setProteinaId(LongFilter proteinaId) {
        this.proteinaId = proteinaId;
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
        final GeneCriteria that = (GeneCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(descricao, that.descricao) &&
            Objects.equals(curadoriaId, that.curadoriaId) &&
            Objects.equals(organismoId, that.organismoId) &&
            Objects.equals(proteinaId, that.proteinaId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, descricao, curadoriaId, organismoId, proteinaId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GeneCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNome().map(f -> "nome=" + f + ", ").orElse("") +
            optionalDescricao().map(f -> "descricao=" + f + ", ").orElse("") +
            optionalCuradoriaId().map(f -> "curadoriaId=" + f + ", ").orElse("") +
            optionalOrganismoId().map(f -> "organismoId=" + f + ", ").orElse("") +
            optionalProteinaId().map(f -> "proteinaId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
