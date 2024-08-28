package org.semprotdb.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.semprotdb.domain.*;
import org.semprotdb.domain.enumeration.BioDB;

public class RowPtn extends Proteina {

    private static final String NO_GENE = "UNKNOWN";
    private static final String NO_MASS = "UNDEFINED";
    private static final String NO_CURATOR = "X";

    public RowPtn(
        String carga,
        String id,
        String ptna,
        String gene,
        String peso,
        Organismo organismo,
        Referencia referencia,
        String curador
    ) throws Exception {
        String _id = id;
        id = BioDBParser.acesso(id);
        ptna = limpar(ptna, id, false);
        if (ptna.equals(BioDBParser.NO_ID)) throw new Exception(
            "O id ou o nome da proteina deve ser informado para " + id + " em " + carga
        );
        gene = limpar(gene, NO_GENE, true);
        peso = limpar(peso, NO_MASS, true);

        if (id.length() > 200) id = id.substring(0, 200);
        if (ptna.length() > 200) ptna = ptna.substring(0, 200);
        if (gene.length() > 200) gene = gene.substring(0, 200);
        if (peso.length() > 200) peso = peso.substring(0, 200);

        BioDB db = BioDBParser.acesso2db(id);
        nome(id);
        massa(peso);
        descricao(("" + ptna).trim());
        addRecurso(new Recurso().uid(db == BioDB.OUTRO ? _id : id).db(db));
        if (referencia != null) addReferencia(referencia);
        gene(new Gene().nome(gene).organismo(organismo));
        if (curador != null) setCuradoria(new Curadoria().email(curador));
    }

    /// essa funcao é para ser usada na importacao da ptna do uniprot map ids
    //    public RowPtn(String carga, String ptna, String gene, String peso) throws Exception {
    //        this(carga, null, ptna, gene, peso, null, null, null);
    //    }

    private static String limpar(String s, String o, boolean up) {
        if (s == null) return o;
        s = s.strip().replaceAll("\\s+", " ").replace("---", "");
        s = up ? s.toUpperCase() : s;
        return !s.isEmpty() ? s : o;
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

            uids.addAll(d.getRecursos().stream().filter(r -> r.getDb() != BioDB.OUTRO).map(Recurso::getUid).collect(Collectors.toSet()));

            for (String uid : uids) {
                Proteina p_uniprot = M.get(uid);
                /// resgatar do uniprot
                if (p_uniprot != null) {
                    ///se mapear no uniprtot substituir o uid pelo do uniprot para n ficar redundandte
                    p_uniprot
                        .getRecursos()
                        .stream()
                        .filter(r -> "ENTRY".equals(r.getLink()))
                        .map(Recurso::getUid)
                        .findAny()
                        .ifPresent(d::setNome);

                    /// nome da ptna
                    if (p_uniprot.getDescricao().length() > 3 && !p_uniprot.getDescricao().equalsIgnoreCase("deleted")) d.setDescricao(
                        p_uniprot.getDescricao()
                    );

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
