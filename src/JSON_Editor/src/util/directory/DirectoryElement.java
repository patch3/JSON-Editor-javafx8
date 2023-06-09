package util.directory;

import java.io.File;

public class DirectoryElement extends IDirectory {
    public final directory parentDir;
    public final String pathToParent;
    public final String pathToElement;
    public final boolean isJson;

    public DirectoryElement(File element, directory parent, boolean isJson) {
        this.isJson = isJson;
        parentDir = parent;
        pathToElement = element.getAbsolutePath();
        pathToParent = element.getParent();
    }

    @Override
    public String getName() {
        return new File(pathToElement).getName();
    }
}
