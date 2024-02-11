package org.JazzZip.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChangeNameDialog extends JDialog {
    private static JTextField ChangeText;
    private static String TestWillChange = null;
    public ChangeNameDialog(JFrame parent) throws Exception {
        super(parent, "重命名", true);
        parent.setEnabled(false);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setSize(300, 150);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        buildUI();

        setLocationRelativeTo(parent);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                parent.setEnabled(true);
                super.windowClosed(e);
            }
        });
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new FlowLayout());
        mainPanel.setBorder(new EmptyBorder(13, 0, 0, 0));

        JPanel inputPanel = new JPanel(new BorderLayout());

        ChangeText = new JTextField();
        ChangeText.setColumns(25);
        inputPanel.add(ChangeText, BorderLayout.NORTH);

        mainPanel.add(inputPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton button1 = new JButton("确定");
        button1.setFocusable(false);
        button1.addActionListener(e -> setChangeText());

        JButton button2 = new JButton("取消");
        button2.setFocusable(false);
        button2.addActionListener(e -> noChangeText());

        buttonPanel.add(button1);
        buttonPanel.add(button2);

        mainPanel.add(buttonPanel);
        add(mainPanel);
    }

    private void setChangeText() {
        TestWillChange = ChangeText.getText();
        dispose();
    }

    public String getTest() {
        return TestWillChange;
    }

    private void noChangeText() {
        TestWillChange = null;
        dispose();
    }
}
