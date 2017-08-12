package com.example.yakir.gradeavg.data;

import android.provider.BaseColumns;

/**
 * Created by Yakir on 12-Aug-17.
 */

public class GradeContract {

    public static final class GradeEntry implements BaseColumns {
        public static final String TABLE_NAME = "grades";
        public static final String COLUMN_COURSE_NAME = "CourseName";
        public static final String COLUMN_GRADE = "grade";
        public static final String COLUMN_CREDITS = "credits";
    }
}
