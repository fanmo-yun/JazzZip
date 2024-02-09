package org.JazzZip.Process;

import com.github.junrar.Archive;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.github.junrar.volume.FileVolumeManager;

import javax.swing.*;
import java.io.IOException;

public class RarProcess {
    public static void ExtractRar(String f, String extractFolderPath, JFrame frame) {
        try {
            Junrar.extract(f, extractFolderPath);
            JOptionPane.showMessageDialog(frame, "解压完成", "JazzZip", JOptionPane.ERROR_MESSAGE);
        } catch (RarException | IOException e) {
            JOptionPane.showMessageDialog(frame, "解压未能完成", "JazzZip", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }
}
