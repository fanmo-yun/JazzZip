package org.JazzZip.gui;
import net.lingala.zip4j.model.FileHeader;
import org.JazzZip.Process.ZipProcess;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainWin {
    private static String selectedFilePath = "";
    private static TreePath selectedInfoNode = null;
    private static TreePath selectedAddNode = null;
    private static TreePath[] selectedDeleNode = null;
    private static TreePath[] selectedExtractNode = null;
    private static List<FileHeader> zipFileNames = null;
    private static JFrame MainFrame;
    private static JTree tree;

    public static void MainWindow() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        MainFrame = new JFrame("JazzZip");
        MainFrame.setMinimumSize(new Dimension(400, 300));
        MainFrame.setSize(800, 600);
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SetPosition();
        BuildMenu();
        BuildUI();

        MainFrame.setVisible(true);
    }

    private static void BuildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenuItem newArchive = new JMenuItem("新建压缩包");
        newArchive.addActionListener(e -> NewPackageAction());
        JMenuItem openArchive = new JMenuItem("打开压缩包");
        openArchive.addActionListener(e -> getFilePath());
        JMenuItem exitBtn = new JMenuItem("退出软件");
        exitBtn.addActionListener(e -> System.exit(0));
        fileMenu.add(newArchive);
        fileMenu.add(openArchive);
        fileMenu.addSeparator();
        fileMenu.add(exitBtn);

        JMenu editorMenu = new JMenu("编辑");
        JMenuItem addFile = new JMenuItem("添加文件");
        JMenuItem removeFile = new JMenuItem("删除文件");
        JMenuItem cancelChose = new JMenuItem("取消选择");
        cancelChose.addActionListener(e -> {
            tree.clearSelection();
            selectedAddNode = null;
            selectedDeleNode = null;
            selectedInfoNode = null;
            selectedExtractNode = null;
            zipFileNames = null;
        });
        editorMenu.add(addFile);
        editorMenu.add(removeFile);
        editorMenu.add(cancelChose);

        JMenu infoMenu = new JMenu("信息");
        JMenuItem infomationfile = new JMenuItem("关于此文件");
        infomationfile.addActionListener(e -> InfoFileAction());
        JMenuItem infofilename = new JMenuItem("关于此文件内容");
        infofilename.addActionListener(e -> InfoAction());
        infoMenu.add(infomationfile);
        infoMenu.add(infofilename);

        menuBar.add(fileMenu);
        menuBar.add(editorMenu);
        menuBar.add(infoMenu);
        MainFrame.setJMenuBar(menuBar);
    }

    private static void BuildUI() {
        MainFrame.setLayout(new BorderLayout());

        JPanel buttonPanel = getjPanel();

        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(new EmptyBorder(4, 4, 4, 4));

        DefaultMutableTreeNode top_node= new DefaultMutableTreeNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(top_node);
        tree = new JTree(treeModel);
        AddPopupMenu();
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                SelectNode(e);
            }
            }
        });
        tree.addMouseListener(new DoubleClick());
        JScrollPane scrollPane = new JScrollPane(tree);
        treePanel.add(scrollPane);

        MainFrame.add(buttonPanel, BorderLayout.NORTH);
        MainFrame.add(treePanel, BorderLayout.CENTER);
    }

    private static class DoubleClick extends MouseAdapter {
        private long lastClickTime = 0;
        @Override
        public void mouseClicked(MouseEvent e) {
            long currentTime = System.currentTimeMillis();
            long doubleClickInterval = 200;
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            TreePath selectedDoubleNode;
            if (currentTime - lastClickTime < doubleClickInterval) {
                if (path != null) {
                    DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (selected.isLeaf()) {
                        selectedDoubleNode = tree.getSelectionPath();
                        ZipProcess.OpenFile(selectedFilePath, MainFrame, selectedDoubleNode);
                    }
                }
            }
            lastClickTime = currentTime;
        }
    }

    private static void SelectNode(MouseEvent e) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) path.getLastPathComponent();
            selectedDeleNode = tree.getSelectionPaths();
            selectedExtractNode = tree.getSelectionPaths();
            selectedInfoNode = tree.getSelectionPath();
            if (!selected.isLeaf()) {
                selectedAddNode = tree.getSelectionPath();
            }
        }
    }

    private static void AddPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem ExtractItem = new JMenuItem("解压至");
        ExtractItem.addActionListener(e -> ExtractAction());
        JMenuItem AddItem = new JMenuItem("添加");
        AddItem.addActionListener(e -> AddAction());
        JMenuItem DeleItem = new JMenuItem("删除");
        DeleItem.addActionListener(e -> DeleteAction());
        JMenuItem InfoItem = new JMenuItem("关于");
        InfoItem.addActionListener(e -> InfoAction());

        popupMenu.add(ExtractItem);
        popupMenu.add(AddItem);
        popupMenu.add(DeleItem);
        popupMenu.add(InfoItem);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                tree.setSelectionRow(row);
                popupMenu.show(tree, e.getX(), e.getY());
            }
            }
        });

    }

    private static JPanel getjPanel() {
        JButton NewBtn = new JButton("新建");
        NewBtn.setFocusable(false);
        NewBtn.addActionListener(e -> NewPackageAction());

        JButton OpenBtn = new JButton("打开");
        OpenBtn.setFocusable(false);
        OpenBtn.addActionListener(e -> getFilePath());

        JButton ExtractBtn = new JButton("解压");
        ExtractBtn.setFocusable(false);
        ExtractBtn.addActionListener(e -> ExtractAction());

        JButton AddBtn = new JButton("添加");
        AddBtn.setFocusable(false);
        AddBtn.addActionListener(e -> AddAction());

        JButton DeleBtn = new JButton("删除");
        DeleBtn.setFocusable(false);
        DeleBtn.addActionListener(e -> DeleteAction());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        buttonPanel.add(NewBtn);
        buttonPanel.add(OpenBtn);
        buttonPanel.add(ExtractBtn);
        buttonPanel.add(AddBtn);
        buttonPanel.add(DeleBtn);
        return buttonPanel;
    }

    private static void ProcessFile(String fileName) {
        if (fileName.toLowerCase().endsWith(".zip")) {
            zipFileNames = ZipProcess.GetZipFileNames(fileName, MainFrame);
            if (zipFileNames != null) {
                ArrayList<String> fileList = new ArrayList<>();
                for (FileHeader filename : zipFileNames) {
                    fileList.add(filename.getFileName());
                }
                FileNameAddToList(fileName, fileList);
            }
        } else {
            JOptionPane.showMessageDialog(MainFrame, "文件载入出错", "JazzZip", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void AddFileToZip() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);

        int result = fileChooser.showDialog(MainFrame, "选择文件或者文件夹");
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFileOrDirectory = fileChooser.getSelectedFiles();
            ZipProcess.AddToZip(selectedFilePath, selectedFileOrDirectory, selectedAddNode, MainFrame);
            ProcessFile(selectedFilePath);
        }
        selectedAddNode = null;
        tree.clearSelection();
    }

    private static void ExtractFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showDialog(MainFrame, "选择路径");
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String selected = selectedFile.getAbsolutePath();
            if (selectedExtractNode == null) {
                ZipProcess.UnzipFile(selectedFilePath, selected, MainFrame);
            } else {
                ZipProcess.UnzipFile(selectedFilePath, selected, selectedExtractNode, MainFrame);
            }
        }
    }

    private static void NewPackageAction() {
        try {
            NewPackageWin newPackageWin = new NewPackageWin(MainFrame);
            newPackageWin.setVisible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void DeleteFile() {
        ZipProcess.RemoveFileZip(selectedFilePath, selectedDeleNode, MainFrame);
        ProcessFile(selectedFilePath);
    }

    private static void FileNameAddToList(String fileName, ArrayList<String> fileList) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(fileName);

        for (String path : fileList) {
            String[] pathElements = path.split("/");
            DefaultMutableTreeNode currentNode = root;

            for (String pathElement : pathElements) {
                DefaultMutableTreeNode node = findNode(currentNode, pathElement);
                if (node == null) {
                    node = new DefaultMutableTreeNode(pathElement);
                    currentNode.add(node);
                }
                currentNode = node;
            }
        }

        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);
        tree.updateUI();
    }

    private static DefaultMutableTreeNode findNode(DefaultMutableTreeNode parent, String nodeName) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (nodeName.equals(node.getUserObject().toString())) {
                return node;
            }
        }
        return null;
    }


    private static void SetPosition() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = MainFrame.getSize();

        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;

        MainFrame.setLocation(x, y);
    }

    private static void getFilePath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory()
                        || f.getName().toLowerCase().endsWith(".zip")
                        || f.getName().toLowerCase().endsWith(".7z");
            }

            @Override
            public String getDescription() {
                return "compressed Files (*.zip, *.7z)";
            }
        });

        int result = fileChooser.showOpenDialog(MainFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            ProcessFile(selectedFilePath);
        }
    }

    private static void ExtractAction() {
        if (!selectedFilePath.isEmpty() && selectedExtractNode == null) {
            ExtractFile();
        } else if (!selectedFilePath.isEmpty()) {
            ExtractFile();
        }
        selectedExtractNode = null;
        tree.clearSelection();
    }

    private static void AddAction() {
        if (!selectedFilePath.isEmpty() && selectedAddNode != null) {
            AddFileToZip();
        }
    }

    private static void DeleteAction() {
        if (!selectedFilePath.isEmpty() &&
                selectedDeleNode != null &&
                JOptionPane.showConfirmDialog(MainFrame, "确认删除?", "JazzZip", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            DeleteFile();
            selectedDeleNode = null;
            tree.clearSelection();
        }
    }

    private static void InfoAction() {
        if (!selectedFilePath.isEmpty() &&
                selectedInfoNode != null) {
            ZipProcess.ShowInfo(selectedFilePath, MainFrame, selectedInfoNode);
        }
    }

    private static void InfoFileAction() {
        if (!selectedFilePath.isEmpty()) {
            try {
                ShowInfoWin showInfoWin = new ShowInfoWin(selectedFilePath, MainFrame);
                showInfoWin.setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException("Cannot Run");
            }
        }
    }
}
