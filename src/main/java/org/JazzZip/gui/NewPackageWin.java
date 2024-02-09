package org.JazzZip.gui;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class NewPackageWin extends JFrame {
    final private static String[] opt = {"Zip archive"};
    private static String SavePath = null;
    private static File[] FilesList = null;
    public NewPackageWin(JFrame frame) throws Exception {
        setTitle("文件信息");
        frame.setEnabled(false);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        setSize(320, 360);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        BuildUI();
        setLocationRelativeTo(frame);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                frame.setEnabled(true);
                super.windowClosed(e);
            }
        });
    }

    private void BuildUI() {
        JPanel UI = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JPanel SaveFolderPanel = new JPanel(new FlowLayout());
        JLabel FolderNameLabel = new JLabel("文件位置:");
        JTextField FolderNameText = new JTextField(27);
        FolderNameText.setFocusable(false);
        JButton SaveWhereButton = new JButton("...");
        SaveWhereButton.addActionListener(e -> SaveFolderWhere(FolderNameText));
        SaveWhereButton.setFocusable(false);
        SaveFolderPanel.add(FolderNameLabel);
        SaveFolderPanel.add(FolderNameText);
        SaveFolderPanel.add(SaveWhereButton);

        JPanel FileNamePanel = new JPanel(new FlowLayout());
        JLabel FileNameLabel = new JLabel("文件名称:");
        JTextField FileNameInput = new JTextField(36);
        FileNamePanel.add(FileNameLabel);
        FileNamePanel.add(FileNameInput);

        JPanel FileTypePanel = new JPanel(new FlowLayout());
        JLabel FileTypeLabel = new JLabel("文件类型:");
        JComboBox<String> FileTypeComboBox = new JComboBox<>(opt);
        FileTypeComboBox.setPreferredSize(new Dimension(222, 20));
        FileTypeComboBox.setFocusable(false);
        FileTypePanel.add(FileTypeLabel);
        FileTypePanel.add(FileTypeComboBox);

        JPanel ChooseFilePanel = new JPanel(new BorderLayout());
        JLabel ChooseFileLabel = new JLabel("选择文件:");
        JButton ChooseFileButton = new JButton("文件选择按钮");
        ChooseFileButton.setFocusable(false);
        JTextArea ChooseFileList = new JTextArea(5, 35);
        ChooseFileButton.addActionListener(e -> ChoosePackageFiles(ChooseFileList));
        ChooseFileList.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(ChooseFileList);
        ChooseFilePanel.add(ChooseFileLabel, BorderLayout.WEST);
        ChooseFilePanel.add(ChooseFileButton, BorderLayout.CENTER);
        ChooseFilePanel.add(scrollPane, BorderLayout.SOUTH);

        JPanel PasswordPanel = new JPanel(new FlowLayout());
        JLabel PasswordLabel1 = new JLabel("密码:");
        JPasswordField PasswordField1 = new JPasswordField(26);
        PasswordField1.setEchoChar('*');
        JCheckBox ShowPasswordCheckBox = new JCheckBox("显示密码");
        ShowPasswordCheckBox.setFocusable(false);
        ShowPasswordCheckBox.addItemListener(e -> ShowPassword(e, PasswordField1));
        PasswordPanel.add(PasswordLabel1);
        PasswordPanel.add(PasswordField1);
        PasswordPanel.add(ShowPasswordCheckBox);

        JPanel ConfirmBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton YesBtn = new JButton("确定");
        YesBtn.addActionListener(e -> confirm(FileNameInput, FileTypeComboBox, PasswordField1));
        YesBtn.setFocusable(false);
        JButton NoBtn = new JButton("取消");
        NoBtn.addActionListener(e -> dispose());
        NoBtn.setFocusable(false);
        ConfirmBtnPanel.add(YesBtn);
        ConfirmBtnPanel.add(NoBtn);

        UI.add(SaveFolderPanel);
        UI.add(FileNamePanel);
        UI.add(FileTypePanel);
        UI.add(ChooseFilePanel);
        UI.add(PasswordPanel);
        UI.add(ConfirmBtnPanel);
        add(UI);
    }

    private static void ShowPassword(ItemEvent e, JPasswordField PasswordField) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            PasswordField.setEchoChar((char)0);
        } else {
            PasswordField.setEchoChar('*');
        }
    }

    private void SaveFolderWhere(JTextField FolderNameText) {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("选择压缩包保存路径");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (folderChooser.showDialog(this, "选择") == JFileChooser.APPROVE_OPTION) {
            SavePath = folderChooser.getSelectedFile().getAbsolutePath();
            FolderNameText.setText(SavePath);
        }
    }

    private void ChoosePackageFiles(JTextArea ChooseFileList) {
        JFileChooser filesChooser = new JFileChooser();
        filesChooser.setDialogTitle("选择文件与文件夹");
        filesChooser.setMultiSelectionEnabled(true);
        filesChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (filesChooser.showDialog(this, "选择") == JFileChooser.APPROVE_OPTION) {
            FilesList = filesChooser.getSelectedFiles();
            for (File file : FilesList) {
                ChooseFileList.append(file.getAbsolutePath() + "\n");
            }
        }
    }

    private void confirm(JTextField FileNameInput, JComboBox<String> FileTypeComboBox, JPasswordField PasswordField) {
        if (SavePath != null && FilesList != null) {
            if (!Objects.equals(FileNameInput.getText(), "")) {
                File PackageFile = new File(String.valueOf(Paths.get(SavePath).resolve(FileNameInput.getText())));
                if (!PackageFile.exists()) {
                    if (Objects.equals(Objects.requireNonNull(FileTypeComboBox.getSelectedItem()).toString(), "Zip archive")) {
                        CreateZipFile(PackageFile, PasswordField);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "文件已存在", "JazzZip", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "文件名称未填写", "JazzZip", JOptionPane.ERROR_MESSAGE);
            }
        } else if (SavePath == null) {
            JOptionPane.showMessageDialog(this, "文件保存路径未选择", "JazzZip", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "未选择要压缩的文件", "JazzZip", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void CreateZipFile(File ZipFileName, JPasswordField PasswordField) {
        if (PasswordField.getPassword().length != 0) {
            ZipFile zipFile = new ZipFile(ZipFileName, PasswordField.getPassword());
            zipFile.setCharset(Charset.forName("GBK"));
            try (zipFile) {
                ZipParameters parameters = new ZipParameters();
                parameters.setEncryptFiles(true);
                parameters.setCompressionMethod(CompressionMethod.DEFLATE);
                parameters.setCompressionLevel(CompressionLevel.NORMAL);
                parameters.setEncryptionMethod(EncryptionMethod.AES);
                parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);

                for (File file : FilesList) {
                    if (file.exists() && file.isFile() && file.length() != 0) {
                        zipFile.addFile(file, parameters);
                    } else if (file.exists() && file.isDirectory() && FileUtils.sizeOfDirectory(file) != 0) {
                        zipFile.addFolder(file, parameters);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            ZipFile zipFile = new ZipFile(ZipFileName);
            zipFile.setCharset(Charset.forName("GBK"));
            try (zipFile) {
                ZipParameters parameters = new ZipParameters();
                parameters.setCompressionMethod(CompressionMethod.DEFLATE);
                parameters.setCompressionLevel(CompressionLevel.NORMAL);

                for (File file : FilesList) {
                    if (file.exists() && file.isFile() && file.length() != 0) {
                        zipFile.addFile(file, parameters);
                    } else if (file.exists() && file.isDirectory() && FileUtils.sizeOfDirectory(file) != 0) {
                        zipFile.addFolder(file, parameters);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        JOptionPane.showMessageDialog(this, "文件创建成功", "JazzZip", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
