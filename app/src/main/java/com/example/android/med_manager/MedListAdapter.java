package com.example.android.med_manager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.med_manager.data.MedContract.MedEntry;
import com.example.android.med_manager.sync.MedReminderIntentService;
import com.example.android.med_manager.sync.ReminderTasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
//import com.example.android.med_manager.sync.ReminderUtilities;

/**
 * Created by SOLARIN O. OLUBAYODE on 04/04/18.
 */

public class MedListAdapter extends RecyclerView.Adapter<MedListAdapter.MedViewHolder> {

    private static final String LOG_TAG = MedListAdapter.class.getSimpleName();

    Context mContext;

    Cursor mCursor;

//    ReminderUtilities mReminderUtilities;

    public MedListAdapter(Context context) {
        mContext = context;
    }


    @Override
    public MedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.med_card_view, parent, false);
        return new MedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedViewHolder holder, final int position) {

        mCursor.moveToPosition(position); // get to the right location in the cursor

        final long idIndex = mCursor.getLong(mCursor.getColumnIndexOrThrow(MedEntry.MED_DB_DEFAULT_ID));
        String name = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_NAME));
        int type = mCursor.getInt(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_TYPE));
        String description = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DESCRIPTION));
        int dosage = mCursor.getInt(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_DOSAGE));
        String interval = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_INTERVAL));
        String startDate = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_START_DATE));
        String endDate = mCursor.getString(mCursor.getColumnIndexOrThrow(MedEntry.MED_COLUMN_END_DATE));
        holder.itemView.setTag(idIndex);
        Log.i(LOG_TAG, "HERE WE ARE !!!" + name + type + description + dosage + interval + startDate + endDate);
        bindHolder(holder, name, type, description, dosage, interval, startDate, endDate);

//        holder.medTakenLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent incrementTakenCountIntent = new Intent(mContext, MedReminderIntentService.class);
//                incrementTakenCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_MED_TAKEN_COUNT);
//                incrementTakenCountIntent.putExtra("id", idIndex);
//                mContext.startService(incrementTakenCountIntent);
//                Log.i(LOG_TAG, "ALSO CALLED WHAT IS HERE :" + idIndex);
//            }
//        });

        holder.medIgnoreLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent incrementTakenCountIntent = new Intent(mContext, MedReminderIntentService.class);
                incrementTakenCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_MED_IGNORE_COUNT);
                incrementTakenCountIntent.putExtra("id", idIndex);
                mContext.startService(incrementTakenCountIntent);
                Log.i(LOG_TAG, "CALLED IGNORE WHAT IS HERE :" + position);
            }
        });
    }

    private void bindHolder(MedViewHolder holder, String name, int type, String description,
                            int dosage, String interval, String startDate, String endDate) {
        holder.medNameTextView.setText(name);
        holder.medDosageTextView.setText(Integer.toString(dosage));
        String startDateSubString = reduceDateLength(convertFormMilliSecToDate(startDate));
        holder.medStartTextView.setText(startDateSubString);
        String endDateSubString = reduceDateLength(convertFormMilliSecToDate(endDate));
        holder.medEndTextView.setText(endDateSubString);
        medTypeImage(holder, type);
    }
    //        Log.i(LOG_TAG,"LENGTH :" + startDateSubString);

    private String reduceDateLength(String dateValue){
        return dateValue.substring(0,dateValue.length()-5);
    }

    private String convertFormMilliSecToDate(String date) {
        long dateValue = Long.parseLong(date);
        String dateFormat = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
         Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateValue);
            return simpleDateFormat.format(calendar.getTime());
    }

    private void medTypeImage(MedViewHolder holder, int type) {
        switch (type) {
            case MedEntry.MED_TYPE_CAPSULES:
                holder.medTypeImageView.setImageResource(R.drawable.ic_capsule);
                break;
            case MedEntry.MED_TYPE_TABLETS:
                holder.medTypeImageView.setImageResource(R.drawable.ic_tablet);
                break;
            case MedEntry.MED_TYPE_SYRUP:
                holder.medTypeImageView.setImageResource(R.drawable.ic_syrup_liquid);
                break;
            case MedEntry.MED_TYPE_INHALER:
                holder.medTypeImageView.setImageResource(R.drawable.ic_inhaler);
                break;
            case MedEntry.MED_TYPE_DROPS:
                holder.medTypeImageView.setImageResource(R.drawable.ic_eye_drop);
                break;
            case MedEntry.MED_TYPE_OINTMENT:
                holder.medTypeImageView.setImageResource(R.drawable.ic_ointiment);
                break;
            case MedEntry.MED_TYPE_INJECTION:
                holder.medTypeImageView.setImageResource(R.drawable.ic_injection);
                break;
            case MedEntry.MED_TYPE_OTHERS:
                holder.medTypeImageView.setImageResource(R.drawable.ic_other_meds);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == cursor) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = cursor; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public Cursor entireCursorForHomeActivity() {
        return mCursor;
    }

    class MedViewHolder extends RecyclerView.ViewHolder {

        TextView medNameTextView;
        TextView medDosageTextView;
        TextView medStartTextView;
        TextView medEndTextView;
        ImageView medTypeImageView;
        LinearLayout medTakenLinearLayout;
        LinearLayout medIgnoreLinearLayout;


        public MedViewHolder(View itemView) {
            super(itemView);

            medNameTextView = itemView.findViewById(R.id.med_name_card);
            medDosageTextView = itemView.findViewById(R.id.med_dosage_card);
            medStartTextView = itemView.findViewById(R.id.start_date_card);
            medEndTextView = itemView.findViewById(R.id.end_date_card);
            medTypeImageView = itemView.findViewById(R.id.med_type_image_card);
            medTakenLinearLayout = itemView.findViewById(R.id.taken_layout_card);
            medIgnoreLinearLayout = itemView.findViewById(R.id.ignore_layout_card);

        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.length() == 0){

        }
    }
}
