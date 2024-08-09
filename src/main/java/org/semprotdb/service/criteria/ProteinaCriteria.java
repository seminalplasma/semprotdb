package org.semprotdb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.semprotdb.domain.Proteina} entity. This class is used
 * in {@link org.semprotdb.web.rest.ProteinaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /proteinas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProteinaCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private IntegerFilter tamanho;

    private StringFilter massa;

    private StringFilter descricao;

    private LongFilter curadoriaId;

    private LongFilter versaoId;

    private LongFilter geneId;

    private LongFilter referenciaId;

    private LongFilter recursoId;

    private Boolean distinct;

    public ProteinaCriteria() {}

    public ProteinaCriteria(ProteinaCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nome = other.optionalNome().map(StringFilter::copy).orElse(null);
        this.tamanho = other.optionalTamanho().map(IntegerFilter::copy).orElse(null);
        this.massa = other.optionalMassa().map(StringFilter::copy).orElse(null);
        this.descricao = other.optionalDescricao().map(StringFilter::copy).orElse(null);
        this.curadoriaId = other.optionalCuradoriaId().map(LongFilter::copy).orElse(null);
        this.versaoId = other.optionalVersaoId().map(LongFilter::copy).orElse(null);
        this.geneId = other.optionalGeneId().map(LongFilter::copy).orElse(null);
        this.referenciaId = other.optionalReferenciaId().map(LongFilter::copy).orElse(null);
        this.recursoId = other.optionalRecursoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProteinaCriteria copy() {
        return new ProteinaCriteria(this);
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

    public IntegerFilter getTamanho() {
        return tamanho;
    }

    public Optional<IntegerFilter> optionalTamanho() {
        return Optional.ofNullable(tamanho);
    }

    public IntegerFilter tamanho() {
        if (tamanho == null) {
            setTamanho(new IntegerFilter());
        }
        return tamanho;
    }

    public void setTamanho(IntegerFilter tamanho) {
        this.tamanho = tamanho;
    }

    public StringFilter getMassa() {
        return massa;
    }

    public Optional<StringFilter> optionalMassa() {
        return Optional.ofNullable(massa);
    }

    public StringFilter massa() {
        if (massa == null) {
            setMassa(new StringFilter());
        }
        return massa;
    }

    public void setMassa(StringFilter massa) {
        this.massa = massa;
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

    public LongFilter getVersaoId() {
        return versaoId;
    }

    public Optional<LongFilter> optionalVersaoId() {
        return Optional.ofNullable(versaoId);
    }

    public LongFilter versaoId() {
        if (versaoId == null) {
            setVersaoId(new LongFilter());
        }
        return versaoId;
    }

    public void setVersaoId(LongFilter versaoId) {
        this.versaoId = versaoId;
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

    public LongFilter getReferenciaId() {
        return referenciaId;
    }

    public Optional<LongFilter> optionalReferenciaId() {
        return Optional.ofNullable(referenciaId);
    }

    public LongFilter referenciaId() {
        if (referenciaId == null) {
            setReferenciaId(new LongFilter());
        }
        return referenciaId;
    }

    public void setReferenciaId(LongFilter referenciaId) {
        this.referenciaId = referenciaId;
    }

    public LongFilter getRecursoId() {
        return recursoId;
    }

    public Optional<LongFilter> optionalRecursoId() {
        return Optional.ofNullable(recursoId);
    }

    public LongFilter recursoId() {
        if (recursoId == null) {
            setRecursoId(new LongFilter());
        }
        return recursoId;
    }

    public void setRecursoId(LongFilter recursoId) {
        this.recursoId = recursoId;
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
        final ProteinaCriteria that = (ProteinaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(tamanho, that.tamanho) &&
            Objects.equals(massa, that.massa) &&
            Objects.equals(descricao, that.descricao) &&
            Objects.equals(curadoriaId, that.curadoriaId) &&
            Objects.equals(versaoId, that.versaoId) &&
            Objects.equals(geneId, that.geneId) &&
            Objects.equals(referenciaId, that.referenciaId) &&
            Objects.equals(recursoId, that.recursoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, tamanho, massa, descricao, curadoriaId, versaoId, geneId, referenciaId, recursoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProteinaCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNome().map(f -> "nome=" + f + ", ").orElse("") +
            optionalTamanho().map(f -> "tamanho=" + f + ", ").orElse("") +
            optionalMassa().map(f -> "massa=" + f + ", ").orElse("") +
            optionalDescricao().map(f -> "descricao=" + f + ", ").orElse("") +
            optionalCuradoriaId().map(f -> "curadoriaId=" + f + ", ").orElse("") +
            optionalVersaoId().map(f -> "versaoId=" + f + ", ").orElse("") +
            optionalGeneId().map(f -> "geneId=" + f + ", ").orElse("") +
            optionalReferenciaId().map(f -> "referenciaId=" + f + ", ").orElse("") +
            optionalRecursoId().map(f -> "recursoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
