package util;

import jdk.nashorn.internal.runtime.regexp.joni.exception.SyntaxException;
import util.json.JsonException;

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

    public static String string(char[] chStr, int i) {
        if (chStr[i] != '"') {
            throw new JsonException("EXPECTED_START_CHAR", i);
        }
        ++i;
        StringBuilder strBuilder = new StringBuilder();
        while (i < chStr.length) {
            char c = chStr[i];
            if (c == '\\' && i < chStr.length - 1 && chStr[i + 1] == '"') {
                // Если встретился символ обратного слеша перед кавычкой, то добавляем в строку кавычку без экранирования
                strBuilder.append('"');
                i++;
            } else if (c == '"') {
                // Если встретилась кавычка, то выходим из цикла
                return strBuilder.toString();
            } else {
                strBuilder.append(c);
            }
            i++;
        }
        throw new SyntaxException("End of String not found of index " + i);
    }

    public static String numberStr(char[] chStr, int i){
        if (!(Character.isDigit(chStr[i]) || chStr[i] == '-')) {
            throw new SyntaxException("Expected start of number at index '" + i + "'");
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
                if (Character.isDigit(chStr[i+1])){
                    throw new JsonException("A number was expected after the dot", i);
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
            throw new SyntaxException("Expected number at index '" + i + "'");
        }
        return numBuilder.toString();
    }

    public static Number number(char[] chStr, int i) {
        if (!(Character.isDigit(chStr[i]) || (chStr[i] == '-'))) {
            throw new SyntaxException("Expected start of number at index '" + i + "'");
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
            throw new SyntaxException("Expected number at index '" + i + "'");
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
