package com.example.curamate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "exercise_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "exercises";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "exercise_name";

    private static final String[] DEFAULT_EXERCISES = {
            "10 Push-ups",
            "10 Squats",
            "10 Lunges",
            "15 Jumping Jacks",
            "20-second Plank"
    };

    public ExerciseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT"
                + ")";
        db.execSQL(createTable);

        // Insert default exercises only if table is empty
        for (String exercise : DEFAULT_EXERCISES) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_NAME, exercise);
            db.insert(TABLE_NAME, null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addExercise(String exerciseName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, exerciseName);
        db.insert(TABLE_NAME, null, cv);
        db.close();
    }

    public List<String> getAllExercises() {
        List<String> exercises = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NAME}, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    exercises.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return exercises;
    }

    public void resetExercises() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
    public Cursor getAllExerciseTimes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }
}
