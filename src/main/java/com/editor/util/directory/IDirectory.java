package com.editor.util.directory;

/**
 * Это интерфейс, который обобщает функционал Directory и DirectoryElement
 */
public interface IDirectory {
    String pathToElement();
    boolean isRemote();
    String getName();
}
