package com.seanschlaefli.medstat;


import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.UUID;

/**
 * MedicalValue.java
 * models a statistic tracked by the application
 * generic class because the data may be an integer
 * or double
 */
public class MedicalItem implements Comparable {

    private UUID mId;
    private String mName;
    private double mValue;
    private String mUnits;
    private DateTime mDateTime;


    public MedicalItem() {
        this(UUID.randomUUID());
    }

    public MedicalItem(UUID id) {
        mId = id;
        mName = "";
        mValue = 0.0;
        mUnits = "";
        mDateTime = new DateTime();

    }

    public MedicalItem(String name, double value, String units, DateTime dateTime) {
        mId = UUID.randomUUID();
        mName = name;
        mValue = value;
        mUnits = units;
        mDateTime = dateTime;
    }

    public MedicalItem(UUID id, String name, double value, String units, DateTime dateTime) {
        mId = id;
        mName = name;
        mValue = value;
        mUnits = units;
        mDateTime = dateTime;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public double getValue() {
        return mValue;
    }

    public void setValue(double value) {
        mValue = value;
    }

    public String getUnits() {
        return mUnits;
    }

    public void setUnits(String units) {
        mUnits = units;
    }

    public DateTime getDateTime() { return mDateTime; }

    public void setDateTime(DateTime DateTime) { mDateTime = DateTime; }

    public String toString() {
        return mId + " " + mName + " " + mValue + " " + mDateTime.toString();
    }

    public Date getDate() {
        return new Date(mDateTime.getMillis());
    }

    @Override
    public int compareTo(@NonNull Object o) {
        MedicalItem item = (MedicalItem) o;
        double itemValue = item.getValue();

        if (mValue == itemValue) {
            return 0;
        } else if (mValue > itemValue) {
            return 1;
        } else {
            return -1;
        }
    }
}
