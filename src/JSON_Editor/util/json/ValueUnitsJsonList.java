package util.json;

import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import util.Interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ValueUnitsJsonList {
    public static final char[] START_VALUE_CHAR = {'"', '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '[', '{'};
    protected List<IUnitJson> value;
    protected TypeUnit type;

    public ValueUnitsJsonList() {
    }

    public ValueUnitsJsonList(char[] chStr, int i) {
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

    public ValueUnitsJsonList(List<? extends IUnitJson> value, TypeUnit type) {
        if (value == null) {
            this.value = null;
        } else {
            this.value = value.stream()
                    .map(unit -> (IUnitJson)unit)
                    .collect(Collectors.toList());
        }
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueUnitsJsonList that = (ValueUnitsJsonList) o;
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
                        this.value = units.stream()
                                .map(unitJson -> (IUnitJson) unitJson)
                                .collect(Collectors.toList());
                        this.type = TypeUnit.UNIT;
                        return i;
                    } else if (start) {
                        this.value = null;
                        this.type = TypeUnit.UNIT;
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
                        this.value = units.stream()
                                .map(unitJson -> (IUnitJson) unitJson)
                                .collect(Collectors.toList());
                        this.type = TypeUnit.ARRAY_UNIT;
                        return i;
                    } else if (start) {
                        this.value = null;
                        this.type = TypeUnit.ARRAY_UNIT;
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

    /*public List<Integer> indexOf(Object value, TypeUnit type, List<Integer> result) {
        List<? extends IUnitJson> list;
        int hash;
        int size;
        if (type == TypeUnit.UNIT && this.type == TypeUnit.UNIT) {
            list = getUnitsValue();
            if (list == null) return null;
            hash = ((UnitJson) value).hashCode();
            size = list.size();
            for (int i = 0; i < size; ++i) {
                UnitJson unit = (UnitJson) list.get(i);
                if (unit.hashCode() == hash) {
                    result.add(i);
                    return result;
                } else if (unit.getTypeValue() == ArrayUnitJson.TypeValue.UNITS_ARRAY) {
                    List<Integer> tempResult = new ArrayList<Integer>(result);
                    tempResult.add(i);
                    tempResult = ((ValueUnitsJsonList) unit.getValue()).indexOf(value, type, tempResult);
                    if (tempResult != null) {
                        return tempResult;
                    }
                }
            }
        } else if (type == TypeUnit.ARRAY_UNIT && this.type == TypeUnit.ARRAY_UNIT) {
            list = getArrayValue();
            hash = ((ArrayUnitJson) value).hashCode();
            if (list == null) return null;
            size = list.size();

            for (int i = 0; i < size; ++i) {
                ArrayUnitJson unit = (ArrayUnitJson) list.get(i);
                if (unit.hashCode() == hash) {
                    result.add(i);
                    return result;
                } else if (unit.getTypeValue() == ArrayUnitJson.TypeValue.UNITS_ARRAY) {
                    List<Integer> tempResult = new ArrayList<>(result);
                    tempResult.add(i);
                    tempResult = ((ValueUnitsJsonList) unit.getValue()).indexOf(value, type, tempResult);
                    if (tempResult != null) {
                        return tempResult;
                    }
                }
            }
        } else if (type == TypeUnit.ARRAY_UNIT && this.type == TypeUnit.UNIT) {
            list = getUnitsValue();
            if (list == null) return null;
            size = list.size();

            for (int i = 0; i < size; ++i) {
                UnitJson unit = (UnitJson) list.get(i);
                if (unit.getTypeValue() == ArrayUnitJson.TypeValue.UNITS_ARRAY) {
                    List<Integer> tempResult = new ArrayList<>(result);
                    tempResult.add(i);
                    tempResult = ((ValueUnitsJsonList) unit.getValue()).indexOf(value, type, tempResult);
                    if (tempResult != null) {
                        return tempResult;
                    }
                }
            }
        } else if (type == TypeUnit.UNIT && this.type == TypeUnit.ARRAY_UNIT) {
            list = getArrayValue();
            if (list == null) return null;
            size = list.size();

            for (int i = 0; i < size; ++i) {
                ArrayUnitJson unit = (ArrayUnitJson) list.get(i);
                if (unit.getTypeValue() == ArrayUnitJson.TypeValue.UNITS_ARRAY) {
                    List<Integer> tempResult = new ArrayList<>(result);
                    tempResult.add(i);
                    tempResult = ((ValueUnitsJsonList) unit.getValue()).indexOf(value, type, tempResult);
                    if (tempResult != null) {
                        return tempResult;
                    }
                }
            }
        }
        return null;
    }*/


    public List<Integer> indexOf(IUnitJson value, List<Integer> result) {
        if (this.value == null) return null;
        int size = this.value.size();
        int hash = value.hashCode();

        if (this.type == value.getTypeUnit()){
            for (int i = 0; i < size; ++i) {
                IUnitJson unit = this.value.get(i);
                if (unit.hashCode() == hash){
                    result.add(i);
                    return result;
                }
                List<Integer> tempResult = this.findingElemint(value, unit, i, result);
                if (tempResult != null) {
                    return tempResult;
                }
            }
        } else {
            for (int i = 0; i < size; ++i) {
                IUnitJson unit = this.value.get(i);
                List<Integer> tempResult = this.findingElemint(value, unit, i, result);
                if (tempResult != null) {
                    return tempResult;
                }
            }
        }
        return null;
    }

    private List<Integer> findingElemint(IUnitJson value, IUnitJson unit, Integer i, List<Integer> result){
        if (unit.getTypeValue() == IUnitJson.TypeValue.UNITS_ARRAY) {
            List<Integer> tempResult = new ArrayList<>(result);
            tempResult.add(i);
            tempResult = ((ValueUnitsJsonList) unit.getValue()).indexOf(value, tempResult);
            return tempResult;
        }
        return null;
    }


    public List<IUnitJson> getValue() {
        return value;
    }

    public Object getValueIn(int i) {
        return this.value.get(i);
    }

    public UnitJson getUnitValueIn(int i) {
        return getUnitsValue().get(i);
    }

    public ArrayUnitJson getArrayValueIn(int i) {
        return getArrayValue().get(i);
    }


    public TypeUnit getType() {
        return this.type;
    }

    public TypeUnit getTypeUnit(int i) {
        return ((IUnitJson) this.value.get(i)).getTypeUnit();
    }

    public ArrayUnitJson.TypeValue getTypeValue(int i) {
        return ((IUnitJson) this.value.get(i)).getTypeValue();
    }


    // преобразование UnitJson
    public List<UnitJson> getUnitsValue() {
        if (this.type != TypeUnit.UNIT || this.value == null)
            throw new JsonException("WRONG_TYPE");
        return this.value.stream()
                .map(unit -> (UnitJson) unit)
                .collect(Collectors.toList());
    }


    public List<ArrayUnitJson> getArrayValue() {
        if (this.type != TypeUnit.ARRAY_UNIT || this.value == null)
            throw new JsonException("WRONG_TYPE");
        return this.value.stream()
                .map(unit -> (ArrayUnitJson) unit)
                .collect(Collectors.toList());
    }


    public void setValue(List<IUnitJson> value, TypeUnit type) {
        this.value = value;
        this.type = type;
    }

    /*public enum TypeValue {
        UNITS,
        ARRAY
    }*/

}
