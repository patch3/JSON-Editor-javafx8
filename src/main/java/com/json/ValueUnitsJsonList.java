package com.json;


import org.w3c.dom.ranges.RangeException;

import java.util.*;
import java.util.stream.Collectors;

public class ValueUnitsJsonList {
    public static final char[] START_VALUE_CHAR = {'"', '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '[', '{'};

    protected List<IUnitJson> value;

    protected TypeUnit type;

    public ValueUnitsJsonList() {
        this.value = new ArrayList<>();
        this.type = null;
    }

    public ValueUnitsJsonList(char[] chStr, int i) throws JsonException {
        if (chStr.length < 2) {
            this.value = new ArrayList<>();
            this.type = null;
            return;
        }
        switch (chStr[i]) {
            case '{':
                this.unitsInterpreter(chStr, i);
                break;
            case '[':
                this.arrayInterpreter(chStr, i);
                break;
            default:
                throw new JsonException("error.json.value_list.unexp_open_char");
        }
    }

    public ValueUnitsJsonList(TypeUnit type) {
        this.value = new ArrayList<>();
        this.type = type;
    }

    public ValueUnitsJsonList(List<? extends IUnitJson> value, TypeUnit type) {
        if (value == null) {
            this.value = new ArrayList<>();
        } else {
            this.value = value.stream()
                    .map(unit -> (IUnitJson) unit)
                    .collect(Collectors.toList());
        }
        this.type = type;
    }


    public int unitsInterpreter(char[] chStr, int i) throws JsonException {
        if (chStr[i] != '{') {
            throw new JsonException("error.json.value_list.exp_start_brace");
        }
        ++i;
        List<UnitJson> units = new ArrayList<>();
        UnitJson unit = new UnitJson();
        i = Interpreter.skipChar(chStr, i);
        while (i < chStr.length) {
            if (chStr[i] == '}') {
                this.value = this.convertToIUnitJsonList(units);
                this.type = TypeUnit.UNIT;
                return i;
            }
            try {
                i = unit.nameInterpreter(chStr, i);
            } catch (JsonException ex) {
                throw new JsonException(ex);
            }
            i = Interpreter.skipChar(chStr, i);
            if (chStr[i] == ':') {
                i = unit.valueInterpreter(chStr, ++i);
                units.add(unit);
            } else {
                throw new JsonException("error.json.exp_value");
            }
            i = Interpreter.skipChar(chStr, i);
            switch (chStr[i]) {
                case ',':
                    unit = new UnitJson();
                    break;
                case '}':
                    continue;
                default:
                    throw new JsonException("error.json.value_list.exp_start_brace");
            }
            ++i;
            i = Interpreter.skipChar(chStr, i);
        }
        throw new JsonException("error.json.value.unexp_end");
    }

    public int arrayInterpreter(char[] chStr, int i) throws JsonException {
        if (chStr[i] != '[') {
            throw new JsonException("error.json.value.array_expected", i);
        }
        ++i;
        int index = 0;
        i = Interpreter.skipChar(chStr, i);
        List<ArrayUnitJson> units = new ArrayList<>();
        ArrayUnitJson unit = new ArrayUnitJson(index);
        while (i < chStr.length) {
            if (chStr[i] == ']') {
                this.value = this.convertToIUnitJsonList(units);
                this.type = TypeUnit.ARRAY_UNIT;
                return i;
            }
            if (0 <= Arrays.binarySearch(START_VALUE_CHAR, chStr[i])) {
                i = unit.valueInterpreter(chStr, i);
                units.add(unit);
            } else {
                throw new JsonException("error.json.exp_value", i);
            }
            i = Interpreter.skipChar(chStr, i);
            switch (chStr[i]) {
                case ',':
                    unit = new ArrayUnitJson(++index);
                    break;
                case ']':
                    continue;
                default:
                    throw new JsonException("error.json.value_list.exp_start_square_bracket", i);
            }
            ++i;
            i = Interpreter.skipChar(chStr, i);
        }
        throw new JsonException("error.json.value.unexp_end", i);
    }

    public List<IUnitJson> convertToIUnitJsonList(List<? extends IUnitJson> units) {
        return units.stream()
                .map(unitJson -> (IUnitJson) unitJson)
                .collect(Collectors.toList());
    }


    public List<Integer> indexOf(IUnitJson value, List<Integer> result) {
        if (this.value == null) return null;
        int size = this.value.size();
        int hash = value.hashCode();

        if (this.type == value.getTypeUnit()) {
            for (int i = 0; i < size; ++i) {
                IUnitJson unit = this.value.get(i);
                if (unit.hashCode() == hash) {
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

    public IUnitJson get(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (this.value == null) return null;
        int size = this.value.size();
        for (IUnitJson unit : this.value) {
            String unitName = unit.getName();
            if (name.equals(unitName)) {
                return unit;
            }
        }
        return null;
    }

    public IUnitJson get(int[] indexes) {
        List<IUnitJson> valueList = this.value;
        IUnitJson element = null;

        for (int index : indexes) {
            if (index >= 0 && index < valueList.size()) {
                element = valueList.get(index);
                if (element.getTypeValue() == IUnitJson.TypeValue.UNITS_ARRAY) {
                    valueList = element.getValueList();
                } else {
                    valueList = null;
                }
            }
        }
        return element;
    }

    public void set(int[] indexs, IUnitJson element) {
        List<IUnitJson> valueList = this.value;
        IUnitJson tempElementUnit;
        if (indexs.length == 1 && element.getTypeUnit() != this.type){
            throw new RuntimeException("Stored type does not match " + element.getTypeUnit().toString());
        }
        for (int i = 0; i < indexs.length; ++i) {
            int index = indexs[i];
            if (index >= 0 && index < valueList.size()) {
                if (i == indexs.length - 1) {
                    valueList.set(index, element);
                    return;
                }
                tempElementUnit = valueList.get(index);
                if (i == indexs.length - 2 && ((ValueUnitsJsonList) tempElementUnit.getValue()).getType() != element.getTypeUnit()) {
                    throw new RuntimeException("Stored type does not match " + element.getTypeUnit().toString());
                }
                if (tempElementUnit.getTypeValue() == IUnitJson.TypeValue.UNITS_ARRAY) {
                    valueList = tempElementUnit.getValueList();
                } else {
                    valueList = null;
                }
            } else throw new RuntimeException("Invalid index");
        }
        throw new RuntimeException("Unable to set");
    }

    private void add(int[] indexs, IUnitJson element) {
        List<IUnitJson> valueList = this.value;
        IUnitJson tempElementUnit;
        if (indexs.length == 1 && element.getTypeUnit() != this.type){
            throw new RuntimeException("Stored type does not match " + element.getTypeUnit().toString());
        }
        for (int i = 0; i < indexs.length; ++i) {
            int index = indexs[i];
            if (index >= 0 && index < valueList.size()) {
                if (i == indexs.length - 1) {
                    valueList.add(index, element);
                    return;
                }
                tempElementUnit = valueList.get(index);
                if (i == indexs.length - 2 && tempElementUnit.getTypeUnit() == element.getTypeUnit()) {
                    throw new RuntimeException("Stored type does not match " + element.getTypeUnit().toString());
                }
                if (tempElementUnit.getTypeValue() == IUnitJson.TypeValue.UNITS_ARRAY) {
                    valueList = tempElementUnit.getValueList();
                } else {
                    valueList = null;
                }
            } else throw new RuntimeException("Invalid index");
        }
        throw new RuntimeException("Unable to set");
    }

    public void delete(int[] indexes) {
        List<IUnitJson> valueList = this.value;
        IUnitJson tempElementUnit;
        for (int i = 0; i < indexes.length; ++i) {
            int index = indexes[i];
            if (index >= 0 && index < valueList.size()) {
                if (i == indexes.length - 1) {
                    valueList.remove(index);
                    return;
                }
                tempElementUnit = valueList.get(index);
                if (tempElementUnit.getTypeValue() == IUnitJson.TypeValue.UNITS_ARRAY) {
                    valueList = tempElementUnit.getValueList();
                } else {
                    valueList = null;
                }
            } else throw new RuntimeException("Invalid index");
        }
        throw new RuntimeException("Unable to delete");
    }

    private List<Integer> findingElemint(IUnitJson value, IUnitJson unit, Integer i, List<Integer> result) {
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
    public List<UnitJson> getUnitsValue() throws RangeException {
        if (this.type != TypeUnit.UNIT || this.value == null)
            throw new RuntimeException("wrong type");
        return this.value.stream()
                .map(unit -> (UnitJson) unit)
                .collect(Collectors.toList());
    }


    public List<ArrayUnitJson> getArrayValue() {
        if (this.type != TypeUnit.ARRAY_UNIT || this.value == null)
            throw new RuntimeException("wrong type");
        return this.value.stream()
                .map(unit -> (ArrayUnitJson) unit)
                .collect(Collectors.toList());
    }


    public void setValue(List<IUnitJson> value, TypeUnit type) {
        this.value = value;
        this.type = type;
    }


    @Override
    public String toString() {
        return this.toString(0);
    }

    public String toString(int d) {
        List<IUnitJson> unitList = this.value;
        StringBuilder sb = new StringBuilder();

        int newDepth = d + 1;
        char openingBracket;
        char closingBracket;

        if (this.getType() == TypeUnit.UNIT) {
            openingBracket = '{';
            closingBracket = '}';
        } else {
            openingBracket = '[';
            closingBracket = ']';
        }

        sb.append(openingBracket);
        sb.append("\r\n");
        if (unitList != null) {
            for (IUnitJson unit : unitList) {
                sb.append(tabs(newDepth));
                sb.append(unit.toString(newDepth));
                sb.append(",\r\n");
            }
            // Удаление последней запятой и переноса строки
            if (sb.length() > 3) {
                sb.setLength(sb.length() - 3);
                sb.append("\r\n");
            }
        }
        sb.append(tabs(d));
        sb.append(closingBracket);

        return sb.toString();
    }

    public boolean isEmpty() {
        return (this.value == null) || (this.type == null);
    }

    protected String tabs(int d) {
        String TAB = "  ";
        return String.join("", Collections.nCopies(d, TAB));
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


}
