package org.JazzZip.Process;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.JazzZip.gui.ChangeNameDialog;
import org.JazzZip.gui.PasswdDialog;
import org.JazzZip.gui.ShowFileInfoWin;
import org.JazzZip.gui.ShowInfoWin;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ZipProcess {
    public static boolean CheckZipFile(ZipFile zipFile, JFrame frame) {
        if (!zipFile.isValidZipFile()) {
            JOptionPane.showMessageDialog(frame, "文件可能不存在或不合法", "JazzZip", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        try {
            if (zipFile.isSplitArchive()) {
                JOptionPane.showMessageDialog(frame, "文件为分卷压缩,无法对其进行操作", "JazzZip", JOptionPane.ERROR_MESSAGE);
                return true;
            }
        } catch (ZipException e) {
            JOptionPane.showMessageDialog(frame, "文件类型未知", "JazzZip", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    public static char[] ZipPassword(ZipFile zipFile, JFrame frame) {
        try {
            if (zipFile.isEncrypted()) {
                PasswdDialog passwdDialog = new PasswdDialog(frame);
                passwdDialog.setVisible(true);
                return passwdDialog.getPassword();
            }
        } catch (ZipException e) {
            JOptionPane.showMessageDialog(frame, "文件错误", "JazzZip", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static List<FileHeader> GetZipFileNames(String f, JFrame frame) {
        ZipFile zipFile = new ZipFile(f);
        try (zipFile) {
            zipFile.setCharset(Charset.forName("GBK"));
            return zipFile.getFileHeaders();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "文件可能损害", "JazzZip", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public static void UnzipFile(String f, String extractFolderPath, JFrame frame) {
        File file = new File(extractFolderPath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(frame, "路径不存在", "JazzZip", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ZipFile zipFile = new ZipFile(f);
        if (CheckZipFile(zipFile, frame)) {
            return;
        }
        try (zipFile) {
            char[] passwd = ZipPassword(zipFile, frame);
            if (passwd != null) {
                if (!Arrays.equals(passwd, "".toCharArray())) {
                    zipFile.setPassword(passwd);
                } else {
                    return;
                }
            }
            zipFile.setCharset(Charset.forName("GBK"));
            zipFile.extractAll(extractFolderPath);
            JOptionPane.showMessageDialog(frame, "解压成功", "JazzZip", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "文件解压出错", "JazzZip", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    public static void UnzipFile(String f, String extractFolderPath, TreePath[] ExtractNode, JFrame frame) {
        File file = new File(extractFolderPath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(frame, "路径不存在", "JazzZip", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ZipFile zipFile = new ZipFile(f);
        if (CheckZipFile(zipFile, frame)) {
            return;
        }

        try (zipFile) {
            char[] passwd = ZipPassword(zipFile, frame);
            if (passwd != null) {
                if (!Arrays.equals(passwd, "".toCharArray())) {
                    zipFile.setPassword(passwd);
                } else {
                    return;
                }
            }
            List<String> extractFiles = PathJoins(ExtractNode);
            zipFile.setCharset(Charset.forName("GBK"));
            if (extractFiles != null) {
                for (int i = 0; i< extractFiles.size(); i++) {
                    zipFile.extractFile(extractFiles.get(i), extractFolderPath);
                }
            } else {
                zipFile.extractAll(extractFolderPath);
            }
            JOptionPane.showMessageDialog(frame, "解压成功", "JazzZip", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "文件解压出错", "JazzZip", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    public static void AddToZip(String f, File[] AddFolderPathOrFile, TreePath WillAddFile, JFrame frame) {
        ZipFile zipFile = new ZipFile(f);
        if (CheckZipFile(zipFile, frame)) {
            return;
        }
        try (zipFile) {
            zipFile.setCharset(Charset.forName("GBK"));
            ZipParameters parameters = new ZipParameters();
            char[] passwd = ZipPassword(zipFile, frame);
            if (passwd != null) {
                if (!Arrays.equals(passwd, "".toCharArray())) {
                    zipFile.setPassword(passwd);
                    parameters.setEncryptFiles(true);
                    parameters.setEncryptionMethod(EncryptionMethod.AES);
                    parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                } else {
                    return;
                }
            }
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            parameters.setCompressionLevel(CompressionLevel.NORMAL);
            String RootFolder = PathJoin(WillAddFile);
            parameters.setRootFolderNameInZip(Objects.requireNonNullElse(RootFolder, ""));
            for (File fp : AddFolderPathOrFile) {
                if (fp.exists() && fp.isFile() && fp.length() != 0) {
                    zipFile.addFile(fp, parameters);
                } else if (fp.exists() && fp.isDirectory() && FileUtils.sizeOfDirectory(fp) != 0) {
                    zipFile.addFolder(fp, parameters);
                }
            }
            JOptionPane.showMessageDialog(frame, "添加成功", "JazzZip", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "文件添加错误", "JazzZip", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    public static void RemoveFileZip(String f, TreePath[] RemoveFiles, JFrame frame) {
        ZipFile zipFile = new ZipFile(f);
        if (CheckZipFile(zipFile, frame)) {
            return;
        }
        try (zipFile) {
            List<String> paths = PathJoins(RemoveFiles);
            zipFile.setCharset(Charset.forName("GBK"));

            if (paths == null) {
                List<FileHeader> fileHeaders = zipFile.getFileHeaders();
                for (int i = 0; i < fileHeaders.size(); i++) {
                    zipFile.removeFile(fileHeaders.get(0));
                    i--;
                }
            } else {
                zipFile.removeFiles(paths);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "文件删除错误", "JazzZip", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    public static void OpenFile(String f, JFrame frame, TreePath node) {
        ZipFile zipFile = new ZipFile(f);
        if (CheckZipFile(zipFile, frame)) {
            return;
        }
        Path extractWhere;
        try(zipFile) {
            char[] passwd = ZipPassword(zipFile, frame);
            if (passwd != null) {
                if (!Arrays.equals(passwd, "".toCharArray())) {
                    zipFile.setPassword(passwd);
                } else {
                    return;
                }
            }
            String openFileName = PathJoin(node);
            Path baseDir = Paths.get(System.getProperty("user.dir") + "\\temp");
            extractWhere = baseDir.resolve(String.valueOf(System.currentTimeMillis()));
            if (!extractWhere.toFile().exists()) {
                boolean success = extractWhere.toFile().mkdir();
                if (!success) {
                    throw new RuntimeException();
                }
            }
            zipFile.extractFile(openFileName, String.valueOf(extractWhere));
            if (openFileName != null) {
                Desktop.getDesktop().open(extractWhere.resolve(openFileName).toFile());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void ShowInfo(String f, JFrame frame, TreePath node) {
        ZipFile zipFile = new ZipFile(f);
        try (zipFile) {
            String pathNode = PathJoin(node);
            if (pathNode == null) {
                ShowInfoWin showInfoWin = new ShowInfoWin(f, frame);
                showInfoWin.setVisible(true);
            } else {
                ShowFileInfoWin showFileInfoWin = new ShowFileInfoWin(zipFile, frame, pathNode);
                showFileInfoWin.setVisible(true);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "文件信息获取失败", "JazzZip", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void ChangeName(String f, JFrame frame, TreePath node) {
        ZipFile zipFile = new ZipFile(f);
        if (CheckZipFile(zipFile, frame)) {
            return;
        }
        try(zipFile) {
            char[] passwd = ZipPassword(zipFile, frame);
            if (passwd != null) {
                if (!Arrays.equals(passwd, "".toCharArray())) {
                    zipFile.setPassword(passwd);
                } else {
                    return;
                }
            }
            ChangeNameDialog changeNameDialog = new ChangeNameDialog(frame);
            changeNameDialog.setVisible(true);
            String willChangeName = changeNameDialog.getTest();
            if (willChangeName != null) {
                zipFile.setCharset(Charset.forName("GBK"));
                String path = PathJoin(node);
                if (path != null) {
                    String extractPath = String.valueOf(Paths.get(System.getProperty("user.dir") + "\\temp").resolve(String.valueOf(System.currentTimeMillis())));
                    zipFile.extractFile(path, extractPath);
                    File file = new File(extractPath + "\\" + path);
                    File newfile = new File(extractPath + "\\" + willChangeName);
                    try {
                        if (file.isDirectory()) {
                            FileUtils.moveDirectory(file, newfile);
                        } else if (file.isFile()) {
                            FileUtils.moveFile(file, newfile);
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(frame, "更名失败", "JazzZip", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    ZipParameters parameters = new ZipParameters();
                    parameters.setCompressionMethod(CompressionMethod.DEFLATE);
                    parameters.setCompressionLevel(CompressionLevel.NORMAL);
                    if (passwd != null) {
                        parameters.setEncryptFiles(true);
                        parameters.setEncryptionMethod(EncryptionMethod.AES);
                        parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                    }
                    String ParentPath = PathJoin(node.getParentPath());
                    parameters.setRootFolderNameInZip(Objects.requireNonNullElse(ParentPath, ""));
                    if (newfile.isFile()) {
                        zipFile.addFile(newfile, parameters);
                    } else if (newfile.isDirectory()) {
                        zipFile.addFolder(newfile, parameters);
                    }
                    zipFile.removeFile(path);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "文件读取失败", "JazzZip", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    private static List<String> PathJoins(TreePath[] Node) {
        for (TreePath node : Node) {
            if (node.getPath().length == 1) {
                return null;
            }
        }

        List<String> pathList = new ArrayList<>();
        for (TreePath i : Node) {
            StringBuilder path = new StringBuilder();
            Object[] paths = i.getPath();
            for (int j = 1; j < paths.length; j++) {
                path.append(paths[j].toString());
                if (j < paths.length - 1 || !((DefaultMutableTreeNode) paths[j]).isLeaf()) {
                    path.append('/');
                }
            }
            pathList.add(path.toString());
        }
        return pathList;
    }

    private static String PathJoin(TreePath node) {
        if (node.getPath().length == 1) {
            return null;
        } else {
            StringBuilder path = new StringBuilder();
            Object[] paths = node.getPath();
            for (int j = 1; j < paths.length; j++) {
                path.append(paths[j].toString());
                if (j < paths.length - 1 || !((DefaultMutableTreeNode) paths[j]).isLeaf()) {
                    path.append('/');
                }
            }
            return path.toString();
        }
    }
}
