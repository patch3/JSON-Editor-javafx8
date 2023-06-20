package com.editor.util;

import com.editor.Main;
import com.json.IUnitJson;
import com.json.Json;
import com.json.JsonException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;



public class TranslationTextComponent {
    private static File[] translateFiles;

    public static String fileName;
    private final String key;
    private final Object[] arguments;

    private static Json currentJson;

    static {
        TranslationTextComponent.loadLangs();
    }

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
            currentJson = new Json(new File(Objects.requireNonNull(Main.class.getResource("/translate/en_UK.json")).toURI()));
            fileName = "en_UK.json";
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (JsonException e) {
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

    public static void setCurrentTranslation(File newTrabslation) throws IOException, JsonException {
        currentJson = new Json(newTrabslation);
        fileName = newTrabslation.getName();
    }

    private String getTranslationFromKey() throws JsonException {
        IUnitJson obj = currentJson.get(key);

        return obj == null ? key : (String) obj.getValue();
    }

    @Override
    public String toString() {
        if (this.key == null)
            return "null";
        try {
            if (this.arguments != null) {
                return String.format(getTranslationFromKey(), arguments);
            }
            return getTranslationFromKey();
        } catch (JsonException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static File[] getTranslateFiles() {
        return translateFiles;
    }


}
