package org.semprotdb.util.FileIO;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.semprotdb.domain.enumeration.Tipo;
import org.semprotdb.util.DataSet;

public class Excel extends AbstractTabela {

    public static final int MAX_ABAS = 100;

    public Excel(String nome, Tipo tipo, String caminho, byte[] dados) throws Exception {
        super(nome, tipo, caminho, dados);
    }

    @Override
    public DataSet[] rawLines() throws Exception {
        ArrayList<DataSet> dataSets = new ArrayList<>();
        Workbook wb = getWorkbook();

        if (wb == null) throw new Exception("Falhou ao abrir o arquivo excel " + nome + ".");

        if (wb.getNumberOfSheets() > MAX_ABAS) throw new Exception(
            "A quantidade " + wb.getNumberOfSheets() + " em " + nome + " ultrapassou o limite (" + MAX_ABAS + ") de abas permitido."
        );

        for (int sheet = 0; sheet < wb.getNumberOfSheets(); sheet++) {
            String[] colunas = null;
            ArrayList<String[]> linhas = new ArrayList<>();
            for (Row row : wb.getSheetAt(sheet)) {
                String[] linha = getLinha(row);
                if (linha == null) continue;
                if (colunas == null) colunas = linha;
                else linhas.add(linha);
                if (linhas.size() > MAX_LINHAS) throw new Exception(
                    "A quantidade de linhas" + " em " + nome + " ultrapassou o limite (" + MAX_LINHAS + ") de linhas permitido."
                );
            }
            dataSets.add(new DataSet(colunas, linhas));
        }

        return dataSets.toArray(new DataSet[] {});
    }

    private Workbook getWorkbook() throws Exception {
        Workbook workbook = null;
        switch (tipo) {
            case ARQUIVO -> workbook = new XSSFWorkbook(new ByteArrayInputStream(dados));
            case CAMINHO -> workbook = new XSSFWorkbook(caminho);
            case REMOTO -> {
                try {
                    workbook = new XSSFWorkbook(new URL(caminho).openStream());
                } catch (Exception e) {
                    try {
                        workbook = new XSSFWorkbook(new ByteArrayInputStream(Zip.unzip(new URL(caminho).openStream())));
                    } catch (Exception e2) {
                        throw e;
                    }
                }
            }
        }
        return workbook;
    }

    private String[] getLinha(Row row) throws Exception {
        ArrayList<String> ln = new ArrayList<>(MAX_COLUNAS);
        boolean vazio = true;
        for (Cell cell : row) {
            String celula = null;

            switch (cell.getCellType()) {
                case STRING -> celula = cell.getRichStringCellValue().getString();
                case NUMERIC -> celula = cell.getNumericCellValue() + "";
            }
            celula = celula == null || celula.strip().isEmpty() ? null : celula.strip();

            if (celula != null && celula.length() > MAX_CHARS) {
                throw new Exception(
                    "A quantidade " +
                    celula.length() +
                    " em " +
                    nome +
                    " [" +
                    cell.getAddress() +
                    "] ultrapassou o limite (" +
                    MAX_CHARS +
                    ") de caracteres por celula permitido."
                );
            }

            int c = cell.getColumnIndex();
            if (c >= MAX_COLUNAS) throw new Exception(
                "A quantidade " +
                c +
                " em " +
                nome +
                " [" +
                cell.getAddress() +
                "] ultrapassou o limite (" +
                MAX_COLUNAS +
                ") de abas permitido."
            );

            if (c > ln.size()) for (int i = 0; i < c; i++) ln.add(null);

            ln.add(c, celula);
            if (celula != null) vazio = false;
        }
        return vazio ? null : ln.toArray(String[]::new);
    }
}
