package org.semprotdb.util.FileIO;

import static java.nio.file.Files.readAllBytes;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
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
        super(nome, tipo, caminho, parseBytes(nome, tipo, caminho, dados));
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

    private static byte[] parseBytes(String nome, Tipo tipo, String caminho, byte[] dados) throws Exception {
        ByteArrayInputStream bais = null;
        switch (tipo) {
            case ARQUIVO -> bais = new ByteArrayInputStream(dados);
            case CAMINHO -> bais = new ByteArrayInputStream(readAllBytes(new File(caminho).toPath()));
            case REMOTO -> bais = new ByteArrayInputStream(new URL(caminho).openStream().readAllBytes());
        }
        if (bais != null && nome.endsWith(".zip")) bais = new ByteArrayInputStream(Zip.unzip(bais));
        return bais == null ? null : bais.readAllBytes();
    }

    public TSV zip() throws IOException {
        this.dados = Zip.bytes(this.nome, this.dados);
        this.md5 += "|" + DigestUtils.md5DigestAsHex(dados);
        this.nome += ".zip";
        this.formato = "application/zip";
        return this;
    }

    @Override
    public DataSet[] rawLines() throws Exception {
        ArrayList<String[]> linhas = new ArrayList<>();
        String[] colunas = null;
        ByteArrayInputStream bd = new ByteArrayInputStream(this.dados);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bd));
        while (reader.ready()) {
            String line = reader.readLine();
            if (line == null) continue;
            line = line.strip();
            if (line.isEmpty()) continue;
            if (line.length() > 10000) throw new Exception(
                "Linha " + (linhas.size() + 1) + "tem tamanho " + line.length() + " muito grande."
            );
            if (colunas == null) colunas = line.split("\t");
            else linhas.add(line.split("\t"));
        }
        return new DataSet[] { new DataSet(colunas, linhas) };
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
            .linhas(this.linhas)
            .checksum(this.md5)
            .status("OK");
    }
}
