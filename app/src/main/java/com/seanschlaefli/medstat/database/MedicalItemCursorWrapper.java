package com.seanschlaefli.medstat.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.seanschlaefli.medstat.MedicalItem;
import com.seanschlaefli.medstat.database.MedStatDbSchema.MedicalItemTable;

import org.joda.time.DateTime;

import java.util.UUID;

public class MedicalItemCursorWrapper extends CursorWrapper {


    public MedicalItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public MedicalItem getMedicalItem() {
        String name = getString(getColumnIndex(MedicalItemTable.Cols.NAME));
        String uuidString = getString(getColumnIndex(MedicalItemTable.Cols.UUID));
        double value = getDouble(getColumnIndex(MedicalItemTable.Cols.VALUE));
        // set the units by querying the db using the name
        String units = "";
        long dateTimeLong = getLong(getColumnIndex(MedicalItemTable.Cols.DATETIME));
        DateTime dateTime = new DateTime(dateTimeLong);
        return new MedicalItem(UUID.fromString(uuidString), name, value, units, dateTime);
    }

    public String getUnits(String name) {
        return "";
    }

}
