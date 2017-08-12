package com.example.yakir.gradeavg.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yakir on 12-Aug-17.
 */

public class GradesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "grade.db";
    private static final int DATABASE_VERSION = 1;

    public GradesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold grades data
        final String SQL_CREATE_GRADES_TABLE = "CREATE TABLE " + GradeContract.GradeEntry.TABLE_NAME + " (" +
                GradeContract.GradeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GradeContract.GradeEntry.COLUMN_COURSE_NAME + " TEXT NOT NULL, " +
                GradeContract.GradeEntry.COLUMN_GRADE + " INTEGER NOT NULL, " +
                GradeContract.GradeEntry.COLUMN_CREDITS + " REAL NOT NULL" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_GRADES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GradeContract.GradeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
