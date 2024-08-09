package org.semprotdb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Formato;
import org.semprotdb.domain.enumeration.Tipo;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.semprotdb.domain.Carga} entity. This class is used
 * in {@link org.semprotdb.web.rest.CargaResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cargas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CargaCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Tipo
     */
    public static class TipoFilter extends Filter<Tipo> {

        public TipoFilter() {}

        public TipoFilter(TipoFilter filter) {
            super(filter);
        }

        @Override
        public TipoFilter copy() {
            return new TipoFilter(this);
        }
    }

    /**
     * Class for filtering Formato
     */
    public static class FormatoFilter extends Filter<Formato> {

        public FormatoFilter() {}

        public FormatoFilter(FormatoFilter filter) {
            super(filter);
        }

        @Override
        public FormatoFilter copy() {
            return new FormatoFilter(this);
        }
    }

    /**
     * Class for filtering Destino
     */
    public static class DestinoFilter extends Filter<Destino> {

        public DestinoFilter() {}

        public DestinoFilter(DestinoFilter filter) {
            super(filter);
        }

        @Override
        public DestinoFilter copy() {
            return new DestinoFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter status;

    private IntegerFilter ordem;

    private StringFilter nome;

    private StringFilter caminho;

    private BooleanFilter validado;

    private TipoFilter tipo;

    private FormatoFilter formato;

    private DestinoFilter destino;

    private IntegerFilter linhas;

    private StringFilter checksum;

    private LongFilter versaoId;

    private Boolean distinct;

    public CargaCriteria() {}

    public CargaCriteria(CargaCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(StringFilter::copy).orElse(null);
        this.ordem = other.optionalOrdem().map(IntegerFilter::copy).orElse(null);
        this.nome = other.optionalNome().map(StringFilter::copy).orElse(null);
        this.caminho = other.optionalCaminho().map(StringFilter::copy).orElse(null);
        this.validado = other.optionalValidado().map(BooleanFilter::copy).orElse(null);
        this.tipo = other.optionalTipo().map(TipoFilter::copy).orElse(null);
        this.formato = other.optionalFormato().map(FormatoFilter::copy).orElse(null);
        this.destino = other.optionalDestino().map(DestinoFilter::copy).orElse(null);
        this.linhas = other.optionalLinhas().map(IntegerFilter::copy).orElse(null);
        this.checksum = other.optionalChecksum().map(StringFilter::copy).orElse(null);
        this.versaoId = other.optionalVersaoId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CargaCriteria copy() {
        return new CargaCriteria(this);
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

    public StringFilter getStatus() {
        return status;
    }

    public Optional<StringFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public StringFilter status() {
        if (status == null) {
            setStatus(new StringFilter());
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public IntegerFilter getOrdem() {
        return ordem;
    }

    public Optional<IntegerFilter> optionalOrdem() {
        return Optional.ofNullable(ordem);
    }

    public IntegerFilter ordem() {
        if (ordem == null) {
            setOrdem(new IntegerFilter());
        }
        return ordem;
    }

    public void setOrdem(IntegerFilter ordem) {
        this.ordem = ordem;
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

    public StringFilter getCaminho() {
        return caminho;
    }

    public Optional<StringFilter> optionalCaminho() {
        return Optional.ofNullable(caminho);
    }

    public StringFilter caminho() {
        if (caminho == null) {
            setCaminho(new StringFilter());
        }
        return caminho;
    }

    public void setCaminho(StringFilter caminho) {
        this.caminho = caminho;
    }

    public BooleanFilter getValidado() {
        return validado;
    }

    public Optional<BooleanFilter> optionalValidado() {
        return Optional.ofNullable(validado);
    }

    public BooleanFilter validado() {
        if (validado == null) {
            setValidado(new BooleanFilter());
        }
        return validado;
    }

    public void setValidado(BooleanFilter validado) {
        this.validado = validado;
    }

    public TipoFilter getTipo() {
        return tipo;
    }

    public Optional<TipoFilter> optionalTipo() {
        return Optional.ofNullable(tipo);
    }

    public TipoFilter tipo() {
        if (tipo == null) {
            setTipo(new TipoFilter());
        }
        return tipo;
    }

    public void setTipo(TipoFilter tipo) {
        this.tipo = tipo;
    }

    public FormatoFilter getFormato() {
        return formato;
    }

    public Optional<FormatoFilter> optionalFormato() {
        return Optional.ofNullable(formato);
    }

    public FormatoFilter formato() {
        if (formato == null) {
            setFormato(new FormatoFilter());
        }
        return formato;
    }

    public void setFormato(FormatoFilter formato) {
        this.formato = formato;
    }

    public DestinoFilter getDestino() {
        return destino;
    }

    public Optional<DestinoFilter> optionalDestino() {
        return Optional.ofNullable(destino);
    }

    public DestinoFilter destino() {
        if (destino == null) {
            setDestino(new DestinoFilter());
        }
        return destino;
    }

    public void setDestino(DestinoFilter destino) {
        this.destino = destino;
    }

    public IntegerFilter getLinhas() {
        return linhas;
    }

    public Optional<IntegerFilter> optionalLinhas() {
        return Optional.ofNullable(linhas);
    }

    public IntegerFilter linhas() {
        if (linhas == null) {
            setLinhas(new IntegerFilter());
        }
        return linhas;
    }

    public void setLinhas(IntegerFilter linhas) {
        this.linhas = linhas;
    }

    public StringFilter getChecksum() {
        return checksum;
    }

    public Optional<StringFilter> optionalChecksum() {
        return Optional.ofNullable(checksum);
    }

    public StringFilter checksum() {
        if (checksum == null) {
            setChecksum(new StringFilter());
        }
        return checksum;
    }

    public void setChecksum(StringFilter checksum) {
        this.checksum = checksum;
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
        final CargaCriteria that = (CargaCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(status, that.status) &&
            Objects.equals(ordem, that.ordem) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(caminho, that.caminho) &&
            Objects.equals(validado, that.validado) &&
            Objects.equals(tipo, that.tipo) &&
            Objects.equals(formato, that.formato) &&
            Objects.equals(destino, that.destino) &&
            Objects.equals(linhas, that.linhas) &&
            Objects.equals(checksum, that.checksum) &&
            Objects.equals(versaoId, that.versaoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, ordem, nome, caminho, validado, tipo, formato, destino, linhas, checksum, versaoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CargaCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalOrdem().map(f -> "ordem=" + f + ", ").orElse("") +
            optionalNome().map(f -> "nome=" + f + ", ").orElse("") +
            optionalCaminho().map(f -> "caminho=" + f + ", ").orElse("") +
            optionalValidado().map(f -> "validado=" + f + ", ").orElse("") +
            optionalTipo().map(f -> "tipo=" + f + ", ").orElse("") +
            optionalFormato().map(f -> "formato=" + f + ", ").orElse("") +
            optionalDestino().map(f -> "destino=" + f + ", ").orElse("") +
            optionalLinhas().map(f -> "linhas=" + f + ", ").orElse("") +
            optionalChecksum().map(f -> "checksum=" + f + ", ").orElse("") +
            optionalVersaoId().map(f -> "versaoId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
