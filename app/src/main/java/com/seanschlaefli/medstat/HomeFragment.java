package com.seanschlaefli.medstat;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private BottomNavigationView mToolbar;
    private Fragment mDisplayFragment;
    private Fragment mListFragment;
    private Fragment mGraphFragment;

    private static final String TAG = "HomeFragment";
    private static final String DISPLAY_KEY = "display_key";

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

        mDisplayFragment = null;
        mListFragment = MedicalItemListFragment.newInstance();
        mGraphFragment = MedicalItemGraphViewFragment.newInstance();

        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString(DISPLAY_KEY);
            showFragment(tag);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        /*
        if (requestCode == REQUEST_ADD_MED &&
                mDisplayFragment.getTag() != null) {
            sendUpdateToDisplayFragment((String) mSpinner.getSelectedItem(),
                    mDisplayFragment.getTag());
        }
        */
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DISPLAY_KEY, mDisplayFragment.getTag());
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mDisplayFragment.getTag() != null) {
            showFragment(mDisplayFragment.getTag());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideFragment(mDisplayFragment.getTag());
    }

    private void updateDisplayFragment(Fragment current, String tag) {
        hideFragment(current.getTag());
        showFragment(tag);
        sendUpdateToDisplayFragment(tag);
    }


    private void showFragment(String tag) {
        FragmentManager fm = getFragmentManager();

        try {
            mDisplayFragment = fm.findFragmentByTag(tag);

            if (mDisplayFragment == null) {
                setDisplayFragment(tag);
                fm.beginTransaction()
                        .add(R.id.display_fragment_container, mDisplayFragment, tag)
                        .commit();
            } else {
                fm.beginTransaction()
                        .show(mDisplayFragment)
                        .commit();
            }
        } catch (NullPointerException e) {
            // handle this
        }
    }


    private void hideFragment(String tag) {
        FragmentManager fm = getFragmentManager();
        try {
            Fragment fragment = fm.findFragmentByTag(tag);

            if (fragment != null) {
                fm.beginTransaction()
                        .hide(fragment)
                        .commit();
            }
        } catch (NullPointerException e) {
            // handle this
        }
    }


    private void setDisplayFragment(String tag) {
        if (tag.equals(MedicalItemListFragment.TAG)) {
            mDisplayFragment = mListFragment;
        } else if (tag.equals(MedicalItemGraphViewFragment.TAG)) {
            mDisplayFragment = mGraphFragment;
        }
    }


    private void sendUpdateToDisplayFragment(String tag) {
        if (tag.equals(MedicalItemListFragment.TAG)) {
            MedicalItemListFragment list = (MedicalItemListFragment) mDisplayFragment;
            //list.updateMedicalItemDisplay();
        } else if (tag.equals(MedicalItemGraphViewFragment.TAG)) {
            MedicalItemGraphViewFragment graph = (MedicalItemGraphViewFragment) mDisplayFragment;
            //graph.updateGraphViewDisplay(name);
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener createToolbarListener() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list:
                        updateDisplayFragment(mDisplayFragment, MedicalItemListFragment.TAG);
                        return true;
                    case R.id.graph:
                        updateDisplayFragment(mDisplayFragment, MedicalItemGraphViewFragment.TAG);
                        return true;
                    case R.id.profile:
                        hideFragment(mDisplayFragment.getTag());
                        //showFragment(MedicalProfileFragment.TAG);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }
}
