package org.semprotdb.util;

import org.semprotdb.domain.Carga;
import org.semprotdb.domain.enumeration.Destino;
import org.semprotdb.util.FileIO.AbstractTabela;
import org.semprotdb.util.FileIO.Excel;
import org.semprotdb.util.FileIO.TSV;

public abstract class AbstractDataModel {

    protected final Carga carga;
    private Destino destino;
    protected AbstractTabela tabela;

    AbstractDataModel(final Carga carga) throws Exception {
        this.carga = carga;
        switch (carga.getFormato()) {
            case XLSX -> tabela = new Excel(carga.getNome(), carga.getTipo(), carga.getCaminho(), carga.getPlanilha());
            case TSV -> tabela = new TSV(carga.getNome(), carga.getTipo(), carga.getCaminho(), carga.getPlanilha());
        }
    }

    public boolean validar(int ordem) {
        this.destino = verificar();
        this.destino = this.destino == null ? Destino.OUTRO : this.destino;
        if (this.destino != Destino.OUTRO) {
            carga.setValidado(true);
            carga.setDestino(this.destino);
            carga.setLinhas(tabela.countLines());
            carga.setStatus("OK");
            carga.setOrdem(ordem);
            return true;
        }
        return false;
    }

    public abstract Destino verificar();
}
