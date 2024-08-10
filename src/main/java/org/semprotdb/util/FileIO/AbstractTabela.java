package org.semprotdb.util.FileIO;

import java.io.IOException;
import org.semprotdb.domain.enumeration.Tipo;
import org.semprotdb.util.DataSet;

public abstract class AbstractTabela {

    public static final int MAX_LINHAS = 100000;
    public static final int MAX_COLUNAS = 100;
    public static final int MAX_CHARS = 10000;

    protected String nome;
    protected Tipo tipo;
    protected String caminho;
    protected byte[] dados;
    protected DataSet[] dataSets;

    public AbstractTabela(String nome) throws IOException {
        this.nome = nome;
    }

    public AbstractTabela(String nome, Tipo tipo, String caminho, byte[] dados) throws Exception {
        this.nome = nome;
        this.tipo = tipo;
        this.caminho = caminho;
        this.dados = dados;
        this.dataSets = rawLines();
    }

    protected abstract DataSet[] rawLines() throws Exception;

    public int countLines() {
        if (dataSets == null || dataSets.length == 0) return 0;
        int total = 0;
        for (DataSet dataSet : dataSets) {
            total += dataSet.count();
        }
        return total;
    }

    public int countDataSets() {
        return dataSets == null ? 0 : dataSets.length;
    }

    public DataSet getDataSetByCols(String[] cols, boolean pivot, DataSet[] skip) throws Exception {
        if (dataSets == null || dataSets.length == 0 || cols == null || cols.length == 0) return null;
        for (DataSet dataSet : dataSets) {
            boolean cnt = false;
            for (DataSet d : skip) {
                if (d.equals(dataSet)) {
                    cnt = true;
                    break;
                }
            }
            if (cnt) {
                continue;
            }
            dataSet = pivot ? dataSet.pivot() : dataSet;
            boolean is = true;
            for (String col : cols) {
                if (dataSet.getColByName(col) < 0) is = false;
            }
            if (is) return dataSet;
        }
        return null;
    }

    public DataSet getDataSetByCols(String[] cols, boolean pivot) throws Exception {
        return getDataSetByCols(cols, pivot, new DataSet[] {});
    }
}
