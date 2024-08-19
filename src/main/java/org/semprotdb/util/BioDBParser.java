package org.semprotdb.util;

import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.semprotdb.domain.Proteina;
import org.semprotdb.domain.Recurso;
import org.semprotdb.domain.enumeration.BioDB;

public class BioDBParser {

    public static final String NO_ID = "UNSET";
    private static final Pattern p_rs = Pattern.compile("([NX]P_\\d{3,9}\\.?\\d{1,3})");
    private static final Pattern p_gi = Pattern.compile("(GI\\|\\d{3,9})|(GI\\d{3,9})|(^\\d{2,9}$)");
    private static final Pattern p_up = Pattern.compile("(\\w{3,6}_?\\w{3,6})");

    public static String acesso(final String id) {
        String acesso = NO_ID;
        if (id != null && !id.trim().isBlank()) {
            acesso = id.trim().toUpperCase();
            MatchResult matchResult = p_rs
                .matcher(acesso)
                .results()
                .findFirst()
                .orElse(p_gi.matcher(acesso).results().findFirst().orElse(p_up.matcher(acesso).results().findFirst().orElse(null)));
            acesso = matchResult == null ? NO_ID : matchResult.group();

            if (acesso.matches("^GI\\|?\\d+$")) acesso = acesso.replaceAll("\\D", "");
        }
        return acesso;
    }

    public static BioDB acesso2db(final String acesso) {
        if (acesso == null || acesso.trim().isBlank() || acesso.equals(NO_ID)) return BioDB.OUTRO;
        if (p_rs.matcher(acesso).matches()) return BioDB.REFSEQ;
        if (p_gi.matcher(acesso).matches()) return BioDB.GI;
        if (p_up.matcher(acesso).matches()) return BioDB.UNIPROT;
        return BioDB.OUTRO;
    }

    public static Recurso acesso2recurso(final String uid) {
        String acesso = acesso(uid);
        if (acesso == null || acesso.isBlank() || acesso.equals(NO_ID)) return null;
        BioDB db = acesso2db(acesso);
        if (db == null || db == BioDB.OUTRO) return null;
        Recurso r = new Recurso().uid(acesso).db(db);
        r.setLink(recurso2link(r));
        return r;
    }

    public static String recurso2link(final Recurso recurso) {
        return switch (recurso.getDb()) {
            case GI -> "https://www.ncbi.nlm.nih.gov/gene/" + recurso.getUid(); ////286862
            case REFSEQ -> "https://www.ncbi.nlm.nih.gov/protein/" + recurso.getUid(); ///NP_777218.2
            case UNIPROT -> "https://www.uniprot.org/uniprotkb/" + recurso.getUid() + "/entry"; ///A7Z057
            default -> null;
        };
    }

    public static Recurso recursos2stringdb(final Proteina proteina) {
        ///https://string-db.org/cgi/network?identifier=A7Z057
        ///https://string-db.org/cgi/network?identifier=286862
        ///https://string-db.org/cgi/network?identifier=NP_777218.2
        for (Recurso recurso : proteina.getRecursos()) {
            switch (recurso.getDb()) {
                case UNIPROT, REFSEQ, GI:
                    return new Recurso()
                        .addProteina(proteina)
                        .uid("String-db:" + recurso.getUid())
                        .db(BioDB.STRINGDB)
                        .link("https://string-db.org/cgi/network?identifier=" + recurso.getUid());
            }
        }
        return null;
    }

    public static Recurso recursos2kegg(final Proteina proteina) {
        if (
            proteina == null ||
            proteina.getGene() == null ||
            proteina.getGene().getOrganismo() == null ||
            proteina.getGene().getOrganismo().getSigla() == null ||
            proteina.getGene().getOrganismo().getSigla().length() != 3
        ) return null;
        ///https://www.genome.jp/dbget-bin/www_bget?bta:286862
        for (Recurso recurso : proteina.getRecursos()) {
            if (Objects.requireNonNull(recurso.getDb()) == BioDB.GI) {
                return new Recurso()
                    .addProteina(proteina)
                    .uid("Kegg:" + recurso.getUid())
                    .db(BioDB.KEGG)
                    .link( ////https://www.genome.jp/dbget-bin/www_bget?mmu:235674
                        "https://www.genome.jp/dbget-bin/www_bget?" +
                        proteina.getGene().getOrganismo().getSigla().toLowerCase() +
                        ":" +
                        recurso.getUid()
                    );
            }
        }
        return null;
    }

    public static Recurso[] recursos2recursos(final Proteina proteina) {
        return new Recurso[] { BioDBParser.recursos2stringdb(proteina), BioDBParser.recursos2kegg(proteina) };
    }
}
