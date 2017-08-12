package com.example.yakir.gradeavg;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.yakir.gradeavg.data.GradeContract;
import com.example.yakir.gradeavg.data.GradesDbHelper;


public class MainActivity extends AppCompatActivity {

    private GradesListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private EditText mNewNameEditText;
    private EditText mNewGradeEditText;
    private EditText mNewCreditEditText;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView gradesRecyclerView;

        // Set local attributes to corresponding views
        gradesRecyclerView = (RecyclerView) this.findViewById(R.id.all_grades_list_view);
        mNewNameEditText = (EditText) this.findViewById(R.id.course_name_edit_text);
        mNewGradeEditText = (EditText) this.findViewById(R.id.grade_edit_text);
        mNewCreditEditText = (EditText) this.findViewById(R.id.credits_edit_text);

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        gradesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData(gradesRecyclerView);
    }

    private void initData(RecyclerView gradesRecyclerView) {
        // Create a DB helper (this will create the DB if run for the first time)
        GradesDbHelper dbHelper = new GradesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = getAllGrades();

        // Create an adapter for that cursor to display the data
        mAdapter = new GradesListAdapter(this, cursor);

        // Link the adapter to the RecyclerView
        gradesRecyclerView.setAdapter(mAdapter);

        addRemoveOnSwipe(gradesRecyclerView);
        updateAvg();
    }

    private void addRemoveOnSwipe(RecyclerView gradesRecyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //get the id of the item being swiped
                long id = (long) viewHolder.itemView.getTag();
                removeGrade(id);
                mAdapter.swapCursor(getAllGrades());
            }
        }).attachToRecyclerView(gradesRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action, menu);
        return true;
    }

    public void addToGrades(View view) {
        if (mNewNameEditText.getText().length() == 0 ||
                mNewGradeEditText.getText().length() == 0 ||
                mNewCreditEditText.getText().length() == 0) {
            return;
        }

        int grade = 100;
        double credit = 5;
        try {
            grade = Integer.parseInt(mNewGradeEditText.getText().toString());
            credit = Double.parseDouble(mNewCreditEditText.getText().toString());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Failed to parse numbers text to number: " + ex.getMessage());
        }

        // Add grade info to mDb
        addNewGrade(mNewNameEditText.getText().toString(), grade, credit);

        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllGrades());

        //clear UI text fields
        mNewGradeEditText.clearFocus();
        mNewNameEditText.getText().clear();
        mNewCreditEditText.getText().clear();
        mNewGradeEditText.getText().clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                resetGrades();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Cursor getAllGrades() {
        return mDb.query(
                GradeContract.GradeEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private long addNewGrade(String name, int grade, double credit) {
        ContentValues cv = new ContentValues();
        cv.put(GradeContract.GradeEntry.COLUMN_COURSE_NAME, name);
        cv.put(GradeContract.GradeEntry.COLUMN_GRADE, grade);
        cv.put(GradeContract.GradeEntry.COLUMN_CREDITS, credit);
        long howMany =  mDb.insert(GradeContract.GradeEntry.TABLE_NAME, null, cv);
        updateAvg();
        return howMany;
    }

    private void updateAvg() {
        Cursor grades = getAllGrades();
        int totalGrade = 0;
        double totalCredit = 0;
        double currentCredit;
        while (grades.moveToNext()) {
            currentCredit = grades.getDouble(grades.getColumnIndex(GradeContract.GradeEntry.COLUMN_CREDITS));
            totalGrade += grades.getInt(grades.getColumnIndex(GradeContract.GradeEntry.COLUMN_GRADE)) * currentCredit;
            totalCredit += currentCredit;
        }
        double avgGrade = totalCredit != 0 ? (double)totalGrade / totalCredit : 0;
        TextView viewGrade = (TextView) findViewById(R.id.total_avg_text);
        viewGrade.setText(getString(R.string.avarage) + " " + avgGrade);
        TextView viewCredit = (TextView) findViewById(R.id.total_credit_text);
        viewCredit.setText(getString(R.string.credit_point) +" " + totalCredit);
    }

    private boolean removeGrade(long id) {
        boolean success = mDb.delete(GradeContract.GradeEntry.TABLE_NAME, GradeContract.GradeEntry._ID + "=" + id, null) > 0;
        updateAvg();
        return success;
    }

    private void resetGrades() {
        mDb.delete(GradeContract.GradeEntry.TABLE_NAME, null, null);
        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllGrades());
        Toast.makeText(getApplicationContext(), getString(R.string.toast_reset), Toast.LENGTH_SHORT).show();
        updateAvg();
    }
}
