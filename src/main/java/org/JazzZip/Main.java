package org.JazzZip;
import org.JazzZip.gui.MainWin;

import java.io.File;

public class Main {
    private static void deleteContents(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteContents(file);
                        boolean deleted = file.delete();
                        if (!deleted) {
                            System.out.println("Failed to delete file: " + file.getAbsolutePath());
                        }
                    } else {
                        boolean deleted = file.delete();
                        if (!deleted) {
                            System.out.println("Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            System.out.println("Invalid directory: " + directory.getAbsolutePath());
        }
    }

    private static void CheckTempPathExists() {
        File TempFolder = new File(System.getProperty("user.dir") + "\\temp");
        if (!TempFolder.exists()) {
            boolean success = TempFolder.mkdir();
            if (!success) {
                throw new RuntimeException("文件夹创建失败, 可能为系统权限有关");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteContents(new File(System.getProperty("user.dir") + "\\temp"))));

        CheckTempPathExists();
        MainWin.MainWindow();
    }
}
