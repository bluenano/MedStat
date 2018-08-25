package com.seanschlaefli.medstat;

import java.util.Date;
import java.util.List;

public class MathStat {

    private MathStat() {}


    public static <T extends Comparable<? super T>> T findMax(List<T> items) {
        if (items.size() == 0) {
            return null;
        }

        T max = items.get(0);
        for (T item: items) {
            if (item.compareTo(max) >= 0) {
                max = item;
            }
        }
        return max;
    }

    public static <T extends Comparable<? super T>> T findMin(List<T> items) {
        if (items.size() == 0) {
            return null;
        }

        T min = items.get(0);
        for (T item: items) {
            if (item.compareTo(min) <= 0) {
                min = item;
            }
        }
        return min;
    }


}
