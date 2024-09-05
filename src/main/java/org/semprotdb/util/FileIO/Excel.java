package org.semprotdb.util.FileIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.semprotdb.domain.enumeration.Formato;
import org.semprotdb.domain.enumeration.Tipo;
import org.semprotdb.util.DataSet;

public class Excel extends AbstractTabela {

    public static final Formato formato = Formato.XLSX;
    public static final String ctype = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final int W_PQ = 4000;
    public static final int W_MD = 6000;
    public static final int W_LG = 12000;
    public static final int W_XLG = 20000;

    public static final int MAX_ABAS = 100;

    public Excel(String nome, Tipo tipo, String caminho, byte[] dados) throws Exception {
        super(nome, tipo, caminho, dados);
    }

    public Excel(String nome, HashMap<String, DataSet> sheets, int[] cols) throws Exception {
        super(nome, formato, ctype);
        Workbook workbook = new XSSFWorkbook();

        int linhas = 0;

        for (String sheetName : sheets.keySet()) {
            DataSet dataSet = sheets.get(sheetName);
            Sheet sheet = workbook.createSheet(sheetName);

            if (cols != null) for (int i = 0; i < cols.length; i++) sheet.setColumnWidth(i, cols[i]);

            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < dataSet.getColunas().length; i++) {
                Cell headerCell = header.createCell(i);
                headerCell.setCellValue(dataSet.getColunas()[i]);
                headerCell.setCellStyle(headerStyle);
            }

            XSSFFont font2 = ((XSSFWorkbook) workbook).createFont();
            font2.setFontName("Arial");
            font2.setFontHeightInPoints((short) 12);

            CellStyle style = workbook.createCellStyle();
            style.setFont(font2);
            style.setWrapText(true);

            for (int i = 0; i < dataSet.getLinhas().size(); i++) {
                Row row = sheet.createRow(i + 1);
                linhas++;
                String[] r = dataSet.getLinhas().get(i);
                for (int j = 0; j < r.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(r[j]);
                    cell.setCellStyle(style);
                }
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        setDados(outputStream.toByteArray());
        this.linhas = linhas;
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
            case ARQUIVO -> workbook = dados == null ? null : new XSSFWorkbook(new ByteArrayInputStream(dados));
            case CAMINHO -> workbook = caminho == null ? null : new XSSFWorkbook(caminho);
            case REMOTO -> {
                try {
                    workbook = caminho == null ? null : new XSSFWorkbook(new URL(caminho).openStream());
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
                case NUMERIC -> {
                    celula = BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
                    if (celula.endsWith(".0")) celula = celula.replace(".0", "");
                }
            }
            celula = celula == null || celula.isBlank() ? null : celula.strip();

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
