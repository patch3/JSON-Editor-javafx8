package com.editor.util.json;

import com.editor.util.Convert;

import java.util.Objects;

public class ArrayUnitJson extends AbstractElementJson {
    protected int index;


    public ArrayUnitJson() {
        super(TypeUnit.ARRAY_UNIT);
    }

    public ArrayUnitJson(int index) {
        this();
        this.index = index;
    }

    public ArrayUnitJson(Object value, TypeValue typeValue) {
        this();
        this.typeValue = typeValue;
        this.value = value;
    }

    public ArrayUnitJson(Object value, TypeValue typeValue, int index) {
        this(index);
        this.typeValue = typeValue;
        this.value = value;
    }

    public ArrayUnitJson(IUnitJson obj) {
        this(obj.getValue(), obj.getTypeValue());
        if (obj instanceof ArrayUnitJson) {
            this.index = ((ArrayUnitJson) obj).index;
        }
    }

    @Override
    public String toString() {
        if (this.typeValue == TypeValue.UNITS_ARRAY) {
            return "[List]";
        } else if (this.typeValue == TypeValue.STRING) {
            return String.format("\"%s\"", Convert.toRecord((String) this.value));
        }
        return (String) this.value;
    }


    @Override
    public int hashCode() {
        return Objects.hash(value, typeValue, id);
    }

    @Override
    public String getName() {
        return String.format("[%d]", this.index);
    }

    @Override
    public void setName(Object obj) {
        if (obj instanceof Integer) {
            this.index = (int) obj;
        } else {
            throw new IllegalArgumentException("Ожидался index");
        }
    }

    @Override
    public TypeUnit getTypeUnit() {
        return super.TYPE_UNIT;
    }

}
