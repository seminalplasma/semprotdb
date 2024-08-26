package org.semprotdb.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.semprotdb.domain.*;
import org.semprotdb.domain.enumeration.BioDB;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.domain.enumeration.Formato;

public class DataModelRecover extends AbstractDataModel {

    private final String oCol = "ORGANISM";
    private final String gCol = "GENE";
    private final String pCol = "PROTEIN";
    private final String lCol = "LENGTH";
    private final String mCol = "MASS";
    private final String rCol = "REFERENCE";
    private final String eCol = "ENTRY";
    private final String cCol = "CURATOR";
    DataSet restoreDataSet = null;

    public DataModelRecover(Carga carga) throws Exception {
        super(carga);
        if (carga.getFormato() == Formato.TSV && tabela != null && tabela.countDataSets() == 1) restoreDataSet = tabela.getDataSetByCols(
            new String[] { oCol, gCol, pCol, lCol, mCol, rCol, eCol, cCol },
            false
        );
    }

    @Override
    public Destino verificar() {
        return restoreDataSet != null ? Destino.RESTORE : Destino.OUTRO;
    }

    @Override
    public List<Proteina> asProteinas() throws Exception {
        if (restoreDataSet == null || restoreDataSet.count() < 1) return List.of();
        ArrayList<Proteina> proteinas = new ArrayList<>();
        int oIDX = restoreDataSet.getColByName(oCol);
        int gIDX = restoreDataSet.getColByName(gCol);
        int pIDX = restoreDataSet.getColByName(pCol);
        int lIDX = restoreDataSet.getColByName(lCol);
        int mIDX = restoreDataSet.getColByName(mCol);
        int rIDX = restoreDataSet.getColByName(rCol);
        int eIDX = restoreDataSet.getColByName(eCol);
        int cIDX = restoreDataSet.getColByName(cCol);

        String colerr = "";
        String a = "+ coluna ";
        String b = " NÃO encontrado";

        if (oIDX < 0) colerr += a + oCol + b;
        if (gIDX < 0) colerr += a + gCol + b;
        if (pIDX < 0) colerr += a + pCol + b;
        if (lIDX < 0) colerr += a + lCol + b;
        if (mIDX < 0) colerr += a + mCol + b;
        if (rIDX < 0) colerr += a + rCol + b;
        if (eIDX < 0) colerr += a + eCol + b;
        if (cIDX < 0) colerr += a + cCol + b;

        if (!colerr.isEmpty()) throw new Exception("Errors " + colerr);
        AtomicLong ids = new AtomicLong(1);

        for (String[] row : restoreDataSet.getLinhas()) {
            String reference = row.length > rIDX ? row[rIDX] : null;
            if (reference == null || reference.trim().isEmpty()) continue;
            HashSet<Referencia> refs = new HashSet<>();
            for (String ref : reference.trim().split("&")) {
                String r = ref.strip();
                if (r.length() > 3) refs.add(new Referencia().citacao(r.toUpperCase()).id(ids.getAndIncrement())); /// YU2010
            }

            String organismo = row.length > oIDX ? row[oIDX] : null;
            if (organismo == null || organismo.trim().isEmpty()) continue;
            Organismo org = new Organismo().nome(organismo.toUpperCase()).apelido(organismo);

            String g = row.length > gIDX ? row[gIDX] : null;
            if (g == null || g.trim().isEmpty()) continue;
            Gene gene = new Gene().nome(g.toUpperCase()).descricao(g).organismo(org);

            String curator = row.length > cIDX ? row[cIDX] : null;
            if (curator == null || curator.trim().isEmpty()) continue;
            if (curator.length() > 1 && curator.contains("G")) gene.setCuradoria(new Curadoria().email(curator.split("G")[1].trim()));

            String length = row.length > lIDX ? row[lIDX] : null;
            int tamanho;
            try {
                tamanho = length != null ? Integer.parseInt(length) : 0;
            } catch (Exception e) {
                tamanho = -1;
            }

            String mass = row.length > mIDX ? row[mIDX] : null;

            String ctp = curator.length() > 1 ? curator.split("G")[0] : null;
            Curadoria cp = null;
            if (ctp != null && !ctp.trim().isEmpty() && ctp.length() > 1) cp = new Curadoria().email(ctp);

            String protein = row.length > pIDX ? row[pIDX] : null;
            if (protein == null || protein.trim().isEmpty()) continue;
            proteinas.add(
                new Proteina()
                    .nome(proteinas.size() + "> " + protein.toUpperCase())
                    .descricao(protein)
                    .gene(gene)
                    .massa(mass)
                    .tamanho(tamanho)
                    .referencias(refs)
                    .curadoria(cp)
                    .recursos(
                        Arrays.stream((row.length > eIDX && row[eIDX] != null ? row[eIDX] : "").split(";"))
                            .filter(Objects::nonNull)
                            .filter(x -> x.length() > 3)
                            .map(r -> r.trim().toUpperCase().split(":"))
                            .map(r -> {
                                if ("String-db".toUpperCase().equals(r[0])) return new Recurso()
                                    .uid("String-db:" + r[1].trim())
                                    .db(BioDB.STRINGDB)
                                    .id(ids.getAndIncrement());
                                if ("Kegg".toUpperCase().equals(r[0])) return new Recurso()
                                    .uid("Kegg:" + r[1].trim())
                                    .db(BioDB.KEGG)
                                    .id(ids.getAndIncrement());
                                BioDB db = BioDBParser.acesso2db(r[0]);
                                if (db != BioDB.OUTRO) return new Recurso().uid(r[0].trim()).db(db).id(ids.getAndIncrement());
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet())
                    )
            );
        }
        return proteinas;
    }
}
