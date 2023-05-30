package util.json;

import util.Convert;
import util.Interpreter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Json extends ValueUnitsJson {

    public Json(File fileJson) throws IOException {
        this(
                new String(
                        Files.readAllBytes(
                                Paths.get(
                                        fileJson.getAbsolutePath()
                                ))));
    }

    public Json(String str) {
        super(str.toCharArray(), Interpreter.skipChar(str.toCharArray(), 0));
    }

    public Json(List<? extends IUnitJson> obj, TypeUnit type) {
        super(obj, type);
    }


    public int[] indexOf(ArrayUnitJson obj, TypeUnit type) {
        return Convert.toIntArray(super.indexOf(obj, type, new ArrayList<Integer>()));
    }

    public IUnitJson get(int[] indexes) {
        List<? extends IUnitJson> valueList = this.value;
        IUnitJson element = null;

        for (int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            int size = valueList.size();
            if (index >= 0 && index < size) {
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
}