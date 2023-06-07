package util.json;

import util.Interpreter;

import java.util.List;
import java.util.Objects;

public class UnitJson extends AbstractElementJson {
    protected String name;

    public UnitJson() {
        super(TypeUnit.ARRAY_UNIT);
    }

    public UnitJson(String name) {
        super(TypeUnit.ARRAY_UNIT);
        this.name = name;
    }


    public UnitJson(String name, Object value, TypeValue type) {
        super(value, type, TypeUnit.ARRAY_UNIT);
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
    public String toString() {
        return String.format("\"%s\":%s", this.name, super.toString());
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

    @Override
    public Object getValue() {
        return super.getValue();
    }



    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(Object obj){
        if (obj instanceof String){
            this.name = (String) obj;
        } else {
            throw new IllegalArgumentException("Ожидался String");
        }
    }
}
