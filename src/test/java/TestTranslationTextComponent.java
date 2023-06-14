import com.editor.Main;
import com.editor.util.TranslationTextComponent;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class TestTranslationTextComponent {

    @Test
    public void testTranslateRu(){
        try {
            TranslationTextComponent.setCurrentTranslation(new File(Main.class.getResource("/translate/ru_RU.json").toURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String actual =  new TranslationTextComponent("error.interpreter.exp_start_char_str").toString();
        assertEquals("Ожидался начальный символ строки [\"]", actual);
    }
}
//"error.json.exp_str": "Ожидалась строка"