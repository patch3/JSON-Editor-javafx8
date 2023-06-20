package com.json;

import java.math.BigInteger;
import java.util.Arrays;

public class Interpreter {
    public static final char[] SKIP_CHARS = {'\t', '\n', '\r', ' '};

    // пропускать не нужные символы
    public static int skipChar(char[] chStr, int i) {
        while (i < chStr.length) {
            // выйти из цыкла если встретился символ отличный от пропускаемого
            if (Arrays.binarySearch(SKIP_CHARS, chStr[i]) < 0)
                break;
            ++i;
        }
        return i;
    }

    public static String string(char[] chStr, int i) throws JsonException {
        if (chStr[i] != '"') {
            throw new JsonException("error.interpreter.exp_start_char_str", i);
        }
        StringBuilder strBuilder = new StringBuilder();

        for (++i; i < chStr.length; i++) {
            char ch = chStr[i];
            if (ch == '\\') {
                if (i < chStr.length - 1) {
                    char nch = chStr[i + 1];
                    if (nch == '"' || nch == 'n' || nch == 't') {
                        strBuilder.append(getEscapedCharacter(nch));
                        i++;
                        continue;
                    }
                }
            } else if (ch == '"') {
                return strBuilder.toString();
            }

            strBuilder.append(ch);
        }

        throw new JsonException("error.interpreter.exp_end_str");
    }

    private static char getEscapedCharacter(char ch) {
        switch (ch) {
            case '"':
                return '"';
            case 'n':
                return '\n';
            case 't':
                return '\t';
            default:
                return ch;
        }
    }

    public static String numberStr(char[] chStr, int i) throws JsonException {
        if (chStr.length < 1) {
            return "";
        }
        if (!(Character.isDigit(chStr[i]) || chStr[i] == '-')) {
            throw new JsonException("error.interpreter.exp_start_num", i);
        }

        StringBuilder numBuilder = new StringBuilder();
        boolean hasDecimalPoint = false;
        boolean hasDigit = false;
        boolean isNegative = false;


        while (i < chStr.length) {
            char c = chStr[i];

            if (Character.isDigit(c)) {
                numBuilder.append(c);
                hasDigit = true;
            } else if (hasDigit && c == '.' && !hasDecimalPoint) {
                if (Character.isDigit(chStr[i + 1])) {
                    throw new JsonException("error.interpreter.exp_num_after_dot", i);
                }
                numBuilder.append(c);
                hasDecimalPoint = true;
            } else if (c == '-' && !hasDigit && !hasDecimalPoint && !isNegative) {
                numBuilder.append(c);
                isNegative = true;
            } else {
                break;
            }
            i++;
        }
        if (!hasDigit) {
            throw new JsonException("erroe.interpreter.exp_num", i);
        }
        return numBuilder.toString();
    }


    public static Number number(char[] chStr, int i) throws JsonException {
        if (!(Character.isDigit(chStr[i]) || (chStr[i] == '-'))) {
            throw new JsonException("error.interpreter.exp_start_num", i);
        }

        StringBuilder numBuilder = new StringBuilder();
        boolean hasDecimalPoint = false;
        boolean hasDigit = false;
        boolean isNegative = false;


        while (i < chStr.length) {
            char c = chStr[i];

            if (Character.isDigit(c)) {
                numBuilder.append(c);
                hasDigit = true;
            } else if (c == '.' && !hasDecimalPoint) {
                numBuilder.append(c);
                hasDecimalPoint = true;
            } else if (c == '-' && !hasDigit && !hasDecimalPoint && !isNegative) {
                isNegative = true;
            } else {
                break;
            }
            i++;
        }

        if (!hasDigit) {
            throw new JsonException("error.interpreter.exp_num", i);
        }

        // Разбираем целую часть числа
        String integerPartStr = numBuilder.toString();
        if (hasDecimalPoint) {
            int decimalPointIndex = integerPartStr.indexOf('.');
            if (decimalPointIndex > 0) {
                integerPartStr = integerPartStr.substring(0, decimalPointIndex);
            }
        }
        long integerPart;
        // Проверяем на переполнение
        if (integerPartStr.length() > 10) {
            return new BigInteger(integerPartStr);
        } else {
            integerPart = Long.parseLong(integerPartStr);
            if (integerPart < 0) {
                return new BigInteger(numBuilder.toString());
            }
        }

        // Разбираем дробную часть числа
        double fractionalPart = 0;
        if (hasDecimalPoint) {
            String fractionalPartStr = numBuilder.substring(integerPartStr.length() + 1);
            if (!fractionalPartStr.isEmpty()) {
                fractionalPart = Double.parseDouble("0." + fractionalPartStr);
            }
        }

        // Считаем итоговое значение
        double result = integerPart + fractionalPart;
        if (isNegative) {
            result = -result;
        }

        return result;
    }

}
