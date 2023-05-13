package JSON_Editor.manager;

import java.io.File;

public class Dirs {
    private final File _workDir;

    public Dirs(String nameDir) {
        String userHome = System.getProperty("user.home", ".");
        File workTempDir;
        switch (getPlatform().ordinal()) {
            case 0:
                String appdata = System.getenv("APPDATA");
                if (appdata != null) workTempDir = new File(appdata, nameDir + "/");
                else workTempDir = new File(userHome, nameDir + "/");
                break;
            case 1:
                workTempDir = new File(userHome, "." + nameDir + "/");
                break;
            case 2:
                workTempDir = new File(userHome, "Library/Application Support/" + nameDir);
                break;
            default:
                workTempDir = new File(userHome, nameDir + "/");
        }
        if ((!workTempDir.exists()) && (!workTempDir.mkdirs()))
            throw new RuntimeException("Рабочая директория не определена(" + workTempDir + ")");
        _workDir = workTempDir;
    }

    public static OS getPlatform() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) return Dirs.OS.windows;
        else if (os.contains("linux") || os.contains("unix")) return Dirs.OS.linux;
        else if (os.contains("macos")) return Dirs.OS.macos;
        else return OS.unknown;
    }

    @Override
    public String toString() {
        return "ManagerDirs{" +
                "_workDir=" + _workDir +
                '}';
    }

    public File getWorkDir() {
        return _workDir;
    }

    public enum OS {
        windows,
        linux,
        macos,
        unknown
    }
}
