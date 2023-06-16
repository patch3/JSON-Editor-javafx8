package com.editor.util.directory;

import com.sun.istack.internal.Nullable;
import com.editor.util.FileUtils;
import com.editor.util.TranslationTextComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс фактической директории. Служит для разделения директории (фактической папки, в которой уже известно кол-во файлом и сами файлы)
 * и ее элементов (Отображаемые файлы директории, которые пустышки до нажатия на них в дереве).
 */
public class Directory implements IDirectory {
    /**
     * Лист директорий в папке
     */
    public List<DirectoryElement> elementlist = new ArrayList<>();
    /**
     * Лист json-файлов в папке
     */
    public List<DirectoryElement> jsonFiles = new ArrayList<>();
    /**
     * Путь до этой папки
     */
    public String pathToEl;
    /**
     * Удаленная папка или нет
     */
    public boolean remote;


    /**
     * Конструктор локальной директории
     * @param workDir Файл, с которым работаем (Сама директория)
     */
    public Directory(File workDir) {
        remote = false;
        pathToEl = workDir.getPath();
        if (!workDir.exists()) {
            throw new NullPointerException(new TranslationTextComponent("exception.dirNotFound", workDir.getAbsolutePath()).toString());
        }
        if (workDir.listFiles() == null) {
            return;
        }
        // Проходимся по всем файлам в папке и сортируем их по директориям и файлаи json
        for (File f : Objects.requireNonNull(workDir.listFiles())) {
            if (f.isDirectory()) {
                elementlist.add(new DirectoryElement(f, this, false)); // Если файл директория, то добавляем его в список элементов
            } else {
                if (FileUtils.getExtensionByStringHandling(f.getName()).equals("json")) { // Проверка расширения файла на json. Если нет - пропускаем
                    jsonFiles.add(new DirectoryElement(f, this, true)); // Если файл json, то добавляем его в список json-ов
                }
            }
        }
    }

    /**
     * Конструктор локальной директории из элемента директории
     * @param element Элемент, из которого мы хотим сделать директорию
     */
    public Directory(DirectoryElement element) {
        remote = false;
        File rootFile = new File(element.pathToElement()); // Файл элемента директории
        if (!rootFile.exists()) { // Если файла нет
            throw new NullPointerException(new TranslationTextComponent("exception.dirNotFound", rootFile.getAbsolutePath()).toString());
        }
        if (rootFile.listFiles() == null) { // Если в директории нет файлов
            return;
        }
        // Сортируем файлы в директории на папки и json-ы
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

    /**
     * Конструктор в основном удаленной директории, но может использоваться для лоакальных
     * @param folders Папки в этой директории
     * @param jsons json-ы в этой директории
     * @param pathToElement Сама директория
     * @param remote Удаленная или нет
     */
    public Directory(List<DirectoryElement> folders, List<DirectoryElement> jsons, String pathToElement, boolean remote) {
        this.remote = remote;
        elementlist = folders;
        jsonFiles = jsons;
        this.pathToEl = pathToElement;
    }

    /**
     * @return true - удаленная, false - локальная
     */
    public boolean isRemote(){
        return remote;
    }
    /**
     * @return Путь к этому элементу
     */
    public String pathToElement(){
        return pathToEl;
    }


    /**
     * @return Вернет имя директории. В случае, если папка удаленная, вернет [connect].
     */
    @Override
    public String getName() {
        return new File(pathToEl).getName().isEmpty() ? "[connect]" : new File(pathToEl).getName();
    }


}
