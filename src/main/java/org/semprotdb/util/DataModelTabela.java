package org.semprotdb.util;

import java.util.ArrayList;
import java.util.List;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.Organismo;
import org.semprotdb.domain.Proteina;
import org.semprotdb.domain.Referencia;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Formato;

public class DataModelTabela extends AbstractDataModel {

    private static final String ORGANISMO_COL = "RGAN";
    private static final String REFERENCIA_COL = "REF";
    private static final String LINK_COL = "LINK";
    private static final String ID_COL = "ID";
    private static final String PESO_COL = "PESO";
    private static final String PROTEINA_COL = "PROTEIN";
    private static final String GENE_COL = "GENE";

    private DataSet dados;
    private DataSet metadados;

    private Organismo organismo;
    private Referencia referencia;
    private ArrayList<Proteina> proteinas;

    public DataModelTabela(final Carga carga) throws Exception {
        super(carga);
        if (carga.getFormato() == Formato.XLSX && tabela != null && tabela.countDataSets() > 1) {
            dados = tabela.getDataSetByCols(new String[] { ID_COL, PESO_COL, PROTEINA_COL, GENE_COL }, false);
            try {
                DataSet[] skip = dados == null ? new DataSet[] {} : new DataSet[] { dados };
                metadados = tabela.getDataSetByCols(new String[] { ORGANISMO_COL, REFERENCIA_COL, LINK_COL }, true, skip);
            } catch (Exception e) {
                if (dados == null) throw e;
                throw new Exception(
                    "Planilha de dados " + dados.count() + " linhas reconhecidas mas a de metadados não, " + e.getMessage()
                );
            }
        }
    }

    @Override
    public Destino verificar() {
        return dados != null && metadados != null ? Destino.DADOS : Destino.OUTRO;
    }

    public List<Proteina> getProteinas() throws Exception {
        if (organismo == null) {
            organismo = getOrganismo();
            referencia = getReferencia();
            proteinas = new ArrayList<>();

            int id_idx = dados.getColByName(ID_COL); /// UNSET
            int proteina_idx = dados.getColByName(PROTEINA_COL); /// ID
            int gene_idx = dados.getColByName(GENE_COL); /// UNKNOWN
            int massa_idx = dados.getColByName(PESO_COL); /// UNDEFINED
            if (massa_idx < 0) massa_idx = dados.getColByName("MASS");

            for (String[] l : dados.getLinhas()) {
                String id = id_idx >= 0 && id_idx < l.length ? l[id_idx] : null;
                String ptna = proteina_idx >= 0 && proteina_idx < l.length ? l[proteina_idx] : null;
                String gene = gene_idx >= 0 && gene_idx < l.length ? l[gene_idx] : null;
                String massa = massa_idx >= 0 && massa_idx < l.length ? l[massa_idx] : null;
                proteinas.add(new RowPtn(id, ptna, gene, massa, organismo, referencia));
            }
        }
        return proteinas;
    }

    private Organismo getOrganismo() throws Exception {
        int org_idx = metadados.getColByName(ORGANISMO_COL);
        String org = metadados.getLinhas().get(0)[org_idx];
        if (org == null || org.isBlank()) throw new Exception("Erro nome de organismo invalido ou vazio");
        org = org.strip().replaceAll("\\s+", " ").toUpperCase();
        return new Organismo().nome(org);
    }

    private Referencia getReferencia() throws Exception {
        int ref_idx = metadados.getColByName(REFERENCIA_COL);
        String ref = metadados.getLinhas().get(0)[ref_idx];
        if (ref == null || ref.isBlank()) ref = "NONE";
        ref = ref.strip().replaceAll("\\s+", " ").toUpperCase();

        int link_idx = metadados.getColByName(LINK_COL);
        String link = metadados.getLinhas().get(0)[link_idx];
        if (link == null || link.isBlank() || !link.contains("/")) link = "NULL";
        link = link.strip();
        return new Referencia().citacao(ref).link(link);
    }
}
