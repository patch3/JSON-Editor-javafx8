import com.json.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;


public class TestJSON {
    @Test
    public void testSimpleJsonUnitsList() throws JsonException, IOException {
        List<UnitJson> expected = new ArrayList<>();
        expected.add(new UnitJson("name", "Test", UnitJson.TypeValue.STRING));
        expected.add(new UnitJson("tester", "123", UnitJson.TypeValue.NUMBER));

        Json actualJson = new Json(new File(
                Objects.requireNonNull(getClass().getResource("/json/simple_units.json")).getPath()
        ));
        List<UnitJson> actual = actualJson.getUnitsValue();
        assertEquals(expected, actual);

    }

    @Test
    public void testAttachmentsUnits() throws JsonException, IOException {
        List<UnitJson> expected = new ArrayList<>();
        expected.add(new UnitJson("name", "resurs", UnitJson.TypeValue.STRING));
        expected.add(new UnitJson("version", "123", UnitJson.TypeValue.NUMBER));

        List<UnitJson> attachment = new ArrayList<>();
        attachment.add(new UnitJson("uuid", "9.0.0", UnitJson.TypeValue.STRING));
        attachment.add(new UnitJson("fs-extra", "11", UnitJson.TypeValue.NUMBER));

        expected.add(new UnitJson(
                "dependencies",
                new ValueUnitsJsonList(
                        attachment,
                        TypeUnit.UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY)
        );

        Json actualJson = new Json(new File(
                Objects.requireNonNull(
                        getClass()
                                .getResource("/json/attachments_units.json")
                ).getPath()
        ));
        List<UnitJson> actual = actualJson.getUnitsValue();
        assertEquals(expected, actual);

    }

    @Test
    public void testSimpleArray() throws JsonException, IOException {
        List<ArrayUnitJson> expected = new ArrayList<>();
        expected.add(new ArrayUnitJson("46,56,78", UnitJson.TypeValue.STRING));
        expected.add(new ArrayUnitJson("12345", UnitJson.TypeValue.NUMBER));


        Json actualJson = new Json(new File(
                Objects.requireNonNull(
                        getClass()
                                .getResource("/json/simple_array.json")
                ).getPath()
        ));
        List<ArrayUnitJson> actual = actualJson.getArrayValue();
        assertEquals(expected, actual);

    }

    @Test
    public void testAttachmentsArray() throws JsonException, IOException {
        List<ArrayUnitJson> expected = new ArrayList<>();
        expected.add(new ArrayUnitJson("name", UnitJson.TypeValue.STRING));
        expected.add(new ArrayUnitJson("3566", UnitJson.TypeValue.NUMBER));

        List<ArrayUnitJson> attachment = new ArrayList<>();
        attachment.add(new ArrayUnitJson("test", UnitJson.TypeValue.STRING));
        attachment.add(new ArrayUnitJson("436", UnitJson.TypeValue.NUMBER));

        expected.add(
                new ArrayUnitJson(
                        new ValueUnitsJsonList(
                                attachment,
                                TypeUnit.ARRAY_UNIT
                        ), ArrayUnitJson.TypeValue.UNITS_ARRAY
                ));


        Json actualJson = new Json(new File(
                Objects.requireNonNull(
                        getClass()
                                .getResource("/json/attachments_array.json")
                ).getPath()
        ));
        List<ArrayUnitJson> actual = actualJson.getArrayValue();
        assertEquals(expected, actual);

    }

    @Test
    public void testHardJson() throws JsonException, IOException {
        List<UnitJson> expected = new ArrayList<>();
        expected.add(new UnitJson("name", "resource", UnitJson.TypeValue.STRING));
        expected.add(new UnitJson("version", "3647", UnitJson.TypeValue.NUMBER));

        List<ArrayUnitJson> attachment = new ArrayList<>();
        attachment.add(new ArrayUnitJson("egeg", UnitJson.TypeValue.STRING));
        attachment.add(new ArrayUnitJson("575", UnitJson.TypeValue.NUMBER));
        attachment.add(new ArrayUnitJson(
                new ValueUnitsJsonList(
                        null,
                        TypeUnit.UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY
        ));
        attachment.add(new ArrayUnitJson(
                new ValueUnitsJsonList(
                        null,
                        TypeUnit.ARRAY_UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY
        ));


        expected.add(new UnitJson(
                "dependencies",
                new ValueUnitsJsonList(
                        attachment,
                        TypeUnit.ARRAY_UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY)
        );


        Json actualJson = new Json(new File(
                Objects.requireNonNull(
                        getClass()
                                .getResource("/json/hard.json")
                ).getPath()
        ));
        if (actualJson.getType() == TypeUnit.ARRAY_UNIT) {
            List<UnitJson> actual = actualJson.getUnitsValue();
            assertEquals(expected, actual);
        } else if (actualJson.getType() == TypeUnit.UNIT) {
            List<UnitJson> actual = actualJson.getUnitsValue();
            assertEquals(expected, actual);
        }


    }

    @Test
    public void testHardMinJson() throws JsonException, IOException {
        List<UnitJson> expected = new ArrayList<>();
        expected.add(new UnitJson("name", "resource", UnitJson.TypeValue.STRING));
        expected.add(new UnitJson("version", "3647", UnitJson.TypeValue.NUMBER));

        List<ArrayUnitJson> attachment = new ArrayList<>();
        attachment.add(new ArrayUnitJson("egeg", UnitJson.TypeValue.STRING));
        attachment.add(new ArrayUnitJson("575", UnitJson.TypeValue.NUMBER));
        attachment.add(new ArrayUnitJson(
                new ValueUnitsJsonList(
                        null,
                        TypeUnit.UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY
        ));
        attachment.add(new ArrayUnitJson(
                new ValueUnitsJsonList(
                        null,
                        TypeUnit.ARRAY_UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY
        ));


        expected.add(new UnitJson(
                "dependencies",
                new ValueUnitsJsonList(
                        attachment,
                        TypeUnit.ARRAY_UNIT
                ), UnitJson.TypeValue.UNITS_ARRAY)
        );


        Json actualJson = new Json(new File(
                Objects.requireNonNull(
                        getClass()
                                .getResource("/json/hard.json")
                ).getPath()
        ));
        if (actualJson.getType() == TypeUnit.ARRAY_UNIT) {
            List<UnitJson> actual = actualJson.getUnitsValue();
            assertEquals(expected, actual);
        } else if (actualJson.getType() == TypeUnit.UNIT) {
            List<UnitJson> actual = actualJson.getUnitsValue();
            assertEquals(expected, actual);
        }

    }
}
