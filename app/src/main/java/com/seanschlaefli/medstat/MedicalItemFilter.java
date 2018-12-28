package com.seanschlaefli.medstat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MedicalItemFilter {

    private MedicalItemFilter() {}

    // return medical items that were recorded at a time greater than the
    // time given by applying a filter to a base time
    public static List<MedicalItem> filterByTime(List<MedicalItem> items,
                                                 long baseTimeInMS,
                                                 long filterInMS) {
        List<MedicalItem> filteredItems = new ArrayList<>();
        long lowerBound = baseTimeInMS - filterInMS;
        for (MedicalItem item: items) {
            long recordTime = item.getDateTime().getMillis();
            if (recordTime > lowerBound) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

}
