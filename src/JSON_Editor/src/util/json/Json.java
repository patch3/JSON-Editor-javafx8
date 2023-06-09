package util.json;

import util.Convert;
import util.FileUtils;
import util.Interpreter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Json extends ValueUnitsJsonList {

    public Json(File fileJson) throws IOException {
        this(FileUtils.readFile(fileJson.getAbsolutePath()));
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


}