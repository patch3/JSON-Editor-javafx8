package com.editor.util;

import com.editor.Main;
import com.editor.util.json.IUnitJson;
import com.editor.util.json.Json;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;



public class TranslationTextComponent {
    private static File[] translateFiles;
    public static File currentTranslation;



    private final String key;
    private final Object[] arguments;


    public TranslationTextComponent(String key) {
        this.key = key;
        this.arguments = null;
    }

    public TranslationTextComponent(String key, Object... args) {
        this.key = key;
        this.arguments = args;
    }
    /**
     * Подгрузить выбранный язык
     * */
    public static void loadLangs() {
        try {
            currentTranslation = new File(Objects.requireNonNull(Main.class.getResource("/translate/en_UK.json")).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        File langDir;
        try {
            langDir = new File(Objects.requireNonNull(Main.class.getResource("/translate")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (!langDir.exists()) return;
        TranslationTextComponent.translateFiles = langDir.listFiles();
        System.out.println("The following languages have been loaded: ");
        Arrays.stream(translateFiles).map(File::getName).forEach(System.out::println);
    }

    public static void setCurrentTranslation(File newTrabslation){
        currentTranslation = newTrabslation;
    }

    private String getTranslationFromKey() {
        Json lang;
        try {
            lang = new Json(currentTranslation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IUnitJson obj = lang.get(key);

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

    public static File[] getTranslateFiles() {
        return translateFiles;
    }
}
