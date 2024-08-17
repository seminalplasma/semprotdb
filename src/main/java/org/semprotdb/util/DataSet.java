package org.semprotdb.util;

import java.util.ArrayList;
import org.semprotdb.util.FileIO.AbstractTabela;

public class DataSet {

    private final ArrayList<String[]> linhas;
    private final String[] colunas;

    public DataSet(String[] colunas, ArrayList<String[]> linhas) {
        this.linhas = linhas;
        this.colunas = colunas;
    }

    public int getColByName(String nome) throws Exception {
        int pos = -1;
        String col = "";
        for (int i = 0; i < colunas.length; i++) {
            String coluna = colunas[i];
            if (coluna != null && !coluna.isEmpty() && colunas[i].toUpperCase().contains(nome)) {
                if (pos < 0) {
                    pos = i;
                    col = coluna;
                } else if (coluna.compareToIgnoreCase(col) < 0) {
                    int a = 0;
                    int b = 0;
                    for (String[] l : linhas) {
                        a += l[pos] == null ? 0 : 1;
                        b += l[i] == null ? 0 : 1;
                    }
                    if (a > 0 && b > 0) throw new Exception(
                        "Coluna " + nome + " => \"" + coluna + "\" duplicada " + pos + "_" + a + " /e/ " + i + "_" + a + "."
                    );
                    else pos = a > 0 ? a : b;
                }
            }
        }
        return pos;
    }

    public int count() {
        return linhas.size();
    }

    public DataSet pivot() {
        ArrayList<String[]> lns = new ArrayList<>();
        for (int i = 0; i < AbstractTabela.MAX_COLUNAS; i++) {
            String[] l = new String[1 + linhas.size()];
            l[0] = i < colunas.length ? colunas[i] : null;
            for (int j = 0; j < linhas.size(); j++) {
                l[j + 1] = i < linhas.get(j).length ? linhas.get(j)[i] : null;
            }
            for (String v : l) {
                if (v != null) {
                    lns.add(l);
                    break;
                }
            }
        }
        String[] cols = lns.remove(0);
        return new DataSet(cols, lns);
    }

    public ArrayList<String[]> getLinhas() {
        return linhas;
    }

    public String[] getColunas() {
        return colunas;
    }
}
