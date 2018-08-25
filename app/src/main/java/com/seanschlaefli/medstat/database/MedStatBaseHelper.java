package com.seanschlaefli.medstat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.seanschlaefli.medstat.database.MedStatDbSchema.MedicalItemTable;
import com.seanschlaefli.medstat.database.MedStatDbSchema.MedicalUnitsTable;

import java.util.HashMap;
import java.util.Map;

public class MedStatBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "medicalItemBase.db";

    public MedStatBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MedicalItemTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                MedicalItemTable.Cols.UUID + ", " +
                MedicalItemTable.Cols.NAME + ", " +
                MedicalItemTable.Cols.VALUE + " float, " +
                MedicalItemTable.Cols.DATETIME + ")"
        );

        db.execSQL("create table " + MedicalUnitsTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                MedicalUnitsTable.Cols.NAME + ", " +
                MedicalUnitsTable.Cols.UNITS + ")"
        );

        Map<String, String> defaults = new HashMap<>();
        defaults.put("Temperature", "\u00b0" + "F");
        defaults.put("Blood Pressure", "mmHg");
        defaults.put("Glucose", "mg/dL");
        defaults.put("Pulse", "bpm");
        defaults.put("Insulin Taken", "units");
        for (Map.Entry<String, String> entry: defaults.entrySet()) {
            db.execSQL("INSERT INTO " + MedicalUnitsTable.NAME + " (" +
                    MedicalUnitsTable.Cols.NAME + ", " + MedicalUnitsTable.Cols.UNITS + ") " +
                    "VALUES " +
                    "('" + entry.getKey() + "', '" + entry.getValue() + "'" +
                    ")"
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
