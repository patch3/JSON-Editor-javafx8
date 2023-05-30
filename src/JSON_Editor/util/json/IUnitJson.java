package util.json;

import java.util.List;

public interface IUnitJson {
    Object getValue();

    void setValue(IUnitJson value);

    List<IUnitJson> getValueList();

    TypeUnit getTypeUnit();

    TypeValue getTypeValue();

    void setValue(Object value, TypeValue type);

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

    enum TypeValue {
        NUMBER,
        STRING,
        UNITS_ARRAY
    }
}
