import com.editor.Main;
import com.editor.util.TranslationTextComponent;
import com.json.JsonException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class TestTranslationTextComponent {

    @Test
    public void testTranslateRu() throws IOException, URISyntaxException, JsonException {

        TranslationTextComponent.setCurrentTranslation(new File(Main.class.getResource("/translate/ru_RU.json").toURI()));

        String actual = new TranslationTextComponent("error.interpreter.exp_start_char_str").toString();
        assertEquals("Ожидался начальный символ строки [\"]", actual);
    }

}
//"error.json.exp_str": "Ожидалась строка"