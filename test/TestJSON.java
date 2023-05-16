import JSON_Editor.util.json.Json;
import JSON_Editor.util.json.UnitJson;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestJSON {
    @Test
    public void testSimpleJsonUnitsList() {
        List<UnitJson> expected = new ArrayList<UnitJson>();
        expected.add(new UnitJson("name", ((Object)"Test"), UnitJson.TypeValue.STRING));
        expected.add(new UnitJson("tester", ((Object)"123"), UnitJson.TypeValue.NUMBER));

        try {
            Json actualJson = new Json(new File(
                    Objects.requireNonNull(getClass().getResource("/resource/simpleUnits.json")).getPath()
            ));
            List<UnitJson> actual = actualJson.getUnitsValue();
            assertEquals(expected, actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

/*    @Test
    public void testJsonOBJ(){
        StringBuilder str_J = new StringBuilder();
        try (FileReader reader = new FileReader(getClass().getResource("/resurs/simpleUnits.json").getPath())){
            while (reader.ready()){
                str_J.append((char)reader.read());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //List<JSON_Editor.
    }*/
}
