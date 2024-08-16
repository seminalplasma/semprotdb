package org.semprotdb.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.semprotdb.domain.*;
import org.semprotdb.domain.enumeration.BioDB;

public class RowPtn extends Proteina {

    private static final String NO_GENE = "UNKNOWN";
    private static final String NO_MASS = "UNDEFINED";

    public RowPtn(String id, String ptna, String gene, String peso, Organismo organismo, Referencia referencia) throws Exception {
        String _id = id;
        id = BioDBParser.acesso(id);
        ptna = limpar(ptna, id, false);
        if (ptna.equals(BioDBParser.NO_ID)) throw new Exception("O id ou o nome da proteina deve ser informado para " + id + ".");
        if (ptna.length() > 200) throw new Exception("O nome deve ter no MAX 200 carac, mas teve " + ptna.length() + " => " + ptna);
        gene = limpar(gene, NO_GENE, true);
        peso = limpar(peso, NO_MASS, true);
        BioDB db = BioDBParser.acesso2db(id);
        nome(ptna.toUpperCase());
        massa(peso);
        descricao(ptna);
        addRecurso(new Recurso().uid(db == BioDB.OUTRO ? _id : id).db(db));
        if (referencia != null) addReferencia(referencia);
        gene(new Gene().nome(gene).organismo(organismo));
    }

    public RowPtn(String ptna, String gene, String peso) throws Exception {
        this(null, ptna, gene, peso, null, null);
    }

    private static String limpar(String s, String o, boolean up) {
        if (s == null) return o;
        s = s.strip().replaceAll("\\s+", " ");
        s = up ? s.toUpperCase() : s;
        return s.length() > 0 ? s : o;
    }

    public static int join(Iterable<Proteina> dados, Iterable<Proteina> map) {
        HashMap<String, Proteina> M = new HashMap<>();
        map.forEach(px ->
            px
                .getRecursos()
                .forEach(r -> {
                    if (r.getDb() != BioDB.OUTRO) M.put(r.getUid(), px);
                }));
        int cont = 0;
        for (Proteina d : dados) {
            HashSet<String> uids = new HashSet<>();

            /// se a ptna nao tem nome vem o acesso
            uids.add(d.getNome());

            uids.addAll(d.getRecursos().stream().filter(r -> r.getDb() != BioDB.OUTRO).map(r -> r.getUid()).collect(Collectors.toSet()));

            for (String uid : uids) {
                Proteina p_uniprot = M.get(uid);
                /// resgatar do uniprot
                if (p_uniprot != null) {
                    /// nome da ptna
                    if (d.getNome().equals(uid)) d.setNome(p_uniprot.getNome());

                    /// nome do gene
                    if (d.getGene().getNome().equals(NO_GENE)) d.getGene().setNome(p_uniprot.getGene().getNome());

                    /// massa
                    if (d.getMassa().equals(NO_MASS)) d.setMassa(p_uniprot.getMassa());

                    /// tamanho da ptna
                    d.setTamanho(p_uniprot.getTamanho());

                    cont++;
                }
            }
        }
        return cont;
    }
}
