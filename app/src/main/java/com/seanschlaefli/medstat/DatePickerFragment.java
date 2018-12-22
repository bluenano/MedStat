package com.seanschlaefli.medstat;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import org.joda.time.DateTime;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_MED_DATE = "med_data_date";
    public static final String EXTRA_DATE =
            "com.seanschlaefli.medstat.date";

    private DatePicker mDatePicker;

    private DateTime mDateTime;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDateTime = (DateTime) getArguments().getSerializable(ARG_MED_DATE);
        int year = mDateTime.getYear();
        int month = mDateTime.getMonthOfYear()-1;
        int day = mDateTime.getDayOfMonth();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth()+1;
                                int day = mDatePicker.getDayOfMonth();
                                mDateTime = new DateTime(year, month, day, mDateTime.getHourOfDay(),
                                        mDateTime.getMinuteOfHour());
                                sendResult(Activity.RESULT_OK, mDateTime);
                            }
                        })
                .create();
    }

    public static DatePickerFragment newInstance(DateTime date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_MED_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setStyle(STYLE_NORMAL, R.style.PickerTheme);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void sendResult(int resultCode, DateTime date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }


}
