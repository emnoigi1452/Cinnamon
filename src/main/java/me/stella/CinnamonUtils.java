package me.stella;

import java.util.Collection;

public class CinnamonUtils {

    public static final int[] tileEntitySize = new int[] { 27, 54 };

    public static int getMaxInSet(Collection<? extends Integer> intSet) {
        int max = Integer.MIN_VALUE;
        for(Integer i: intSet)
            max = Math.max(max, i);
        return max;
    }

    public static int getTileEntitySize(int maxSlot) {
        for(Integer size: tileEntitySize) {
            if(maxSlot < size)
                return size;
        }
        return 54;
    }

}
