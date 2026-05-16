package com.example.curamate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MedicineDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicines.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MEDICINE = "medicine";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIMES = "times"; // comma-separated times
    public static final String COLUMN_DAYS = "days";   // comma-separated days

    public MedicineDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_MEDICINE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_TIMES + " TEXT, "
                + COLUMN_DAYS + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINE);
        onCreate(db);
    }

    // Add a medicine
    public void addMedicine(String name, List<String> times, List<String> days) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_TIMES, String.join(",", times));
        values.put(COLUMN_DAYS, String.join(",", days));
        db.insert(TABLE_MEDICINE, null, values);
        db.close();
    }

    // Delete all medicines
    public void deleteAllMedicines() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICINE, null, null);
        db.close();
    }

    // Get all medicines as display strings
    public List<String> getAllMedicines() {
        List<String> medicineList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDICINE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String times = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMES));
                String days = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAYS));

                String display = name + " → [" + times + "]";
                if (!days.isEmpty()) {
                    display += " on [" + days + "]";
                } else {
                    display += " (every day)";
                }
                medicineList.add(display);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return medicineList;
    }

    // Get structured list for BootReceiver
    public List<MedicineModel> getAllMedicineModels() {
        List<MedicineModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDICINE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String times = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMES));
                String days = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAYS));

                MedicineModel model = new MedicineModel();
                model.name = name;
                model.times = times.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(times.split(",")));
                model.days = days.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(days.split(",")));

                list.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}
