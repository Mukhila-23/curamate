package com.example.curamate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SleepDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sleepTracker.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_SLEEP = "sleepHistory";
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";       // e.g., 2025-09-23
    private static final String COL_TYPE = "type";       // "Sleep Time" or "Wake Time"
    private static final String COL_TIME = "time";       // e.g., 23:30 or "System Alarm Set"
    private static final String COL_DURATION = "duration"; // e.g., "7h 30m"

    public SleepDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_SLEEP + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DATE + " TEXT, "
                + COL_TYPE + " TEXT, "
                + COL_TIME + " TEXT, "
                + COL_DURATION + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLEEP);
        onCreate(db);
    }

    // Insert sleep or wake record
    public void addSleepRecord(String type, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        String today = String.format(Locale.getDefault(), "%1$tY-%1$tm-%1$td", Calendar.getInstance());

        // Insert the record
        ContentValues values = new ContentValues();
        values.put(COL_DATE, today);
        values.put(COL_TYPE, type);
        values.put(COL_TIME, time);

        db.insert(TABLE_SLEEP, null, values);

        // If wake record has a valid time (hh:mm), calculate duration
        if ("Wake Time".equalsIgnoreCase(type) && time.contains(":")) {
            calculateDuration(db, today, time);
        }

        // Keep only last 7 days of data
        db.execSQL("DELETE FROM " + TABLE_SLEEP + " WHERE " + COL_DATE +
                " NOT IN (SELECT DISTINCT " + COL_DATE + " FROM " + TABLE_SLEEP +
                " ORDER BY " + COL_DATE + " DESC LIMIT 7)");

        db.close();
    }

    // Calculate duration when a wake time is recorded
    private void calculateDuration(SQLiteDatabase db, String today, String wakeTime) {
        // Find last sleep entry today
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_ID + ", " + COL_TIME +
                        " FROM " + TABLE_SLEEP +
                        " WHERE " + COL_DATE + "=? AND " + COL_TYPE + "='Sleep Time' " +
                        " ORDER BY " + COL_ID + " DESC LIMIT 1",
                new String[]{today});

        if (cursor.moveToFirst()) {
            int sleepId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            String sleepTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));

            String duration = computeDuration(sleepTime, wakeTime);

            // Update wake entry with duration
            ContentValues values = new ContentValues();
            values.put(COL_DURATION, duration);
            db.update(TABLE_SLEEP, values, COL_ID + "=(SELECT MAX(" + COL_ID + ") FROM " + TABLE_SLEEP + ")", null);

            // Also update the corresponding sleep entry
            ContentValues sleepValues = new ContentValues();
            sleepValues.put(COL_DURATION, duration);
            db.update(TABLE_SLEEP, sleepValues, COL_ID + "=" + sleepId, null);
        }
        cursor.close();
    }

    // Compute duration between sleep and wake times
    private String computeDuration(String sleepTime, String wakeTime) {
        try {
            String[] s = sleepTime.split(":");
            int sh = Integer.parseInt(s[0]);
            int sm = Integer.parseInt(s[1]);

            String[] w = wakeTime.split(":");
            int wh = Integer.parseInt(w[0]);
            int wm = Integer.parseInt(w[1]);

            Calendar sleepCal = Calendar.getInstance();
            sleepCal.set(Calendar.HOUR_OF_DAY, sh);
            sleepCal.set(Calendar.MINUTE, sm);

            Calendar wakeCal = Calendar.getInstance();
            wakeCal.set(Calendar.HOUR_OF_DAY, wh);
            wakeCal.set(Calendar.MINUTE, wm);

            if (wakeCal.before(sleepCal)) {
                wakeCal.add(Calendar.DATE, 1); // next day
            }

            long diffMillis = wakeCal.getTimeInMillis() - sleepCal.getTimeInMillis();
            int hours = (int) (diffMillis / (1000 * 60 * 60));
            int minutes = (int) ((diffMillis / (1000 * 60)) % 60);

            return hours + "h " + minutes + "m";
        } catch (Exception e) {
            return "N/A";
        }
    }

    // Fetch history (last 7 days max)
    public ArrayList<String> getSleepHistory() {
        ArrayList<String> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SLEEP,
                null, null, null, null, null,
                COL_DATE + " DESC, " + COL_ID + " ASC");

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(COL_DURATION));

                String record = date + " | " + type.toUpperCase() + " at " + time;
                if (duration != null) {
                    record += " → " + duration;
                }
                history.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return history;
    }
}
