package JSON_Editor.util.json;

import JSON_Editor.util.Interpreter;
import com.sun.istack.internal.Nullable;

import java.util.List;
import java.util.Objects;

public class ArrayUnitJson {

    public static final char[] START_VALUE_CHAR = {'"', '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '[', '{'};

    @Nullable
    protected Object value;
    protected TypeValue typeValue;

    public ArrayUnitJson() {
    }

    public ArrayUnitJson(Object value, TypeValue typeValue) {
        this.typeValue = typeValue;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayUnitJson that = (ArrayUnitJson) o;
        return Objects.equals(value, that.value) && typeValue == that.typeValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, typeValue);
    }

    public int valueInterpreter(char[] chStr, int i) {
        i = Interpreter.skipChar(chStr, i);
        char ch = chStr[i];
        switch (ch) {
            case '"':
                this.value = Interpreter.string(chStr, i);
                this.typeValue = TypeValue.STRING;
                return ((String) this.value).length() + 1 + i;
            case '{':
                this.value = new ValueUnitsJson();
                this.typeValue = TypeValue.UNITS_ARRAY;
                return ((ValueUnitsJson) this.value).unitsInterpreter(chStr, i);
            case '[':
                this.value = new ValueUnitsJson();
                this.typeValue = TypeValue.UNITS_ARRAY;
                return ((ValueUnitsJson) this.value).arrayInterpreter(chStr, i);
            default:
                if (Character.isDigit(ch) || ch == '-') {
                    this.value = Interpreter.numberStr(chStr, i);
                    this.typeValue = TypeValue.NUMBER;
                    return ((String) this.value).length() + i - 1;
                }
                throw new JsonException("EXPECTED_VALUE", i);
        }
    }



    /*protected int valueUnitsInterpreter(char[] chStr, int i) {
        this.value = new ValueUnitsJson();
        this.typeValue = TypeValue.UNITS_ARRAY;
        return ((ValueUnitsJson)this.value).unitsInterpreter(chStr, i);
    }*/

    public int valueStringInterpreter(char[] chStr, int i) {
        if (chStr[i] != '"')
            throw new JsonException("EXP_STR", i);

        return this.valueStringInterpreterUnSafe(chStr, i);
    }

    protected int valueStringInterpreterUnSafe(char[] chStr, int i) {
        this.value = Interpreter.string(chStr, i);
        this.typeValue = TypeValue.STRING;
        return ((String) this.value).length() + 1 + i;
    }

    /*protected int valueDigitInterpreter(char[] chStr, int i) {
        this.value = Interpreter.numberStr(chStr, i);
        this.typeValue = TypeValue.NUMBER;
        return ((String)this.value).length() + i;
    }*/

    public String getStringValue() {
        if (this.typeValue != TypeValue.STRING) {
            throw new JsonException("Value is not of type STRING");
        }
        return String.format("\"%s\"", this.value);
    }

    public String getNumStringValue() {
        if (this.typeValue != TypeValue.NUMBER) {
            throw new JsonException("Value is not of type NUMBER");
        }
        return (String) this.value;
    }

    public List<UnitJson> getUnitList() {
        if (this.typeValue != TypeValue.UNITS_ARRAY) {
            throw new JsonException("Value is not of type UNITS_ARRAY");
        }
        return (List<UnitJson>) this.value;
    }


    public Object getValue() {
        return value;
    }

    public TypeValue getTypeValue() {
        return typeValue;
    }

    public void setValue(Object value, TypeValue typeValue) {
        this.typeValue = typeValue;
        this.value = value;
    }

    public enum TypeValue {
        NUMBER,
        STRING,
        UNITS_ARRAY
    }
}
