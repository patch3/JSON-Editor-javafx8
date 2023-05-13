import JSON_Editor.util.json.Json;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestJSON {
    @Test
    public void testJsonOBJ(){
        StringBuilder str_J = new StringBuilder();
        try (FileReader reader = new FileReader(getClass().getResource("/resurs/package.json").getPath())){
            while (reader.ready()){
                str_J.append((char)reader.read());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        Json json = new Json(str_J.toString());

        JSON_Editor.util.json.Json json2 = new Json("{}");
        assertEquals(json, json2);
    }

/*    @Test
    public void testJsonOBJ(){
        StringBuilder str_J = new StringBuilder();
        try (FileReader reader = new FileReader(getClass().getResource("/resurs/package.json").getPath())){
            while (reader.ready()){
                str_J.append((char)reader.read());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //List<JSON_Editor.
    }*/
}
