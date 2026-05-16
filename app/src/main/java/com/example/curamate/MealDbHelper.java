package com.example.curamate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MealDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "meals.db";
    private static final int DATABASE_VERSION = 1;

    // Table for meal times
    private static final String TABLE_TIME = "meal_times";
    private static final String COL_TIME_ID = "id";
    private static final String COL_MEAL = "meal";
    private static final String COL_HOUR = "hour";
    private static final String COL_MINUTE = "minute";

    // Table for meal menus
    private static final String TABLE_MENU = "meal_menu";
    private static final String COL_MENU_ID = "id";
    private static final String COL_MENU_MEAL = "meal";
    private static final String COL_MENU_DAY = "day";
    private static final String COL_MENU_TEXT = "menu";

    public MealDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table for times
        String createTimeTable = "CREATE TABLE " + TABLE_TIME + " (" +
                COL_TIME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MEAL + " TEXT UNIQUE, " +
                COL_HOUR + " INTEGER, " +
                COL_MINUTE + " INTEGER)";
        db.execSQL(createTimeTable);

        // Table for menus
        String createMenuTable = "CREATE TABLE " + TABLE_MENU + " (" +
                COL_MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MENU_MEAL + " TEXT, " +
                COL_MENU_DAY + " TEXT, " +
                COL_MENU_TEXT + " TEXT, " +
                "UNIQUE(" + COL_MENU_MEAL + "," + COL_MENU_DAY + "))";
        db.execSQL(createMenuTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        onCreate(db);
    }

    // ---------------- TIME FUNCTIONS ----------------
    public void saveMealTime(String meal, int hour, int minute) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MEAL, meal);
        values.put(COL_HOUR, hour);
        values.put(COL_MINUTE, minute);

        int rows = db.update(TABLE_TIME, values, COL_MEAL + "=?", new String[]{meal});
        if (rows == 0) {
            db.insert(TABLE_TIME, null, values);
        }
        db.close();
    }

    public int[] getMealTime(String meal) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIME, new String[]{COL_HOUR, COL_MINUTE},
                COL_MEAL + "=?", new String[]{meal},
                null, null, null);

        int[] time = {-1, -1};
        if (cursor != null && cursor.moveToFirst()) {
            time[0] = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HOUR));
            time[1] = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MINUTE));
            cursor.close();
        }
        db.close();
        return time;
    }

    // 🔥 For BootReceiver: fetch all meal times
    public Cursor getAllMealTimes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TIME,
                new String[]{COL_MEAL, COL_HOUR, COL_MINUTE},
                null, null, null, null, null);
    }

    // ---------------- MENU FUNCTIONS ----------------
    public void saveMenu(String meal, String day, String menuText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MENU_MEAL, meal);
        values.put(COL_MENU_DAY, day);
        values.put(COL_MENU_TEXT, menuText);

        int rows = db.update(TABLE_MENU, values,
                COL_MENU_MEAL + "=? AND " + COL_MENU_DAY + "=?",
                new String[]{meal, day});

        if (rows == 0) {
            db.insert(TABLE_MENU, null, values);
        }
        db.close();
    }

    public String getMenu(String meal, String day) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MENU, new String[]{COL_MENU_TEXT},
                COL_MENU_MEAL + "=? AND " + COL_MENU_DAY + "=?",
                new String[]{meal, day}, null, null, null);

        String menu = "No menu set for today.";
        if (cursor != null && cursor.moveToFirst()) {
            menu = cursor.getString(cursor.getColumnIndexOrThrow(COL_MENU_TEXT));
            cursor.close();
        }
        db.close();
        return menu;
    }
}
