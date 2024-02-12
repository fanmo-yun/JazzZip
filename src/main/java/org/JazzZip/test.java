package org.JazzZip;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class test {
    public static void main(String[] args) {
        Collection<File> files = FileUtils.listFilesAndDirs(new File("E:\\java\\JazzZip\\src"), TrueFileFilter.TRUE, TrueFileFilter.TRUE);
        List<File> string = new ArrayList<>(files.stream().toList());
        string.remove(string.size() - 1);
    }
}
