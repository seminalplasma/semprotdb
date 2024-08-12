package org.semprotdb.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.semprotdb.domain.Proteina;

public class ProteinaDTO extends Proteina implements IDTO<ProteinaDTO> {

    @JsonIgnore
    private String nome;

    private CuradoriaDTO curadoria;

    private VersaoDTO.VersaoDTOmin versao;

    private GeneDTO gene;

    public ProteinaDTO() {}

    public ProteinaDTO(
        Long id,
        String descricao,
        Integer tamanho,
        String massa,
        Long curadoriaID,
        Long versaoID,
        String versaoNOME,
        Long geneID,
        String geneNOME,
        Long gene_curadoriaID,
        Long gene_organismoID,
        String gene_organismoAPELIDO
    ) {
        setId(id);
        setTamanho(tamanho);
        setMassa(massa);
        setDescricao(descricao);
        setCuradoria(new CuradoriaDTO(curadoriaID));
        setVersao(new VersaoDTO.VersaoDTOmin(versaoID, versaoNOME));
        setGene(new GeneDTO(geneID, geneNOME, gene_curadoriaID, gene_organismoID, gene_organismoAPELIDO));
    }

    @Override
    public Path[] getConstructorArgsPath(Root<ProteinaDTO> root) {
        Join curadoria = root.join("curadoria", JoinType.LEFT);
        Join versao = root.join("versao");
        Join gene = root.join("gene", JoinType.LEFT);
        Join gene_curadoria = gene.join("curadoria", JoinType.LEFT);
        Join organismo = gene.join("organismo", JoinType.LEFT);

        return new Path[] {
            root.get("id"),
            root.get("descricao"),
            root.get("tamanho"),
            root.get("massa"),
            curadoria.get("id"),
            versao.get("id"),
            versao.get("nome"),
            gene.get("id"),
            gene.get("nome"),
            gene_curadoria.get("id"),
            organismo.get("id"),
            organismo.get("apelido"),
        };
    }
}
