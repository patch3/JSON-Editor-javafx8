package src.util.directory;

import java.io.File;

public class DirectoryElement extends IDirectory {
    public final Directory parentDir;
    public final String pathToElement;
    public final boolean isJson;

    public boolean isRemote;

    public DirectoryElement(File element, Directory parent, boolean isJson) {
        this.isJson = isJson;
        parentDir = parent;
        pathToElement = element.getAbsolutePath();
        isRemote = false;
    }

    public DirectoryElement(String pathToElement, Directory parent, boolean isJson) {
        this.isJson = isJson;
        parentDir = parent;
        this.pathToElement = pathToElement;
        isRemote = true;
    }

    @Override
    public String getName() {
        return new File(pathToElement).getName();
    }
}
