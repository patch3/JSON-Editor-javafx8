package com.json;

import com.editor.util.Convert;
import com.sun.istack.internal.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class AbstractElementJson implements IUnitJson {

    private static int countId = Integer.MIN_VALUE;

    public final TypeUnit TYPE_UNIT;

    @Nullable
    protected Object value;

    protected TypeValue typeValue;

    protected int id;

    public AbstractElementJson(TypeUnit type) {
        this.id = countId++;
        this.TYPE_UNIT = type;
    }

    public AbstractElementJson(Object value, TypeValue typeValue, TypeUnit type) {
        this(type);
        this.typeValue = typeValue;
        this.value = value;
    }

    public AbstractElementJson(AbstractElementJson obj) {
        this.id = obj.id;
        this.TYPE_UNIT = obj.TYPE_UNIT;
        this.value = obj.value;
        this.typeValue = obj.typeValue;
    }

    public int valueInterpreter(char[] chStr, int i) throws JsonException {
        i = Interpreter.skipChar(chStr, i);
        char ch = chStr[i];
        switch (ch) {
            case '"':
                this.value = Interpreter.string(chStr, i);
                this.typeValue = TypeValue.STRING;
                return Convert.countRecord((String) this.value) + i + 2;
            case '{':
                this.value = new ValueUnitsJsonList();
                this.typeValue = TypeValue.UNITS_ARRAY;
                return ((ValueUnitsJsonList) this.value).unitsInterpreter(chStr, i) + 1;
            case '[':
                this.value = new ValueUnitsJsonList();
                this.typeValue = TypeValue.UNITS_ARRAY;
                return ((ValueUnitsJsonList) this.value).arrayInterpreter(chStr, i) + 1;
            default:
                if (Character.isDigit(ch) || ch == '-') {
                    this.value = Interpreter.numberStr(chStr, i);
                    this.typeValue = TypeValue.NUMBER;
                    return ((String) this.value).length() + i;
                }
                throw new JsonException("error.json.exp_value", i );
        }
    }

    public int valueStringInterpreter(char[] chStr, int i) throws JsonException {
        if (chStr[i] != '"')
            throw new JsonException("error.json.exp_str", i);

        return this.valueStringInterpreterUnSafe(chStr, i);
    }

    protected int valueStringInterpreterUnSafe(char[] chStr, int i) throws JsonException {
        this.value = Interpreter.string(chStr, i);
        this.typeValue = TypeValue.STRING;
        return ((String) this.value).length() + 1 + i;
    }

    public String getStringValue() throws RuntimeException {
        if (this.typeValue != TypeValue.STRING) {
            throw new RuntimeException("Value is not of type STRING");
        }
        return String.format("\"%s\"", this.value);
    }

    public String getNumStringValue() throws RuntimeException {
        if (this.typeValue != TypeValue.NUMBER) {
            throw new RuntimeException("Value is not of type NUMBER");
        }
        return (String) this.value;
    }


    public ValueUnitsJsonList getUnitList() throws RuntimeException {
        if (this.typeValue != TypeValue.UNITS_ARRAY) {
            throw new RuntimeException("Value is not of type UNITS_ARRAY");
        }
        return (ValueUnitsJsonList) this.value;
    }

    @Override
    public TypeUnit getTypeUnit() {
        return this.TYPE_UNIT;
    }

    @Override
    public List<IUnitJson> getValueList() {
        return this.getUnitList().getValue();
    }

    @Override
    public TypeValue getTypeValue() {
        return typeValue;
    }

    @Override
    public void setValue(Object value, TypeValue typeValue) throws JsonException {

        this.value = value;
        this.typeValue = typeValue;
    }

    public boolean equalsContent(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractElementJson that = (AbstractElementJson) o;
        return Objects.equals(value, that.value) && typeValue == that.typeValue;
    }

    public int hashCodeContent() {
        return Objects.hash(value, typeValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractElementJson that = (AbstractElementJson) o;
        return id == that.id && Objects.equals(value, that.value) && typeValue == that.typeValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, typeValue, id);
    }

    public String toString() {
        return this.toString(0);
    }

    // toString с глубеной
    public String toString(int d) {
        if (this.typeValue == TypeValue.UNITS_ARRAY) {
            return this.getUnitList().toString(d);
        } else if (this.typeValue == TypeValue.STRING) {
            return String.format("\"%s\"", Convert.toRecord((String)this.value));
        }
        if (this.typeValue == TypeValue.NUMBER) {
            return (String)this.value;
        }
        return null;
    }

    @Override
    public abstract String getName();

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(IUnitJson obj) {
        this.value = obj.getValue();
        this.typeValue = obj.getTypeValue();
    }
}