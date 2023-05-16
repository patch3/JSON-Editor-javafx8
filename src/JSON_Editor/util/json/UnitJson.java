package JSON_Editor.util.json;

import JSON_Editor.util.Interpreter;
import com.sun.istack.internal.Nullable;

public class UnitJson extends ArrayUnitJson {
    public String name;
    @Nullable
    private Object value;
    @Nullable
    public String comment;
    private TypeValue typeValue;

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



    public int nameInterpreter(char[] chStr, int i) {
        this.name = Interpreter.string(chStr, i);
        return this.name.length() + 1 + i;
    }



}
