package org.JazzZip.gui;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class ShowFileInfoWin extends JFrame {
    public ShowFileInfoWin(ZipFile zipFile, JFrame frame, String node) throws Exception {
        setTitle("文件信息");
        frame.setEnabled(false);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setSize(320, 200);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        BuildUI(zipFile, node);
        setLocationRelativeTo(frame);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
            frame.setEnabled(true);
            super.windowClosed(e);
            }
        });
    }

    private void BuildUI(ZipFile zipFile, String node) throws Exception {
        JPanel panel = new JPanel(new BorderLayout());

        FileHeader zipFileInfo = getFile(zipFile, node);

        String fileInfo = null;
        if (zipFileInfo != null) {
            fileInfo = "文件名称: " + zipFileInfo.getFileName() +
            "\n压缩后大小: " + formatFileSize(zipFileInfo.getCompressedSize()) +
            "\n解压后大小: " + formatFileSize(zipFileInfo.getUncompressedSize()) +
            "\n压缩方法: " + zipFileInfo.getCompressionMethod();
        }

        JTextArea textArea = new JTextArea(fileInfo);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }

    private static FileHeader getFile(ZipFile zipFile, String filename) throws ZipException {
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        for (int i = 0; i < fileHeaders.size(); i++) {
            if (Objects.equals(fileHeaders.get(i).getFileName(), filename)){
                return fileHeaders.get(i);
            }
        }
        return null;
    }

    private static String formatFileSize(long size) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double fileSize = size;

        while (fileSize > 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }
}
