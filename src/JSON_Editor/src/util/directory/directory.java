package util.directory;

import com.sun.istack.internal.Nullable;
import util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class directory extends IDirectory {
    public List<DirectoryElement> elementlist = new ArrayList<>();
    public List<DirectoryElement> jsonFiles = new ArrayList<>();
    private File rootFile;
    public String pathToElement;
    @Nullable
    private File parentDir;

    public directory(File workDir, @Nullable File parent) {
        pathToElement = workDir.getPath();
        parentDir = parent;
        rootFile = workDir;
        if (!workDir.exists()) {
            throw new NullPointerException("Папка " + workDir.getAbsolutePath() + " не найдена");
        }
        if (workDir.listFiles() == null) {
            return;
        }
        for (File f : Objects.requireNonNull(workDir.listFiles())) {
            if (f.isDirectory()) {
                elementlist.add(new DirectoryElement(f, this, false));
            } else {
                if (FileUtils.getExtensionByStringHandling(f.getName()).equals("json")) {
                    jsonFiles.add(new DirectoryElement(f, this, true));
                }
            }
        }
    }

    public directory(DirectoryElement element) {
        parentDir = new File(element.pathToParent);
        rootFile = new File(element.pathToElement);
        if (!rootFile.exists()) {
            throw new NullPointerException("Папка " + rootFile.getAbsolutePath() + " не найдена");
        }
        if (rootFile.listFiles() == null) {
            return;
        }
        for (File f : Objects.requireNonNull(rootFile.listFiles())) {
            if (f.isDirectory()) {
                elementlist.add(new DirectoryElement(f, this, false));
            } else {
                if (FileUtils.getExtensionByStringHandling(f.getName()).equals("json")) {
                    jsonFiles.add(new DirectoryElement(f, this, true));
                }
            }
        }
    }


    @Override
    public String getName() {
        return rootFile.getName();
    }

}
