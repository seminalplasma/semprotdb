package org.semprotdb.util.FileIO;

import static java.nio.file.Files.readAllBytes;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import org.semprotdb.domain.enumeration.Formato;
import org.semprotdb.domain.enumeration.Tipo;
import org.semprotdb.util.DataSet;

public class TSV extends AbstractTabela {

    public static final Formato formato = Formato.TSV;
    public static final String ctype = "text/plain";

    public TSV(String nome, Tipo tipo, String caminho, byte[] dados) throws Exception {
        super(nome, tipo, caminho, parseBytes(nome, tipo, caminho, dados));
    }

    public TSV(String nome, Iterable<String> linhas) throws IOException {
        super(nome, formato, ctype);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (String line : linhas) {
            bos.write((line + "\n").getBytes());
            super.linhas++;
        }
        setDados(bos.toByteArray());
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
}
