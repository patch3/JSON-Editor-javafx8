package com.editor.util.json;

import com.editor.util.Convert;
import com.editor.util.FileUtils;
import com.editor.util.Interpreter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Json extends ValueUnitsJsonList {

    public Json(){
        super();
    }

    public Json(File fileJson) throws IOException {
        this(FileUtils.readFile(fileJson.getAbsolutePath()));
    }

    public Json(String str) throws JsonException {
        super(str.toCharArray(), Interpreter.skipChar(str.toCharArray(), 0));
    }

    public Json(List<? extends IUnitJson> obj, TypeUnit type) {
        super(obj, type);
    }


    public int[] indexOf(IUnitJson obj) {
        return Convert.toIntArray(
                super.indexOf(obj, new ArrayList<>())
        );
    }



    /** разрывает ссылку и выделает память под unit сохраняя его тип*/
    public static IUnitJson newUnit(IUnitJson obj){
        if (obj == null) return null;
        if (obj instanceof UnitJson) {
            return new UnitJson(obj);
        } else {
            return new ArrayUnitJson(obj);
        }
    }
}