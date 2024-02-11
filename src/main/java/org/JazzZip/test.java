package org.JazzZip;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.util.List;

public class test {
    public static void main(String[] argv) throws Exception {
        ZipFile zipFile = new ZipFile("E:\\Godotpro\\Godotpro.zip.001");
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        System.out.println(fileHeaders.get(0).getFileName());

    }
}
