package com.seanschlaefli.medstat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;


public class AddMedFragment extends Fragment {

    private static int REQUEST_DATE = 0;
    private static int REQUEST_TIME = 1;

    private static String TAG = "AddMedFragment";
    private static String DIALOG_DATE = "DialogDate";
    private static String DIALOG_TIME = "DialogTime";
    private static String DATE_INDEX = "date_index";

    private Spinner mSpinner;
    private TextView mUnits;
    private EditText mCustomValueName;
    private EditText mCustomUnits;
    private EditText mMedValue;
    private Button mTimeButton;
    private Button mDateButton;
    private ImageButton mAddButton;

    private DateTime mDateTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_med, container, false);

        mSpinner = v.findViewById(R.id.med_select_spinner);
        mUnits = v.findViewById(R.id.units_text_view);
        mCustomValueName = v.findViewById(R.id.custom_value_edit_text);
        mCustomUnits = v.findViewById(R.id.custom_units_edit_text);
        mMedValue = v.findViewById(R.id.med_value_edit_text);
        mTimeButton = v.findViewById(R.id.time_button);
        mDateButton = v.findViewById(R.id.date_button);
        mAddButton = v.findViewById(R.id.add_button);

        mDateTime = new DateTime();


        if (savedInstanceState != null) {
            mDateTime = (DateTime) savedInstanceState.getSerializable(DATE_INDEX);
        }

        setSpinnerData();
        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString(AddMedActivity.EXTRA_NAME, null);
            if (name != null) {
                mSpinner.setSelection(findPosition(mSpinner.getAdapter(), name));

            }
        }
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTimeButtonClick();
            }
        });

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateButtonClick();
            }
        });


        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddButtonClick();
            }
        });

        updateDate();
        updateTime();
        updateUnits((String) mSpinner.getSelectedItem());

        return v;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DATE_INDEX, mDateTime);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            mDateTime = (DateTime) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            updateDate();
        } else if (requestCode == REQUEST_TIME) {
            mDateTime = (DateTime) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            updateTime();
        }
    }

    private void setSpinnerData() {
        List<String> names = MedicalItemBank.get(getContext()).getNames();
        names.add(getResources().getString(R.string.custom_value));
        String[] namesArr = MedStatUtil.stringListToArr(names);
        Arrays.sort(namesArr);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.my_spinner_item,
                namesArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        attachSpinnerListener();
    }

    private void attachSpinnerListener() {
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (selected.equals(getResources().getString(R.string.custom_value))) {
                    setVisibilityForCustomFields(View.VISIBLE);
                } else {
                    setVisibilityForCustomFields(View.INVISIBLE);
                    setVisibilityForFields(View.VISIBLE);
                }
                updateUnits(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setVisibilityForCustomFields(View.INVISIBLE);
            }
        });
    }

    private void handleTimeButtonClick() {
        FragmentManager fm = getFragmentManager();
        TimePickerFragment dialog = TimePickerFragment.newInstance(mDateTime);
        dialog.setTargetFragment(AddMedFragment.this, REQUEST_TIME);
        dialog.show(fm, DIALOG_TIME);
    }


    private void handleDateButtonClick() {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment dialog = DatePickerFragment.newInstance(mDateTime);
        dialog.setTargetFragment(AddMedFragment.this, REQUEST_DATE);
        dialog.show(fm, DIALOG_DATE);
    }


    private void handleAddButtonClick() {
        String customValue = getResources().getString(R.string.custom_value);
        String selected = (String) mSpinner.getSelectedItem();
        String name = selected.equals(customValue) ?
                mCustomValueName.getText().toString() :
                selected;
        if (selected != null) {
            if (isEditTextEmpty(mMedValue)) {
                alertUserOnEntryError(mMedValue, "Enter the value");
            } else if (selected.equals(customValue)
                    &&
                    isEditTextEmpty(mCustomValueName)) {
                alertUserOnEntryError(mCustomValueName, "Enter the name for this custom value");
            } else if (selected.equals(customValue)
                    &&
                    !isUniqueName(name)) {
                alertUserOnEntryError(mCustomValueName, "This custom value already exists");
            } else if (selected.equals(customValue)
                    &&
                   isEditTextEmpty(mCustomUnits)) {
                alertUserOnEntryError(mCustomUnits, "Enter the units for this custom value");
            } else {
                try {
                    saveItem();
                    Log.d(TAG, "Putting name in return intent: " + name);
                    getActivity().setResult(Activity.RESULT_OK,
                            HomeFragment.newIntent(name));
                    getActivity().finish();
                } catch (NumberFormatException e) {
                    alertUserOnEntryError(mMedValue, "Enter a numeric value");
                }
            }
        } else {
            showToast("Select a type");
        }
    }


    private void updateTime() {
        mTimeButton.setText(FormatDateTime.getFormattedTimeString(mDateTime));
    }


    private void updateDate() {
        mDateButton.setText(FormatDateTime.getFormattedDateString(mDateTime));
    }


    private void updateUnits(String name) {
        MedicalItemBank bank = MedicalItemBank.get(getActivity());
        mUnits.setText(bank.getUnits(name));
    }


    private void saveItem() {
        String name = (String) mSpinner.getSelectedItem();
        double value = Double.parseDouble(mMedValue.getText().toString());
        String units = mUnits.getText().toString();
        MedicalItemBank items = MedicalItemBank.get(getActivity());
        if (name.equals(getResources().getString(R.string.custom_value))) {
            name = mCustomValueName.getText().toString();
            units = mCustomUnits.getText().toString();
            items.addCustomValue(name, units);
        }
        MedicalItem item = new MedicalItem(name, value, units, new DateTime(mDateTime));
        items.addMedicalItem(item);
    }

    private int findPosition(SpinnerAdapter adapter, String name) {
        for (int i = 0; i < adapter.getCount(); i++) {
            String current = (String) adapter.getItem(i);
            if (name.equals(current)) {
                return i;
            }
        }
        return 0;
    }

    private void setVisibilityForFields(int visibility) {
        if (visibility == View.VISIBLE || visibility == View.INVISIBLE
                || visibility == View.GONE ) {
            mMedValue.setVisibility(visibility);
            mTimeButton.setVisibility(visibility);
            mDateButton.setVisibility(visibility);
            mAddButton.setVisibility(visibility);
        }
    }

    private void setVisibilityForCustomFields(int visibility) {
        if (visibility == View.VISIBLE || visibility == View.INVISIBLE
                || visibility == View.GONE ) {
            mCustomValueName.setVisibility(visibility);
            mCustomUnits.setVisibility(visibility);
            setVisibilityForFields(visibility);
        }
    }


    private void alertUserOnEntryError(View view, String message) {
        showToast(message);
        view.requestFocus();
    }


    private void showToast(String alert) {
        Toast.makeText(getActivity(), alert, Toast.LENGTH_LONG).show();
    }


    private boolean isEditTextEmpty(EditText e) {
        return e.getText().length() == 0;
    }

    private boolean isUniqueName(String name) {
        return MedicalItemBank.get(getActivity()).isUniqueName(name);
    }
}

