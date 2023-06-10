package src.util;

import src.Main;
import src.util.json.IUnitJson;
import src.util.json.Json;
import src.util.json.UnitJson;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TranslationTextComponent {
    public static File[] translates;
    public static File currentTranslation;

    private final boolean withArgs;
    private Object[] arguments;

    static {
        try {
            currentTranslation = new File(Objects.requireNonNull(Main.class.getResource("config/translate/en_UK.json")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private final String key;

    public TranslationTextComponent(String key){
        this.key = key.toLowerCase();
        withArgs = false;
    }
    public TranslationTextComponent(String key, Object... args){
        this.key = key.toLowerCase();
        withArgs = true;
        arguments = args;
    }


    private String getTranslationFromKey(){
        Json lang;
        try {
            lang = new Json(currentTranslation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        IUnitJson obj = lang.get(key);

        return obj == null ? key : (String)obj.getValue();
    }

    public static void loadLangs(){
        File langDir;
        try {
            langDir = new File(Objects.requireNonNull(Main.class.getResource("config/translate/")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (!langDir.exists()) return;
        translates = langDir.listFiles();
        System.err.println("The following languages have been loaded: ");
        Arrays.stream(translates).forEach(file -> System.err.println(file.getName()));
    }


    @Override
    public String toString(){

        if (withArgs){
            return String.format(getTranslationFromKey(), arguments);
        }
        return getTranslationFromKey();
    }


}
