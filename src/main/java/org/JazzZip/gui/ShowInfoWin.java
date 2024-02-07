package org.JazzZip.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ShowInfoWin extends JFrame {
    public ShowInfoWin(String f, JFrame frame) throws Exception {
        setTitle("文件信息");
        frame.setEnabled(false);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setSize(320, 250);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        BuildUI(f);
        setLocationRelativeTo(frame);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                frame.setEnabled(true);
                super.windowClosed(e);
            }
        });
    }

    private void BuildUI(String f) {
        JPanel panel = new JPanel(new BorderLayout());
        File selectedFile = new File(f);

        
        String fileInfo =
                "名称: " + selectedFile.getName() +
                "\n位置: " + selectedFile.getParent() +
                "\n大小: " + formatFileSize(selectedFile.length()) +
                "\n文件类型: " + selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.') + 1) +
                "\n创建时间: " + getCreationTime(selectedFile) +
                "\n更新时间: " + getLastModifiedTime(selectedFile) +
                "\n访问时间: " + getLastAccessTime(selectedFile);

        JTextArea textArea = new JTextArea(fileInfo);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
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

    private static String getCreationTime(File file) {
        BasicFileAttributes attr;
        try {
            Path path =  file.toPath();
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            throw new RuntimeException("get time wrong");
        }
        Instant instant = null;
        if (attr != null) {
            instant = attr.creationTime().toInstant();
        }
        String format = null;
        if (instant != null) {
            format = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(instant);
        }
        return format;
    }

    private static String getLastModifiedTime(File file) {
        BasicFileAttributes attr;
        try {
            Path path =  file.toPath();
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            throw new RuntimeException("get time wrong");
        }
        Instant instant = null;
        if (attr != null) {
            instant = attr.lastModifiedTime().toInstant();
        }
        String format = null;
        if (instant != null) {
            format = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(instant);
        }
        return format;
    }

    private static String getLastAccessTime(File file) {
        BasicFileAttributes attr;
        try {
            Path path =  file.toPath();
            attr = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            throw new RuntimeException("get time wrong");
        }
        Instant instant = null;
        if (attr != null) {
            instant = attr.lastAccessTime().toInstant();
        }
        String format = null;
        if (instant != null) {
            format = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(instant);
        }
        return format;
    }
}
