package com.seanschlaefli.medstat;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import org.joda.time.DateTime;

public class TimePickerFragment extends DialogFragment {

    private static final String TAG = "TimePickerFragment";

    private static final String ARG_MEDSTAT_TIME = "medstat_time";
    public static final String EXTRA_TIME =
            "com.seanschlaefli.medstat.extra_time";

    private TimePicker mTimePicker;

    private DateTime mDateTime;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDateTime = (DateTime) getArguments().getSerializable(ARG_MEDSTAT_TIME);
        int hour = mDateTime.getHourOfDay();
        int minute = mDateTime.getMinuteOfHour();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.time_picker);

        if (Build.VERSION.SDK_INT >= 23) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour, minute;
                                if (Build.VERSION.SDK_INT >= 23) {
                                    hour = mTimePicker.getHour();
                                    minute = mTimePicker.getMinute();
                                } else {
                                    hour = mTimePicker.getCurrentHour();
                                    minute = mTimePicker.getCurrentMinute();
                                }

                                mDateTime = new DateTime(mDateTime.getYear(), mDateTime.getMonthOfYear(),
                                        mDateTime.getDayOfMonth(), hour, minute);
                                sendResult(Activity.RESULT_OK, mDateTime);
                            }
                        })
                .create();

    }


    public static TimePickerFragment newInstance(DateTime date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDSTAT_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        fragment.setStyle(STYLE_NORMAL, R.style.PickerTheme);
        return fragment;
    }


    private void sendResult(int resultCode, DateTime date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private int adjustHour(int hour) {
        if (hour == 0) {
            return 12;
        } else if (hour > 12) {
            return hour-12;
        }
        return hour;
    }
}
