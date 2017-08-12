package com.example.yakir.gradeavg;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yakir.gradeavg.data.GradeContract;

/**
 * Created by Yakir on 12-Aug-17.
 */

public class GradesListAdapter extends RecyclerView.Adapter<GradesListAdapter.GradeViewHolder> {
    private Cursor mCursor;
    private Context mContext;

    /**
     * Constructor using the context and the db cursor
     * @param context the calling context/activity
     * @param cursor the db cursor with waitlist data to display
     */
    public GradesListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public GradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.grade_list_item, parent, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GradeViewHolder holder, int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null
        String name = mCursor.getString(mCursor.getColumnIndex(GradeContract.GradeEntry.COLUMN_COURSE_NAME));
        int grade = mCursor.getInt(mCursor.getColumnIndex(GradeContract.GradeEntry.COLUMN_GRADE));
        double credits = mCursor.getDouble(mCursor.getColumnIndex(GradeContract.GradeEntry.COLUMN_CREDITS));
        long id = mCursor.getLong(mCursor.getColumnIndex(GradeContract.GradeEntry._ID));

        holder.nameTextView.setText(name);
        holder.gradeTextView.setText(String.valueOf(grade));
        holder.creditsTextView.setText(String.valueOf(credits));
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }


    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class GradeViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView gradeTextView;
        TextView creditsTextView;

        public GradeViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            gradeTextView = (TextView) itemView.findViewById(R.id.grade_text_view);
            creditsTextView = (TextView) itemView.findViewById(R.id.credits_text_view);
        }

    }
}
