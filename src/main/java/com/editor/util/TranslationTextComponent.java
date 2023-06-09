package com.editor.util;

import com.json.IUnitJson;
import com.json.Json;
import com.json.JsonException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class TranslationTextComponent {
    public static String fileName;
    private final String key;
    private final Object[] arguments;

    private static Json currentJson;

    static {
        try {
            InputStream jsonStream = TranslationTextComponent.class.getResourceAsStream("/translate/en_UK.json");
            if (jsonStream == null) {
                throw new FileNotFoundException("Failed to load language: en_UK.json");
            }
            currentJson = new Json(jsonStream);
            fileName = "en_UK.json";
        } catch (JsonException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load language: en_UK.json");
        }
    }

    public TranslationTextComponent(String key) {
        this.key = key;
        this.arguments = null;
    }

    public TranslationTextComponent(String key, Object... args) {
        this.key = key;
        this.arguments = args;
    }
    public static List<String> getLanguages() {
        List<String> languages = new ArrayList<>();
        ClassLoader classLoader = TranslationTextComponent.class.getClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources("translate");
            while (resources.hasMoreElements()) {
                URL folderUrl = resources.nextElement();
                if (folderUrl.getProtocol().equals("file")) {
                    // If the resource is in the file system
                    File folder = new File(folderUrl.toURI());
                    File[] files = folder.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            languages.add(file.getName());
                            // Further processing of the file...
                        }
                    }
                } else if (folderUrl.getProtocol().equals("jar")) {
                    // If the resource is inside a JAR file
                    JarURLConnection jarConnection = (JarURLConnection) folderUrl.openConnection();
                    JarFile jarFile = jarConnection.getJarFile();
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (!entry.isDirectory() && entry.getName().startsWith("translate/")) {
                            // File inside the "translate" folder
                            // Further processing of the file...
                            languages.add(entry.getName().substring("translate/".length()));
                        }
                    }
                }
            }
            return languages;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCurrentTranslation(String newTrabslationName) throws JsonException {
        InputStream inputStream = TranslationTextComponent.class.getResourceAsStream("/translate/" + newTrabslationName);
        if (inputStream == null) {
            throw new NullPointerException();
        }
        currentJson = new Json(TranslationTextComponent.class.getResourceAsStream("/translate/" + newTrabslationName));
        fileName = newTrabslationName;
    }

    private String getTranslationFromKey() {
        IUnitJson obj = currentJson.get(key);
        return obj == null ? key : (String) obj.getValue();
    }

    @Override
    public String toString() {
        if (this.key == null)
            return "null";
        if (this.arguments != null) {
            return String.format(getTranslationFromKey(), arguments);
        }
        return getTranslationFromKey();
    }



}
