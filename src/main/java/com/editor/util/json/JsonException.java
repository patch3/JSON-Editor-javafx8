package com.editor.util.json;

import com.editor.util.TranslationTextComponent;

public class JsonException extends RuntimeException {
    public JsonException(String message) {
        super(new TranslationTextComponent(message).toString());
    }

    public JsonException(String message, int index) {
        super(
                new TranslationTextComponent(message).toString() + ' ' +
                        new TranslationTextComponent("in_index") +
                        " \"" + index + '\"'
        );
    }
}
