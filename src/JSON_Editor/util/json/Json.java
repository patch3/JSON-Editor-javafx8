package JSON_Editor.util.json;


import com.sun.istack.internal.Nullable;
import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;

import java.util.ArrayList;
import java.util.List;

public class Json {
    @Nullable
    public List<UnitJson> units;

    public Json(String str) {
        units = Interpreter(str.toCharArray());
    }

    public static List<UnitJson> Interpreter(char[] chStr) {
        if (chStr[0] != '{') {
            throw new SyntaxException("Interpretation of Unit JSON must start with character '{'");
        }
        int i = 1;
        boolean startCheckUnit = true;
        boolean postCheckName = false;
        boolean postCheckValue = false;
        boolean postCheckComma = false;
        boolean CheckValue = false;

        List<UnitJson> units = new ArrayList<>();

        UnitJson unit = new UnitJson();

        while (i < chStr.length) {
            char ch = chStr[i];
            switch (ch) {
                case '{':
                case '[':
                    if (postCheckName) {
                        postCheckName = false;
                        postCheckValue = true;
                        i = unit.unitInterpreter(chStr, i);
                        //++i;
                        break;
                    } else throw new SyntaxException("Opening symbols were not expected at index '" + i + "'");
                case '}':
                case ']':
                    if (postCheckComma || postCheckValue)
                        return units;
                    else if (startCheckUnit) {
                        return null;
                    } else throw new JsonException("Closing character was not expected at index '" + i + "'");
                case ':':
                    if (postCheckName) {
                        postCheckName = false;
                        CheckValue = true;
                        break;
                    } else throw new SyntaxException("Missing character ':' at index '" + i + "'");
                case ',':
                    if (postCheckValue) {
                        postCheckValue = false;
                        postCheckComma = true;
                        break;
                    } else throw new SyntaxException("Missing character ',' at index '" + i + "'");
                case '"':
                    if (startCheckUnit) {
                        startCheckUnit = false;
                        postCheckName = true;
                        i = unit.nameInterpreter(chStr, i);
                        //++i;
                        break;
                    } else if (postCheckComma) {
                        postCheckComma = false;
                        postCheckName = true;
                        i = unit.nameInterpreter(chStr, i);
                        //++i;
                        break;
                    } else if (CheckValue) {
                        CheckValue = false;
                        postCheckValue = true;
                        i = unit.valueInterpreter(chStr, i);
                        units.add(unit);
                        unit = new UnitJson();
                        //++i;
                        break;
                    } else throw new SyntaxException("Not expected '\"' of index " + i);
                default:
                    if (Character.isDigit(ch) || ch == '-' && CheckValue) {
                        CheckValue = false;
                        postCheckValue = true;
                        i = unit.valueInterpreter(chStr, i);
                        units.add(unit);
                        unit = new UnitJson();
                        //++i;
                        break;
                    }
            }
            ++i;
        }
        return units;
    }
}