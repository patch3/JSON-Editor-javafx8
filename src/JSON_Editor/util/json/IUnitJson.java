package util.json;

import java.util.List;

public interface IUnitJson {
    Object getValue();

    List<IUnitJson> getValueList();

    TypeUnit getTypeUnit();

    TypeValue getTypeValue();

    String getName();


    void setValue(IUnitJson value);

    void setValue(Object value, TypeValue type);

    void setName(Object name);

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

    String toString(int d);

    enum TypeValue {
        NUMBER,
        STRING,
        UNITS_ARRAY
    }
}
