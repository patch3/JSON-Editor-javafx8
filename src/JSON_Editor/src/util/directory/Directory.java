package src.util.directory;

import com.sun.istack.internal.Nullable;
import src.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Directory extends IDirectory {
    public List<DirectoryElement> elementlist = new ArrayList<>();
    public List<DirectoryElement> jsonFiles = new ArrayList<>();
    public String pathToElement;
    @Nullable
    private File parentDir;
    public boolean isRemote;

    public Directory(File workDir, @Nullable File parent) {
        isRemote = false;
        pathToElement = workDir.getPath();
        parentDir = parent;
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

    public Directory(DirectoryElement element) {
        isRemote = false;
        parentDir = new File(element.parentDir.pathToElement);
        File rootFile = new File(element.pathToElement);
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

    public Directory(List<DirectoryElement> folders, List<DirectoryElement> jsons, String pathToElement, boolean remote) {
        isRemote = remote;
        elementlist = folders;
        jsonFiles = jsons;
        this.pathToElement = pathToElement;
    }


    @Override
    public String getName() {
        return new File(pathToElement).getName();
    }


}
