package util.directory;

import java.io.File;

public abstract class IDirectory {
    public String pathToElement;

    public String getName() {
        return new File(pathToElement).getName();
    }
}
