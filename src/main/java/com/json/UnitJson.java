package com.json;


import com.editor.util.Convert;

import java.util.Objects;

public class UnitJson extends AbstractElementJson {
    private String name;

    public UnitJson() {
        super(TypeUnit.UNIT);
    }

    public UnitJson(String name) {
        super(TypeUnit.UNIT);
        this.name = name;
    }

    public UnitJson(String name, Object value, TypeValue type) {
        super(value, type, TypeUnit.UNIT);
        this.name = name;
    }

    public UnitJson(IUnitJson obj){
        super(obj.getValue(), obj.getTypeValue(), TypeUnit.UNIT);
        this.name = obj.getName();
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
        return this.toString(0);
    }

    @Override
    public String toString(int d) {
        return String.format("\"%s\": %s", this.getName(), super.toString(d));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public int hashCodeContent() {
        return Objects.hash(super.hashCodeContent(), name);
    }

    public int nameInterpreter(char[] chStr, int i) throws JsonException {
        this.name = Interpreter.string(chStr, i);
        return this.name.length() + 2 + i;
    }

    @Override
    public Object getValue() {
        return super.getValue();
    }


    @Override
    public String getName() {
        return Convert.toRecord(this.name);
    }

    @Override
    public void setName(Object obj) {
        if (obj instanceof String) {
            this.name = (String) obj;
        } else {
            throw new IllegalArgumentException("Ожидался String");
        }
    }
}
