package src.util.json;

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

    public ArrayUnitJson(ArrayUnitJson obj) {
        this(obj.value, obj.typeValue);
    }


    @Override
    public String toString() {
        if (this.typeValue == TypeValue.UNITS_ARRAY) {
            return "[List]";
        } else if (this.typeValue == TypeValue.STRING) {
            return String.format("\"%s\"", this.value);
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
        if (obj instanceof Number) {
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
