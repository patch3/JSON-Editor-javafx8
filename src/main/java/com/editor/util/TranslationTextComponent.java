package com.editor.util;

import com.editor.Main;
import com.editor.util.json.IUnitJson;
import com.editor.util.json.Json;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

/*public class TranslationTextComponent {
    public static File[] translates;
    public static File currentTranslation;

    static {
        try {
            currentTranslation = new File(Objects.requireNonNull(Main.class.getResource("/translate/en_UK.json")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    //private final boolean withArgs;
    private final String key;
    private Object[] arguments;

    public TranslationTextComponent(String key) {
        this.key = key.toLowerCase();
       // this.withArgs = false;
    }

    public TranslationTextComponent(String key, Object... args) {
        this.key = key.toLowerCase();
        //withArgs = true;
        arguments = args;
    }

    public static void loadLangs() {
        File langDir;
        try {
            langDir = new File(Objects.requireNonNull(Main.class.getResource("/translate/")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (!langDir.exists()) return;
        translates = langDir.listFiles();
        System.err.println("The following languages have been loaded: ");
        Arrays.stream(translates).forEach(file -> System.err.println(file.getName()));
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
            return String.format(getTranslationFromKey(), this.arguments);
        }
        return getTranslationFromKey();
    }


}*/

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
        Arrays.stream(translateFiles).map(File::getName).forEach(System.err::println);
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
        if (key == null)
            return "null";

        if (arguments != null) {
            return String.format(getTranslationFromKey(), arguments);
        }
        return getTranslationFromKey();
    }

    public static File[] getTranslateFiles() {
        return translateFiles;
    }
}
