package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Formato;
import org.semprotdb.domain.enumeration.Tipo;

public class CargaDTO extends Carga implements IDTO<CargaDTO> {

    @JsonIgnore
    private String planilhaContentType;

    @JsonIgnore
    private String status;

    @JsonIgnore
    private byte[] planilha;

    @JsonIgnore
    private String caminho;

    @JsonIgnore
    private String checksum;

    public CargaDTO() {}

    public CargaDTO(
        Long id,
        Integer ordem,
        String nome,
        Boolean validado,
        Tipo tipo,
        Formato formato,
        Destino destino,
        Integer linhas,
        Long versaoID,
        String versaoNOME,
        Integer versaoNUMERO
    ) {
        setId(id);
        setOrdem(ordem);
        setNome(nome);
        setValidado(validado);
        setTipo(tipo);
        setFormato(formato);
        setDestino(destino);
        setLinhas(linhas);
        setVersao(new VersaoDTO.VersaoDTOmin(versaoID, versaoNOME, versaoNUMERO));
    }

    @Override
    public Path[] getConstructorArgsPath(Root<CargaDTO> root) {
        Join versao = root.join("versao");
        return new Path[] {
            root.get("id"),
            root.get("ordem"),
            root.get("nome"),
            root.get("validado"),
            root.get("tipo"),
            root.get("formato"),
            root.get("destino"),
            root.get("linhas"),
            versao.get("id"),
            versao.get("nome"),
            versao.get("numero"),
        };
    }
}
