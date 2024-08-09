package org.semprotdb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.semprotdb.domain.enumeration.Status;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.semprotdb.domain.Versao} entity. This class is used
 * in {@link org.semprotdb.web.rest.VersaoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /versaos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VersaoCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {

        public StatusFilter() {}

        public StatusFilter(StatusFilter filter) {
            super(filter);
        }

        @Override
        public StatusFilter copy() {
            return new StatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nome;

    private StringFilter detalhes;

    private InstantFilter release;

    private StringFilter label;

    private StatusFilter status;

    private IntegerFilter numero;

    private StringFilter logo;

    private LongFilter proteinaId;

    private LongFilter cargaId;

    private Boolean distinct;

    public VersaoCriteria() {}

    public VersaoCriteria(VersaoCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nome = other.optionalNome().map(StringFilter::copy).orElse(null);
        this.detalhes = other.optionalDetalhes().map(StringFilter::copy).orElse(null);
        this.release = other.optionalRelease().map(InstantFilter::copy).orElse(null);
        this.label = other.optionalLabel().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StatusFilter::copy).orElse(null);
        this.numero = other.optionalNumero().map(IntegerFilter::copy).orElse(null);
        this.logo = other.optionalLogo().map(StringFilter::copy).orElse(null);
        this.proteinaId = other.optionalProteinaId().map(LongFilter::copy).orElse(null);
        this.cargaId = other.optionalCargaId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VersaoCriteria copy() {
        return new VersaoCriteria(this);
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

    public StringFilter getDetalhes() {
        return detalhes;
    }

    public Optional<StringFilter> optionalDetalhes() {
        return Optional.ofNullable(detalhes);
    }

    public StringFilter detalhes() {
        if (detalhes == null) {
            setDetalhes(new StringFilter());
        }
        return detalhes;
    }

    public void setDetalhes(StringFilter detalhes) {
        this.detalhes = detalhes;
    }

    public InstantFilter getRelease() {
        return release;
    }

    public Optional<InstantFilter> optionalRelease() {
        return Optional.ofNullable(release);
    }

    public InstantFilter release() {
        if (release == null) {
            setRelease(new InstantFilter());
        }
        return release;
    }

    public void setRelease(InstantFilter release) {
        this.release = release;
    }

    public StringFilter getLabel() {
        return label;
    }

    public Optional<StringFilter> optionalLabel() {
        return Optional.ofNullable(label);
    }

    public StringFilter label() {
        if (label == null) {
            setLabel(new StringFilter());
        }
        return label;
    }

    public void setLabel(StringFilter label) {
        this.label = label;
    }

    public StatusFilter getStatus() {
        return status;
    }

    public Optional<StatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StatusFilter status() {
        if (status == null) {
            setStatus(new StatusFilter());
        }
        return status;
    }

    public void setStatus(StatusFilter status) {
        this.status = status;
    }

    public IntegerFilter getNumero() {
        return numero;
    }

    public Optional<IntegerFilter> optionalNumero() {
        return Optional.ofNullable(numero);
    }

    public IntegerFilter numero() {
        if (numero == null) {
            setNumero(new IntegerFilter());
        }
        return numero;
    }

    public void setNumero(IntegerFilter numero) {
        this.numero = numero;
    }

    public StringFilter getLogo() {
        return logo;
    }

    public Optional<StringFilter> optionalLogo() {
        return Optional.ofNullable(logo);
    }

    public StringFilter logo() {
        if (logo == null) {
            setLogo(new StringFilter());
        }
        return logo;
    }

    public void setLogo(StringFilter logo) {
        this.logo = logo;
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

    public LongFilter getCargaId() {
        return cargaId;
    }

    public Optional<LongFilter> optionalCargaId() {
        return Optional.ofNullable(cargaId);
    }

    public LongFilter cargaId() {
        if (cargaId == null) {
            setCargaId(new LongFilter());
        }
        return cargaId;
    }

    public void setCargaId(LongFilter cargaId) {
        this.cargaId = cargaId;
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
        final VersaoCriteria that = (VersaoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(detalhes, that.detalhes) &&
            Objects.equals(release, that.release) &&
            Objects.equals(label, that.label) &&
            Objects.equals(status, that.status) &&
            Objects.equals(numero, that.numero) &&
            Objects.equals(logo, that.logo) &&
            Objects.equals(proteinaId, that.proteinaId) &&
            Objects.equals(cargaId, that.cargaId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, detalhes, release, label, status, numero, logo, proteinaId, cargaId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VersaoCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNome().map(f -> "nome=" + f + ", ").orElse("") +
            optionalDetalhes().map(f -> "detalhes=" + f + ", ").orElse("") +
            optionalRelease().map(f -> "release=" + f + ", ").orElse("") +
            optionalLabel().map(f -> "label=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalNumero().map(f -> "numero=" + f + ", ").orElse("") +
            optionalLogo().map(f -> "logo=" + f + ", ").orElse("") +
            optionalProteinaId().map(f -> "proteinaId=" + f + ", ").orElse("") +
            optionalCargaId().map(f -> "cargaId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
