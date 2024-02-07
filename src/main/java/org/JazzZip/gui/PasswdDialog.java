package org.JazzZip.gui;

import net.lingala.zip4j.ZipFile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PasswdDialog extends JDialog {
    private static JPasswordField passwordField;

    public PasswdDialog(JFrame parent, ZipFile zipFile) throws Exception {
        super(parent, "输入密码", true);
        parent.setEnabled(false);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setSize(300, 150);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        buildUI(zipFile);

        setLocationRelativeTo(parent);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
            parent.setEnabled(true);
            super.windowClosed(e);
            }
        });
    }

    private void buildUI(ZipFile zipFile) {
        JPanel mainPanel = new JPanel(new FlowLayout());
        mainPanel.setBorder(new EmptyBorder(13, 0, 0, 0));

        JPanel inputPanel = new JPanel(new BorderLayout());

        passwordField = new JPasswordField();
        passwordField.setColumns(25);
        passwordField.setEchoChar('*');
        inputPanel.add(passwordField, BorderLayout.NORTH);

        JCheckBox checkBox = new JCheckBox("显示密码");
        checkBox.setFocusable(false);
        checkBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                passwordField.setEchoChar((char)0);
            } else {
                passwordField.setEchoChar('*');
            }
        });
        inputPanel.add(checkBox);
        mainPanel.add(inputPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton button1 = new JButton("确定");
        button1.setFocusable(false);
        button1.addActionListener(e -> getPasswordAction(zipFile));

        JButton button2 = new JButton("取消");
        button2.setFocusable(false);
        button2.addActionListener(e -> noPasswordAction());

        buttonPanel.add(button1);
        buttonPanel.add(button2);

        mainPanel.add(buttonPanel);
        add(mainPanel);
    }

    private void getPasswordAction(ZipFile zipFile) {
        zipFile.setPassword(passwordField.getPassword());
        dispose();
    }

    private void noPasswordAction() {
        dispose();
    }
}