package util.json;

import util.Interpreter;

import java.util.Objects;

public class UnitJson extends ArrayUnitJson {

    public String name;
    public UnitJson() {
        super();
    }

    public UnitJson(String name) {
        super();
        this.name = name;
    }


    public UnitJson(String name, Object value, TypeValue type) {
        super(value, type);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UnitJson unitJson = (UnitJson) o;
        return Objects.equals(name, unitJson.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public int hashCodeContent() {
        return Objects.hash(super.hashCodeContent(), name);
    }

    public int nameInterpreter(char[] chStr, int i) {
        this.name = Interpreter.string(chStr, i);
        return this.name.length() + 1 + i;
    }
}
