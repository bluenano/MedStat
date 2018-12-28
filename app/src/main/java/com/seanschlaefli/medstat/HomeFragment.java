package com.seanschlaefli.medstat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String NAME_SPINNER_KEY = "name_spinner_key";
    private static final String FILTER_SPINNER_KEY = "filter_spinner_key";
    private static final String EXTRA_NAME = "extra_name";
    private static final String PREFERENCE_FILE = "SelectionPreferences";
    private static final int REQUEST_ADD_MED = 0;

    private static final String ALL = "All";
    private static final String YESTERDAY = "1 day";
    private static final String LAST_WEEK = "7 days";
    private static final String LAST_MONTH = "30 days";
    private static final String LAST_YEAR = "365 days";

    private static final long MILLIS_PER_DAY = 86400000;
    private static final int DAYS_PER_WEEK = 7;
    private static final int DAYS_PER_MONTH = 30; // not always 30
    private static final int DAYS_PER_YEAR = 365;

    private BottomNavigationView mToolbar;
    private Spinner mNameSpinner;
    private Spinner mFilterSpinner;
    private ArrayAdapter mNameAdapter;
    private ArrayAdapter mFilterAdapter;

    private MedicalItemListFragment mListFragment;
    private MedicalGraphFragment mGraphFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "In onCreateView");

        mToolbar = view.findViewById(R.id.navigation_view);
        mToolbar.setOnNavigationItemSelectedListener(createToolbarListener());

        mNameSpinner = view.findViewById(R.id.med_name_spinner);
        mFilterSpinner = view.findViewById(R.id.med_filter_spinner);
        setNameAdapter();
        setFilterAdapter();
        setSpinner(mNameSpinner, mNameAdapter);
        setSpinner(mFilterSpinner, mFilterAdapter);
        setSpinnerListener(mNameSpinner);
        setSpinnerListener(mFilterSpinner);

        if (savedInstanceState != null) {
            Log.d(TAG, "Saved instance state is not null");
            mNameSpinner.setSelection(savedInstanceState.getInt(NAME_SPINNER_KEY));
            mFilterSpinner.setSelection(savedInstanceState.getInt(FILTER_SPINNER_KEY));
        } else {
            mToolbar.setSelectedItemId(R.id.list);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = AddMedActivity.newIntent(
                        getActivity(),
                        (String) mNameSpinner.getSelectedItem());
                startActivityForResult(intent, REQUEST_ADD_MED);
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving fragment instance state");
        int position = mNameSpinner.getSelectedItemPosition();
        String name = (String) mNameSpinner.getItemAtPosition(position);
        Log.d(TAG, "Name before save is " + name);
        outState.putInt(NAME_SPINNER_KEY, mNameSpinner.getSelectedItemPosition());
        outState.putInt(FILTER_SPINNER_KEY, mFilterSpinner.getSelectedItemPosition());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "In onResume of HomeFragment");
        loadPreferences();
        updateFragments(
                (String) mNameSpinner.getSelectedItem(),
                getMillisAmountFromFilter()
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD_MED) {
                String name = data.getStringExtra(EXTRA_NAME);
                setNamePreference(name);
            }
        }
    }

    private void updateFragments(String name, long filterInMS) {
        updateList(name, filterInMS);
        updateGraph(name, filterInMS);
    }

    private void updateList(String name, long filterInMS) {
        if (mListFragment != null) {
            mListFragment.updateList(name, filterInMS);
        } else {
            mListFragment = MedicalItemListFragment.newInstance(name, filterInMS);
        }
    }

    private void updateGraph(String name, long filterInMS) {
        if (mGraphFragment != null) {
            mGraphFragment.updateGraph(name, filterInMS);
        } else {
            mGraphFragment = MedicalGraphFragment.newInstance(name, filterInMS);
        }
    }

    private void updateDisplay(String tag) {
        String selection = (String) mNameSpinner.getSelectedItem();
        long filterInMS = getMillisAmountFromFilter();
        if (tag.equals(MedicalItemListFragment.TAG)) {
            mListFragment = MedicalItemListFragment.newInstance(selection, filterInMS);
            replaceDisplay(mListFragment);
        } else if (tag.equals(MedicalGraphFragment.TAG)) {
            mGraphFragment = MedicalGraphFragment.newInstance(selection, filterInMS);
            replaceDisplay(mGraphFragment);
        }
    }

    private void replaceDisplay(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.display_fragment_container, fragment)
                    .commit();
        }
    }

    private void setSpinner(Spinner spinner, ArrayAdapter adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setSpinnerListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFragments(
                        (String) mNameSpinner.getSelectedItem(),
                        getMillisAmountFromFilter()
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // clear graph or list
            }
        });
    }

    private void setNameAdapter() {
        List<String> names = MedicalItemBank.get(getContext()).getNames();
        String[] namesArr = MedStatUtil.stringListToArr(names);
        Arrays.sort(namesArr);
        mNameAdapter = new ArrayAdapter<>(getContext(),
                R.layout.my_spinner_item,
                namesArr);
    }

    private void setFilterAdapter() {
        mFilterAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_array,
                R.layout.my_spinner_item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener createToolbarListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list:
                        updateDisplay(MedicalItemListFragment.TAG);
                        return true;
                    case R.id.graph:
                        updateDisplay(MedicalGraphFragment.TAG);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private int findPosition(ArrayAdapter adapter, String name) {
        for (int i = 0; i < adapter.getCount(); i++) {
            String current = (String) adapter.getItem(i);
            if (name.equals(current)) {
                return i;
            }
        }
        return 0;
    }

    private long getMillisAmountFromFilter() {
        String filter = (String) mFilterSpinner.getSelectedItem();
        long currentTime = Calendar.getInstance().getTimeInMillis();
        switch (filter) {
            case ALL:
                return currentTime;
            case YESTERDAY:
                return MILLIS_PER_DAY;
            case LAST_WEEK:
                return DAYS_PER_WEEK * MILLIS_PER_DAY;
            case LAST_MONTH:
                return DAYS_PER_MONTH * MILLIS_PER_DAY;
            case LAST_YEAR:
                return DAYS_PER_YEAR * MILLIS_PER_DAY;
            default:
                return 0;
        }
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                PREFERENCE_FILE,
                Context.MODE_PRIVATE
        ).edit();
        editor.putString(NAME_SPINNER_KEY,
                (String) mNameSpinner.getSelectedItem());
        editor.putString(FILTER_SPINNER_KEY,
                (String) mFilterSpinner.getSelectedItem());
        editor.apply();
        Log.d(TAG, "Saved preferences");
        Log.d(TAG, "Saved " + (String) mNameSpinner.getSelectedItem());
    }

    private void loadPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                PREFERENCE_FILE,
                Context.MODE_PRIVATE
        );
        String name = preferences.getString(NAME_SPINNER_KEY, null);
        String filter = preferences.getString(FILTER_SPINNER_KEY, null);
        if (name != null) {
            Log.d(TAG, "Name is not null");
            mNameSpinner.setSelection(findPosition(mNameAdapter, name));
        }
        if (filter != null) {
            Log.d(TAG, "Filter is not null");
            mFilterSpinner.setSelection(findPosition(mFilterAdapter, filter));
        }
        Log.d(TAG, "Loaded preferences");
    }

    private void clearPreferences() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                PREFERENCE_FILE,
                Context.MODE_PRIVATE
        ).edit();
        editor.clear();
        editor.apply();
        Log.d(TAG, "Cleared preferences");
    }

    private void setNamePreference(String name) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                PREFERENCE_FILE,
                Context.MODE_PRIVATE
        ).edit();
        editor.putString(NAME_SPINNER_KEY, name);
        editor.apply();
    }

    private void setFilterPreference(String filter) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                PREFERENCE_FILE,
                Context.MODE_PRIVATE
        ).edit();
        editor.putString(FILTER_SPINNER_KEY, filter);
        editor.apply();
    }

    public static Intent newIntent(String name) {
        Intent data = new Intent();
        data.putExtra(EXTRA_NAME, name);
        return data;
    }

}
