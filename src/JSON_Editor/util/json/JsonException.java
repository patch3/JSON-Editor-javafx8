package JSON_Editor.util.json;

public class JsonException extends RuntimeException {
    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, int index) {
        super(message + " in index of '" + index + '"');
    }


}
