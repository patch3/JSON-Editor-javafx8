package src.util;

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
}
