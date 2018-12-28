package com.seanschlaefli.medstat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.seanschlaefli.medstat.database.MedStatBaseHelper;
import com.seanschlaefli.medstat.database.MedicalItemCursorWrapper;
import com.seanschlaefli.medstat.database.MedStatDbSchema.MedicalItemTable;
import com.seanschlaefli.medstat.database.MedStatDbSchema.MedicalUnitsTable;
import com.seanschlaefli.medstat.database.MedicalUnitCursorWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MedicalItemBank {

    public final String TAG = MedicalItemBank.this.getClass().getSimpleName();

    private static MedicalItemBank sMedicalItemBank;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private MedicalItemBank(Context context) {
        mContext = context;
        mDatabase =  new MedStatBaseHelper(context).getWritableDatabase();
    }

    public static MedicalItemBank get(Context context) {
        if (sMedicalItemBank == null) {
            sMedicalItemBank = new MedicalItemBank(context);
        }
        return sMedicalItemBank;
    }

    public MedicalItem getMedicalItem(UUID id) {
        MedicalItemCursorWrapper wrapper = queryMedicalItems(
                MedicalItemTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (wrapper.getCount() == 0) {
                return null;
            }
            wrapper.moveToFirst();
            MedicalItem item = wrapper.getMedicalItem();
            String units = getUnits(item.getName());
            item.setUnits(units);
            return item;
        } finally {
            wrapper.close();
        }
    }


    public List<MedicalItem> getMedicalItems() {
        MedicalItemCursorWrapper cursor = queryMedicalItems(null, null);
        List<MedicalItem> items = getItemsFromCursor(cursor);
        cursor.close();
        return items;
    }


    public List<String> getNames() {
        MedicalUnitCursorWrapper wrapper = queryUnits(null, null);
        List<String> names = new ArrayList<>();
        try {
            if (wrapper.getCount() == 0) {
                return names;
            }
            wrapper.moveToFirst();
            while (!wrapper.isAfterLast()) {
                names.add(wrapper.getName());
                wrapper.moveToNext();
            }
        } finally {
            wrapper.close();
        }
        return names;
    }

    public String getUnits(String name) {
        MedicalUnitCursorWrapper wrapper = queryUnits(
                MedicalUnitsTable.Cols.NAME + " = ?",
                new String[] { name }
        );

        try {
            if (wrapper.getCount() == 0) {
                return "";
            }
            wrapper.moveToFirst();
            String units = wrapper.getUnits();
            return units;
        } finally {
            wrapper.close();
        }
    }

    public List<MedicalItem> getMedicalItemsByName(String name) {
        MedicalItemCursorWrapper cursor = queryMedicalItems(
                MedicalItemTable.Cols.NAME + " = ?",
                new String[] { name }
        );
        List<MedicalItem> items = getItemsFromCursor(cursor);
        for (MedicalItem item: items) {
            String units = getUnits(item.getName());
            item.setUnits(units);
        }
        cursor.close();
        return items;

    }


    public List<MedicalItem> getMedicalItemsByName(String name, long startTime, long endTime) {
        List<MedicalItem> items = getMedicalItemsByName(name);
        List<MedicalItem> filteredItems = new ArrayList<>();
        for (MedicalItem item: items) {
            long time = item.getDateTime().getMillis();
            if (time >= startTime && time <= endTime) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    private List<MedicalItem> getItemsFromCursor(MedicalItemCursorWrapper cursor) {
        List<MedicalItem> items = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            items.add(cursor.getMedicalItem());
            cursor.moveToNext();
        }
        return items;
    }


    public void addMedicalItem(MedicalItem item) {
        ContentValues values = getContentValues(item);
        mDatabase.insert(MedicalItemTable.NAME, null, values);
    }

    public void addCustomValue(String name, String units) {
        ContentValues values = new ContentValues();
        values.put(MedicalUnitsTable.Cols.NAME, name);
        values.put(MedicalUnitsTable.Cols.UNITS, units);
        mDatabase.insert(MedicalUnitsTable.NAME, null, values);
    }

    public void updateMedicalItem(MedicalItem item) {
        String uuid = item.getId().toString();
        ContentValues values = getContentValues(item);

        mDatabase.update(MedicalItemTable.NAME,
                values,
                MedicalItemTable.Cols.UUID + " = ?",
                new String[] { uuid });
    }


    private static ContentValues getContentValues(MedicalItem item) {
        ContentValues values = new ContentValues();
        values.put(MedicalItemTable.Cols.NAME, item.getName());
        values.put(MedicalItemTable.Cols.UUID, item.getId().toString());
        values.put(MedicalItemTable.Cols.VALUE, item.getValue());
        values.put(MedicalItemTable.Cols.DATETIME, item.getDateTime().getMillis());
        return values;
    }


    private MedicalItemCursorWrapper queryMedicalItems(String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                MedicalItemTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                MedicalItemTable.Cols.DATETIME + " ASC",
                null
        );
        return new MedicalItemCursorWrapper(cursor);
    }

    private MedicalUnitCursorWrapper queryUnits(String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                MedicalUnitsTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null
        );
        return new MedicalUnitCursorWrapper(cursor);
    }

    public boolean isUniqueName(String name) {
        MedicalUnitCursorWrapper wrapper = queryUnits("name = ?",
                new String[] {name});
        Log.d(TAG, "Count is " + Integer.toString(wrapper.getCount()));
        return wrapper.getCount() == 0;
    }

}
