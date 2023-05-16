package JSON_Editor.util.json;

import JSON_Editor.util.Interpreter;
import com.sun.istack.internal.Nullable;

import java.util.Objects;

public class UnitJson extends ArrayUnitJson {
    public String name;
    /*@Nullable
    private Object value;*/
    @Nullable
    public String comment;
    /*private TypeValue typeValue;
*/
    public UnitJson() {
    }

    public UnitJson(String name) {
        this.name = name;
    }


    public UnitJson(String name, Object value, TypeValue type) {
        this.name = name;
        this.value = value;
        this.typeValue = type;
        this.comment = null;
    }

    public UnitJson(String name, Object value, TypeValue type, String comment) {
        this.name = name;
        this.value = value;
        this.typeValue = type;
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UnitJson unitJson = (UnitJson) o;
        return Objects.equals(name, unitJson.name) && Objects.equals(comment, unitJson.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, comment);
    }

    public int nameInterpreter(char[] chStr, int i) {
        this.name = Interpreter.string(chStr, i);
        return this.name.length() + 1 + i;
    }



}
