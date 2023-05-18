package JSON_Editor.util.json;

import JSON_Editor.util.Interpreter;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static JSON_Editor.util.json.ArrayUnitJson.START_VALUE_CHAR;

public class ValueUnitsJson {
    private Object value;
    private TypeValue type;

    public ValueUnitsJson() {
    }

    public ValueUnitsJson(char[] chStr, int i) {
        switch (chStr[i]) {
            case '{':
                this.unitsInterpreter(chStr, i);
                break;
            case '[':
                this.arrayInterpreter(chStr, i);
                break;
            default:
                throw new JsonException("UNEXPECTED_OPEN_CHAR", i);
        }
    }

    public ValueUnitsJson(Object value, TypeValue type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueUnitsJson that = (ValueUnitsJson) o;
        return Objects.equals(value, that.value) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    public int unitsInterpreter(char[] chStr, int i) {
        if (chStr[i] != '{') {
            throw new SyntaxException("Interpretation of Unit JSON must start with character '{'");
        }
        ++i;
        boolean start = true;
        boolean postCheckName = false;
        boolean postCheckValue = false;
        boolean postCheckComma = false;

        int temp;

        List<UnitJson> units = new ArrayList<>();
        UnitJson unit = new UnitJson();

        while (i < chStr.length) {
            char ch = chStr[i];

            switch (ch) {
                case '}':
                    if (postCheckValue) {
                        this.value = units;
                        this.type = TypeValue.UNITS;
                        return i;
                    } else if (start) {
                        this.value = null;
                        this.type = TypeValue.UNITS;
                        return i;
                    }
                case ':':
                    if (postCheckName) {
                        postCheckName = false;
                        postCheckValue = true;
                        i = unit.valueInterpreter(chStr, ++i);
                        units.add(unit);
                        break;
                    }
                case ',':
                    if (postCheckValue) {
                        postCheckValue = false;
                        postCheckComma = true;
                        unit = new UnitJson();
                        break;
                    }
                case '"':
                    if (postCheckComma || start) {
                        if (start) start = false;
                        postCheckComma = false;
                        postCheckName = true;
                        i = unit.nameInterpreter(chStr, i);
                        break;
                    }
                default:
                    if (0 < (temp = (Interpreter.skipChar(chStr, i) - 1))) {
                        i = temp;
                        break;
                    } else
                        throw new JsonException("UNEXPECTED_CHAR", i);
            }
            ++i;
        }
        throw new JsonException("UNEXP_END");
    }

    public int arrayInterpreter(char[] chStr, int i) {
        if (chStr[i] != '[') {
            throw new JsonException("ARRAY_EXPECTED", i);
        }
        ++i;
        boolean start = true;
        boolean postCheckValue = false;
        boolean checkValue = true;

        int temp;

        List<ArrayUnitJson> units = new ArrayList<>();
        ArrayUnitJson unit = new ArrayUnitJson();

        while (i < chStr.length) {
            char ch = chStr[i];

            switch (ch) {
                case ',':
                    if (postCheckValue) {
                        checkValue = true;
                        postCheckValue = false;
                        unit = new ArrayUnitJson();
                    }
                    break;
                case ']':
                    if (postCheckValue) {
                        this.value = units;
                        this.type = TypeValue.ARRAY;
                        return i;
                    } else if (start) {
                        this.value = null;
                        this.type = TypeValue.ARRAY;
                        return i;
                    }
                    break;
                default:
                    if (0 <= Arrays.binarySearch(START_VALUE_CHAR, chStr[i]) && checkValue) {
                        if (start) start = false;
                        checkValue = false;
                        postCheckValue = true;
                        i = unit.valueInterpreter(chStr, i);
                        units.add(unit);
                    } else if (0 < (temp = (Interpreter.skipChar(chStr, i) - 1 - i))) {
                        i += temp;
                    } else throw new JsonException("UNDEFINED_ENTRY", i);
                    break;
            }
            ++i;
        }
        throw new JsonException("UNEXPECTED_ENDING");
    }


    public Object getValue() {
        return value;
    }

    public TypeValue getType() {
        return type;
    }

    public List<UnitJson> getUnitsValue() {
        if (this.type != TypeValue.UNITS)
            throw new JsonException("WRONG_TYPE");
        return (List<UnitJson>) this.value;
    }

    public List<ArrayUnitJson> getArrayValue() {
        if (this.type != TypeValue.ARRAY)
            throw new JsonException("WRONG_TYPE");
        return (List<ArrayUnitJson>) this.value;
    }

    public void setValue(Object value, TypeValue type) {
        this.value = value;
        this.type = type;
    }

    public enum TypeValue {
        UNITS,
        ARRAY/*,
        NULL_UNITS,
        NULL_ARRAY*/
    }

}
