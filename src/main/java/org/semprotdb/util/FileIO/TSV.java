package org.semprotdb.util.FileIO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.semprotdb.domain.Carga;
import org.semprotdb.domain.Versao;
import org.semprotdb.domain.enumeration.Formato;
import org.semprotdb.domain.enumeration.Tipo;
import org.semprotdb.util.DataSet;
import org.springframework.util.DigestUtils;

public class TSV extends AbstractTabela {

    private int linhas = 0;
    private String formato = "text/plain";
    private String md5 = null;

    public TSV(String nome, Tipo tipo, String caminho, byte[] dados) throws Exception {
        super(nome, tipo, caminho, dados);
    }

    public TSV(String nome, Iterable<String> linhas) throws IOException {
        super(nome);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (String line : linhas) {
            bos.write((line + "\n").getBytes());
            this.linhas++;
        }
        this.dados = bos.toByteArray();
        this.md5 = DigestUtils.md5DigestAsHex(dados);
    }

    public TSV zip() throws IOException {
        this.dados = Zip.bytes(this.nome, this.dados);
        this.md5 += "|" + DigestUtils.md5DigestAsHex(dados);
        this.nome += ".zip";
        this.formato = "application/zip";
        return this;
    }

    @Override
    public DataSet[] rawLines() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Carga toCarga(Versao versao) {
        return new Carga()
            .versao(versao)
            .nome(this.nome)
            .tipo(Tipo.ARQUIVO)
            .formato(Formato.TSV)
            .planilhaContentType(this.formato)
            .validado(true)
            .planilha(this.dados)
            .ordem(1)
            .linhas(this.linhas)
            .status(this.md5);
    }
}
