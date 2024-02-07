package org.JazzZip;
import org.JazzZip.gui.MainWin;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Path baseDir = Paths.get(System.getProperty("user.dir") + "\\temp");
            System.out.println(baseDir);
            try {
                FileUtils.deleteDirectory(baseDir.toFile());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }));

        CheckTempPathExists();
        MainWin.MainWindow();
    }
}
