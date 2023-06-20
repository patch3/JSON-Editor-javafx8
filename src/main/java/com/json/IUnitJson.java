package com.json;

import java.util.List;

public interface IUnitJson {
    Object getValue();

    void setValue(IUnitJson value);

    List<IUnitJson> getValueList();

    TypeUnit getTypeUnit();

    TypeValue getTypeValue();

    String getName();

    void setName(Object name);

    void setValue(Object value, TypeValue type) throws JsonException;

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
