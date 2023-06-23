import com.editor.util.TranslationTextComponent;
import com.json.JsonException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestTranslationTextComponent {
    @Test
    public void testTranslateRu() throws JsonException {
        TranslationTextComponent.setCurrentTranslation("ru_RU.json");

        String actual = new TranslationTextComponent("error.interpreter.exp_start_char_str").toString();
        assertEquals("Ожидался начальный символ строки [\"]", actual);

    }
}