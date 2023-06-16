package com.editor.util.directory;

import com.sun.istack.internal.Nullable;

import java.io.File;

/**
 * Класс элемента директории. Служит для разделения директории
 * (фактической папки, в которой уже известно кол-во файлом и сами файлы)
 * и ее элементом (Отображаемые файлы директории, которые пустышки до нажатия на них в дереве).
 */
public class DirectoryElement implements IDirectory {
    /**
     * Родительская директория (Допускается null)
     */
    @Nullable
    public final IDirectory parentDir;
    /**
     * Путь до ЭТОГО элемента
     */
    private final String pathToEl;
    /**
     * Является ли этот элемент json-ом
     */
    public final boolean isJson;
    /**
     * Элемент на сервере или нет
     */
    private final boolean remote;


    /**
     * Конструктор элемента на локальном ПК
     * @param element Сам файл
     * @param parent Родительский элемент/директория
     * @param isJson Является ли файл json-ом
     */
    public DirectoryElement(File element, IDirectory parent, boolean isJson) {
        this.isJson = isJson;
        parentDir = parent;
        pathToEl = element.getAbsolutePath();
        remote = false;
    }
    /**
     * Конструктор элемента на удаленном сервере
     * @param pathToElement Путь к этому элементу на сервере
     * @param parent Родительский элемент/директория
     * @param isJson Является ли файл json-ом
     */
    public DirectoryElement(String pathToElement, IDirectory parent, boolean isJson) {
        this.isJson = isJson;
        parentDir = parent;
        this.pathToEl = pathToElement;
        remote = true;
    }
    /**
     * Переопределенный метод toString(), который возвращает всю информацию о элементе
     */
    @Override
    public String toString() {
        return "DirectoryElement{" +
                "parentDir=" + parentDir +
                ", pathToElement='" + pathToEl + '\'' +
                ", isJson=" + isJson +
                ", isRemote=" + remote +
                '}';
    }

    /**
     * @return true - удаленная, false - на локальном ПК
     */
    public boolean isRemote(){
        return remote;
    }

    /**
     * @return Путь к элементу
     */
    public String pathToElement(){
        return pathToEl;
    }

    /**
     * @return Возвращает имя элемента(Если это файл, то с разрешением)
     */
    @Override
    public String getName() {
        return new File(pathToEl).getName();
    }
}
