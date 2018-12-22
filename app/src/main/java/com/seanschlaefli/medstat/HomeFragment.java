package com.seanschlaefli.medstat;

import android.content.Intent;
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

import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private BottomNavigationView mToolbar;
    private Spinner mSpinner;

    private MedicalItemListFragment mListFragment;
    private MedicalGraphFragment mGraphFragment;

    private static final String TAG = "HomeFragment";
    private static final String SPINNER_KEY = "spinner_key";

    private static final int REQUEST_ADD_MED = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mToolbar = view.findViewById(R.id.navigation_view);
        mToolbar.setOnNavigationItemSelectedListener(createToolbarListener());

        mSpinner = view.findViewById(R.id.med_data_spinner);
        setSpinnerData();

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                updateFragments(selection);
                Log.d(TAG, "selection is " + selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        if (savedInstanceState != null) {
            mSpinner.setSelection(savedInstanceState.getInt(SPINNER_KEY));
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
                Intent intent = AddMedActivity.newIntent(getActivity(), UUID.randomUUID());
                startActivityForResult(intent, REQUEST_ADD_MED);
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SPINNER_KEY, mSpinner.getSelectedItemPosition());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateFragments(String name) {
        if (mListFragment != null) {
            mListFragment.updateList(name);
        } else {
            mListFragment = MedicalItemListFragment.newInstance(name);
        }
        if (mGraphFragment != null) {
            mGraphFragment.updateGraph(name);
        } else {
            mGraphFragment = MedicalGraphFragment.newInstance(name);
        }
    }

    private void updateDisplay(String tag) {
        String selection = (String) mSpinner.getSelectedItem();
        if (tag.equals(MedicalItemListFragment.TAG)) {
            mListFragment = MedicalItemListFragment.newInstance(selection);
            replaceDisplay(mListFragment);
        } else if (tag.equals(MedicalGraphFragment.TAG)) {
            mGraphFragment = MedicalGraphFragment.newInstance(selection);
            replaceDisplay(mGraphFragment);
        }
    }


    private void replaceDisplay(Fragment fragment) {
        if (fragment != null) {
            Log.d(TAG, "Replacing the display fragment");
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.display_fragment_container, fragment)
                    .commit();
        }
    }

    private void setSpinnerData() {
        List<String> names = MedicalItemBank.get(getContext()).getNames();
        for (String s: names) {
            Log.d(TAG, s);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.my_spinner_item,
                names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener createToolbarListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list:
                        Log.d(TAG, "clicked list");
                        updateDisplay(MedicalItemListFragment.TAG);
                        return true;
                    case R.id.graph:
                        Log.d(TAG, "clicked graph");
                        updateDisplay(MedicalGraphFragment.TAG);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

}
