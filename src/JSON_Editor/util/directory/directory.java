package util.directory;

import util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class directory extends IDirectory {
    public List<DirectoryElement> elementlist = new ArrayList<>();
    public List<DirectoryElement> jsonFiles = new ArrayList<>();
    private File rootFile;


    public directory(File workDir) {
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

    @Override
    public String getName() {
        return rootFile.getName();
    }

}
