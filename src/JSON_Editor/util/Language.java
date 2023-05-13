package JSON_Editor.util;

public class Language {
    public static Languages Lang;
    private final String key;

    public Language(final String key) {
        this.key = key;
    }

    public String getString() {
        return "Саня умный";
    }

    enum Languages {
        ru_RU,
        en_US
    }

}
