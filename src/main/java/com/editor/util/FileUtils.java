package com.editor.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    public static String getExtensionByStringHandling(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return null;
    }

    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static void deleteFolder(String localFolderPath) {
        File folder = new File(localFolderPath);
        if (folder.exists() && folder.isDirectory()) {
            deleteFilesRecursive(folder);
            if (folder.delete()) {
                System.out.println("Папка успешно удалена: " + localFolderPath);
            } else {
                System.out.println("Не удалось удалить папку: " + localFolderPath);
            }
        } else {
            System.out.println("Папка не существует или не является директорией: " + localFolderPath);
        }
    }

    private static void deleteFilesRecursive(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteFilesRecursive(child);
                }
            }
        }
        if (!file.delete()) {
            System.out.println("Не удалось удалить файл: " + file.getAbsolutePath());
        }
    }
}
