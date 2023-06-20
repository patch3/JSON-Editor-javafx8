package com.json;

import com.editor.util.TranslationTextComponent;

public class JsonException extends Exception {
    public JsonException(String message) {
        super(new TranslationTextComponent(message).toString());
    }

    public JsonException(String message, int index) {
        super(
                new TranslationTextComponent(message).toString() + ' ' +
                        new TranslationTextComponent("in_index").toString()  +
                        " \"" + index + '\"'
        );
    }

    public JsonException(JsonException ex) {
        super(ex.getMessage());
    }
}
