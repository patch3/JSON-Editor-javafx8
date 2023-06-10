package src.util.directory;

import java.io.File;

public class DirectoryElement extends IDirectory {
    public final IDirectory parentDir;
    public final String pathToElement;
    public final boolean isJson;

    public boolean isRemote;

    public DirectoryElement(File element, IDirectory parent, boolean isJson) {
        this.isJson = isJson;
        parentDir = parent;
        pathToElement = element.getAbsolutePath();
        isRemote = false;
    }

    public DirectoryElement(String pathToElement, IDirectory parent, boolean isJson) {
        this.isJson = isJson;
        parentDir = parent;
        this.pathToElement = pathToElement;
        isRemote = true;
    }

    @Override
    public String toString() {
        return "DirectoryElement{" +
                "parentDir=" + parentDir +
                ", pathToElement='" + pathToElement + '\'' +
                ", isJson=" + isJson +
                ", isRemote=" + isRemote +
                '}';
    }


    @Override
    public String getName() {
        return new File(pathToElement).getName();
    }
}
