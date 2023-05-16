import JSON_Editor.util.json.Json;
import JSON_Editor.util.json.UnitJson;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestJSON {
/*    @Test
    public void testSimpleJsonUnitsList(){
        List<UnitJson> expected = new ArrayList<UnitJson>();
        expected.add(new UnitJson("name", (Object)"Test", UnitJson.TypeValue.STRING));
        expected.add(new UnitJson("tester", (Object)"123", UnitJson.TypeValue.NUMBER));


        StringBuilder str_J = new StringBuilder();
        try (FileReader reader = new FileReader(getClass().getResource("/simple.json").getPath())){
            while (reader.ready()){
                str_J.append((char)reader.read());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        Json actualJson= new Json(str_J.toString());
        List<UnitJson> actual = actualJson.getUnits();
        assertEquals(expected, actual);
    }*/

/*    @Test
    public void testJsonOBJ(){
        StringBuilder str_J = new StringBuilder();
        try (FileReader reader = new FileReader(getClass().getResource("/resurs/simple.json").getPath())){
            while (reader.ready()){
                str_J.append((char)reader.read());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //List<JSON_Editor.
    }*/
}
