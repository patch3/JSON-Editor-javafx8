import org.junit.jupiter.api.Test;
import util.json.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTestJson {

    @Test
    public void testHardIndexOf() throws IOException {
        Json actualJson = new Json(new File(
                Objects.requireNonNull(
                        getClass()
                                .getResource("/resource/hard.json")
                ).getPath()
        ));
        int[] expected = {2, 1};
        ArrayUnitJson arrUnit = actualJson.getUnitValueIn(expected[0]).getUnitList().getArrayValueIn(expected[1]);
        int[] actual = actualJson.indexOf(arrUnit, TypeUnit.ARRAY_UNIT);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testHardGetJson() throws IOException {
        List<UnitJson> valueJson = new ArrayList<>();
        valueJson.add(new UnitJson("name", "resource", UnitJson.TypeValue.STRING));
        valueJson.add(new UnitJson("version", "3647", UnitJson.TypeValue.NUMBER));

        ArrayUnitJson expected = new ArrayUnitJson("575", UnitJson.TypeValue.NUMBER);

        List<ArrayUnitJson> attachment = new ArrayList<>();
        attachment.add(new ArrayUnitJson("egeg", UnitJson.TypeValue.STRING));
        attachment.add(expected);

        attachment.add(new ArrayUnitJson(
                new ValueUnitsJson(
                        null,
                        TypeUnit.UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY
        ));
        attachment.add(new ArrayUnitJson(
                new ValueUnitsJson(
                        null,
                        TypeUnit.ARRAY_UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY
        ));
        valueJson.add(new UnitJson(
                "dependencies",
                new ValueUnitsJson(
                        attachment,
                        TypeUnit.ARRAY_UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY)
        );
        Json Json = new Json(valueJson, TypeUnit.UNIT);

        int[] expectedIndex = {2, 1};
        ArrayUnitJson actual = (ArrayUnitJson) (Json.get(expectedIndex));

        assertEquals(expected, actual);
    }


}
