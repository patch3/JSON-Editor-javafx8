package com.editor.util.directory;

import java.io.File;

public class DirectoryElement implements IDirectory {
    public final IDirectory parentDir;
    private final String pathToEl;
    public final boolean isJson;

    private final boolean remote;

    public DirectoryElement(File element, IDirectory parent, boolean isJson) {
        this.isJson = isJson;
        parentDir = parent;
        pathToEl = element.getAbsolutePath();
        remote = false;
    }

    public DirectoryElement(String pathToElement, IDirectory parent, boolean isJson) {
        this.isJson = isJson;
        parentDir = parent;
        this.pathToEl = pathToElement;
        remote = true;
    }

    @Override
    public String toString() {
        return "DirectoryElement{" +
                "parentDir=" + parentDir +
                ", pathToElement='" + pathToEl + '\'' +
                ", isJson=" + isJson +
                ", isRemote=" + remote +
                '}';
    }
    public boolean isRemote(){
        return remote;
    }
    public String pathToElement(){
        return pathToEl;
    }
    @Override
    public String getName() {
        return new File(pathToEl).getName();
    }
}
