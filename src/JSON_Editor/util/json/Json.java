package JSON_Editor.util.json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import JSON_Editor.util.Interpreter;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.util.List;

public class Json {
    @Nullable
    public ValueUnitsJson units;

    public Json(File fileJson) throws IOException  {
        this(
            new String(
                Files.readAllBytes(
                        Paths.get(
                                fileJson.getAbsolutePath()
        ))));
    }

    public Json(String str) {
        char[] chStr = str.toCharArray();
        units = new ValueUnitsJson(
                str.toCharArray(),
                Interpreter.skipChar(chStr, 0)
        );
    }

    public ValueUnitsJson getUnits() {
        return this.units;
    }

    public void setUnits(ValueUnitsJson units) {
        this.units = units;
    }

    public Object getValue() {
        return units.getValue();
    }

    public ValueUnitsJson.TypeValue getTypeValue() {
        return units.getType();
    }

    public List<UnitJson> getUnitsValue() {
        return units.getUnitsValue();
    }

    public List<ArrayUnitJson> getArrayValue() {
        return units.getArrayValue();
    }
}