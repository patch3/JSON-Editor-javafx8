package JSON_Editor.util.json;

import JSON_Editor.util.Intepreter;
import com.sun.istack.internal.Nullable;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;

import java.util.List;
import java.util.Objects;

public class UnitJson {

    public String name;
    @Nullable
    private Object value;
    @Nullable
    public String comment;
    private TypeValue typeValue;
    private int index;
    public UnitJson() {}

    public UnitJson(String name) {
        this.name = name;
    }


    public UnitJson(String name, Object value) {
        this.name = name;
        this.value = value;
        this.comment = null;
    }

    public UnitJson(String name, Object value, String comment) {
        this.name = name;
        this.value = value;
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitJson unitJSON = (UnitJson) o;
        return Objects.equals(name, unitJSON.name) && Objects.equals(value, unitJSON.value) && Objects.equals(comment, unitJSON.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, comment);
    }

    public int unitInterpreter(char[] chStr, int i) {
        /*if (chStr[i] != '{'){
            throw new SyntaxException("Interpretation of Unit JSON must start with character '{'");
        }
        ++i;
        boolean startCheckUnit = true;
        boolean postCheckName  = false;
        boolean postCheckValue = false;
        boolean postCheckСomma = false;
        value = new ArrayList<UnitJSON>();

        while (i < chStr.length){
            switch (chStr[i]){
                case '{': case '[':
                    if (postCheckName) {
                        postCheckName = false;
                        postCheckValue = true;
                        i = unitInterpreter(chStr, i);
                    } else throw new SyntaxException("Opening symbols were not expected at index '"+i+"'");
                case '}': case ']':
                    if (postCheckСomma || postCheckValue)
                        return i;
                    else throw new SyntaxException("Closing character was not expected at index '"+i+"'");
                case ':':
                    if (postCheckName){
                        postCheckName = false;
                        postCheckValue = true;
                        i = valueInterpreter(chStr, i);
                    } else throw new SyntaxException("Missing character ':' at index '"+i+"'");
                case ',':
                    if (postCheckValue){
                        postCheckValue = false;
                        postCheckСomma = true;
                    } else throw new SyntaxException("Missing character ',' at index '"+i+"'");
                case '"':
                    if (startCheckUnit){
                        this.name = Intepreter.string(chStr, i);
                    } else if (postCheckValue){

                    } else throw new SyntaxException("");
            }
            ++i;
        }*/
        return i;
    }


    public int valueInterpreter(char[] chStr, int i) {
        char ch = chStr[i];
        if (ch == '"') {
            this.value = Intepreter.string(chStr, i);
            this.typeValue = TypeValue.STRING;
            return ((String)this.value).length() + 1 + i;
        } else if (Character.isDigit(ch) || ch == '-') {
            this.value = Intepreter.numberStr(chStr, i);
            this.typeValue = TypeValue.NUMBER;
            return ((String)this.value).length() + i;
        }
        throw new SyntaxException("Expected value of the element by index " + i);
    }

    public int nameInterpreter(char[] chStr, int i) {
        this.name = Intepreter.string(chStr, i);
        return this.name.length() + 1 + i;
    }

    public Object getValue() {
        return this.value;
    }

    public String getStringValue() {
        if (this.typeValue != TypeValue.STRING){
            throw new JsonException("Value is not of type STRING");
        }
        return String.format("\"%s\"", (String)this.value);
    }
    public String getNumStringValue() {
        if (this.typeValue != TypeValue.NUMBER){
            throw new JsonException("Value is not of type NUMBER");
        }
        return (String)this.value;
    }
    public List<UnitJson> getUnitList() {
        if (this.typeValue != TypeValue.UNITS_ARRAY){
            throw new JsonException("Value is not of type UNITS_ARRAY");
        }
        return (List<UnitJson>)this.value;
    }

    public TypeValue getTypeValue() {
        return typeValue;
    }

    public int getIndex() { return index; }

    public void setValue(Object value, TypeValue type) {
        this.value = value;
        this.typeValue = type;
    }

    public enum TypeValue {
        NUMBER,
        STRING,
        ARRAY,
        UNITS_ARRAY
    }
}
