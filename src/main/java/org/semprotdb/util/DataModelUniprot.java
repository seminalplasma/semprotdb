package org.semprotdb.util;

import java.util.ArrayList;
import java.util.List;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.Proteina;
import org.semprotdb.domain.Recurso;
import org.semprotdb.domain.enumeration.BioDB;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Formato;

public class DataModelUniprot extends AbstractDataModel {

    private static final String PROTEINA_COL = "PROTEIN";
    private static final String GENE_COL = "GENE";
    private static final String PESO_COL = "MASS";

    private static final String ID_COL = "FROM";
    private static final String ENTRY_COL = "ENTRY NAME";
    private static final String TAMANHO_COL = "LENGTH";

    DataSet mapDataSet = null;

    public DataModelUniprot(Carga carga) throws Exception {
        super(carga);
        if (carga.getFormato() == Formato.XLSX && tabela != null && tabela.countDataSets() == 1) mapDataSet = tabela.getDataSetByCols(
            new String[] { ID_COL, ENTRY_COL, GENE_COL, PESO_COL, PROTEINA_COL, TAMANHO_COL },
            false
        );
    }

    @Override
    public Destino verificar() {
        return mapDataSet != null ? Destino.MAPEAR : Destino.OUTRO;
    }

    public List<Proteina> asProteinas() throws Exception {
        ArrayList<Proteina> ptnas = new ArrayList<>();

        int from_idx = mapDataSet.getColByName(ID_COL);
        int entry_idx = mapDataSet.getColByName(ENTRY_COL);
        int length_idx = mapDataSet.getColByName(TAMANHO_COL);
        int protein_idx = mapDataSet.getColByName(PROTEINA_COL);
        int gene_idx = mapDataSet.getColByName(GENE_COL);
        int mass_idx = mapDataSet.getColByName(PESO_COL);

        for (String[] l : mapDataSet.getLinhas()) {
            String id = from_idx >= 0 && from_idx < l.length ? l[from_idx] : null;
            String acesso = BioDBParser.acesso(id);
            BioDB db = BioDBParser.acesso2db(acesso);

            if (db == BioDB.OUTRO) continue;

            String ptna = protein_idx >= 0 && protein_idx < l.length ? l[protein_idx] : null;
            String gene = gene_idx >= 0 && gene_idx < l.length ? l[gene_idx] : null;
            String massa = mass_idx >= 0 && mass_idx < l.length ? l[mass_idx] : null;

            Proteina p = new RowPtn("[" + carga.getId() + "]" + carga.getNome(), id, ptna, gene, massa, null, null, null);

            p.addRecurso(new Recurso().uid(acesso).db(db));

            String entry = entry_idx >= 0 && entry_idx < l.length ? l[entry_idx] : null;
            acesso = BioDBParser.acesso(entry);
            db = BioDBParser.acesso2db(acesso);
            if (db != BioDB.OUTRO) p.addRecurso(new Recurso().uid(acesso).db(db).link("ENTRY"));

            String length = length_idx >= 0 && length_idx < l.length ? l[length_idx] : null;

            if (length != null && length.matches("^\\d+$")) p.tamanho(Integer.parseInt(length));

            ptnas.add(p);
        }

        return ptnas;
    }
}
