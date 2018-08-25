package com.seanschlaefli.medstat.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.seanschlaefli.medstat.database.MedStatDbSchema.MedicalUnitsTable;

public class MedicalUnitCursorWrapper extends CursorWrapper {

    public MedicalUnitCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getUnits() {
        return getString(getColumnIndex(MedicalUnitsTable.Cols.UNITS));
    }
}
