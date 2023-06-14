package com.editor.util.directory;

import com.sun.istack.internal.Nullable;
import com.editor.util.FileUtils;
import com.editor.util.TranslationTextComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Directory implements IDirectory {
    public List<DirectoryElement> elementlist = new ArrayList<>();
    public List<DirectoryElement> jsonFiles = new ArrayList<>();
    public String pathToEl;
    public boolean remote;
    @Nullable
    private File parentDir;

    public Directory(File workDir, @Nullable File parent) {
        remote = false;
        pathToEl = workDir.getPath();
        parentDir = parent;
        if (!workDir.exists()) {
            throw new NullPointerException(new TranslationTextComponent("exception.dirNotFound", workDir.getAbsolutePath()).toString());
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
        remote = false;
        parentDir = new File(element.pathToElement());
        File rootFile = new File(element.pathToElement());
        if (!rootFile.exists()) {
            throw new NullPointerException(new TranslationTextComponent("exception.dirNotFound", rootFile.getAbsolutePath()).toString());
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
    public boolean isRemote(){
        return remote;
    }
    public String pathToElement(){
        return pathToEl;
    }
    public Directory(List<DirectoryElement> folders, List<DirectoryElement> jsons, String pathToElement, boolean remote) {
        this.remote = remote;
        elementlist = folders;
        jsonFiles = jsons;
        this.pathToEl = pathToElement;
    }


    @Override
    public String getName() {
        return new File(pathToEl).getName().isEmpty() ? "[connect]" : new File(pathToEl).getName();
    }


}
