package com.seanschlaefli.medstat;

import java.util.List;

public class MedStatUtil {

    public static String[] stringListToArr(List<String> list) {
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

}
