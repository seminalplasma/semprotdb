package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Formato;
import org.semprotdb.domain.enumeration.Tipo;

public class CargaDTO extends Carga {

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
        String versaoNOME
    ) {
        setId(id);
        setOrdem(ordem);
        setNome(nome);
        setValidado(validado);
        setTipo(tipo);
        setFormato(formato);
        setDestino(destino);
        setLinhas(linhas);
        setVersao(new VersaoDTO.VersaoDTOmin(versaoID, versaoNOME));
    }
}
