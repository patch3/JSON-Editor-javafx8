package com.editor.util;

import java.util.List;

public class Convert {
    public static int[] toIntArray(List<Integer> integerList) {
        if (integerList == null) return new int[0];
        int[] intArray = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            intArray[i] = integerList.get(i);
        }
        return intArray;
    }

    /**
     * Подсчитать кочиство потенциального размера строки.
     * Командный символ '"' засчитывается как 2 символа.
     * (подразумевается что в строке перед кавычкой ставится символ '\')
     * @param string строка для подчета
     * @return размер строки для записи
     */
    public static int countRecord(String string) {
        char[] chars = string.toCharArray();
        int count = 0;
        for (int i = 0; i < chars.length; ++i, ++count) {
            char ch = chars[i];
            if (ch == '"' || ch == '\n' ||  ch == '\t') {
                ++count;
            }
        }
        return count;
    }


    public static String toRecord(String string) {
        char[] chars = string.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char ch : chars){
            if (ch == '"'){
                sb.append("\\\"");
            } else if (ch == '\n'){
                sb.append("\\\n");
            } else if (ch == '\t'){
                sb.append("\\\t");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
