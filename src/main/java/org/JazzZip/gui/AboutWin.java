package org.JazzZip.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AboutWin extends JFrame {
    public AboutWin(JFrame frame, boolean isBook) throws Exception {

        frame.setEnabled(false);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        if (isBook) {
            setTitle("软件操作指南");
            setSize(600, 200);
            showScreen();
        } else {
            setTitle("软件信息");
            setSize(380, 250);
            showScreen2();
        }
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(frame);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                frame.setEnabled(true);
                super.windowClosed(e);
            }
        });
    }

    private void showScreen() {
        String info = """
                新建压缩文件：点击 "文件" -> "新建压缩包" -> "输入压缩包的相关信息" -> "确定"
                打开压缩文件：点击 "文件" -> "打开压缩包" -> "选择压缩包的所在位置" -> "打开"
                添加压缩文件(需要在打开压缩包后): 点击 "编辑" -> "添加文件" -> "在压缩包文件预览框中选择需要删除的文件夹(不选默认根目录), 再选择需要添加的文件" -> "添加"
                删除压缩文件(需要在打开压缩包后): 点击 "编辑" -> "删除文件" -> "在压缩包文件预览框中选择需要删除的文件或者文件夹" -> "删除"
                重命名压缩文件(需要在打开压缩包后): 点击 "编辑" -> "重新命名" -> "在压缩包文件预览框中选择需要重命名的文件或者文件夹" -> "重命名"
                移动压缩文件(需要在打开压缩包后): "在压缩包文件预览框中选择需要重命名的文件或者文件夹" -> "按下Ctrl + D" -> "再次选择要移动那个文件夹中" -> "按下Ctrl + B"
                """;

        JPanel panel = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea(info);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }

    private void showScreen2() {
        String info = """
                软件名称: JazzZip
                软件所使用的Java版本: Java17
                软件所使用到的库: swing, awt, zip4j, commons-io
                """;

        JPanel panel = new JPanel(new BorderLayout());

        JTextArea textArea = new JTextArea(info);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }
}
