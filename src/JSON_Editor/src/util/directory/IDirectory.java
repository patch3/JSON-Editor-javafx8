package src.util.directory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class IDirectory {
    public String pathToElement;

    public static List<IDirectory> toIDirList(List<?> list) {
        List<IDirectory> returnable = new ArrayList<>();
        for (Object o : list) {
            returnable.add((IDirectory) o);
        }
        return returnable;
    }

    public String getName() {
        return new File(pathToElement).getName();
    }
}
