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
            if (chars[i] == '"') {
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
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
