package util.json;

import util.Convert;
import util.Interpreter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Json extends ValueUnitsJsonList {

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


    public int[] indexOf(IUnitJson obj) {
        return Convert.toIntArray(
                super.indexOf(obj,  new ArrayList<>())
        );
    }

    public IUnitJson get(int[] indexes) {
        List<IUnitJson> valueList = super.value;
        IUnitJson element = null;

        for (int index : indexes) {
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