package org.semprotdb.util.FileIO;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {

    public static void file(String sourceFile, String zipFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        zipOut.close();
        fis.close();
        fos.close();
    }

    public static byte[] bytes(String nome, byte[] in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bos);

        ByteArrayInputStream fis = new ByteArrayInputStream(in);
        ZipEntry zipEntry = new ZipEntry(nome);
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        zipOut.close();
        fis.close();
        return bos.toByteArray();
    }

    public static byte[] unzip(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(in);
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            //                String fileName = ze.getName();
            int len;
            while ((len = zis.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            bos.close();
            zis.closeEntry();
            ze = zis.getNextEntry();
        }
        //close last ZipEntry
        zis.closeEntry();
        zis.close();
        in.close();
        return bos.toByteArray();
    }
}
