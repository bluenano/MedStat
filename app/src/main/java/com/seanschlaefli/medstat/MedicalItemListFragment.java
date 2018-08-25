package com.seanschlaefli.medstat;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MedicalItemListFragment extends Fragment {

    public static final String TAG = "MedicalItemListFragment";
    private static final String ARG_NAME = "name";
    private static final String SPINNER_DATA_KEY = "spinner_data_key";

    private RecyclerView mMedicalItemRecyclerView;
    private MedicalItemAdapter mAdapter;
    private Spinner mNameSpinner;
    private TextView mNoDataTextView;

    private ArrayList<String> mSpinnerData;

    public void updateMedicalItemDisplay(String name) {

        MedicalItemBank medicalItemBank = MedicalItemBank.get(SingleFragmentActivity.sContext);
        List<MedicalItem> medicalItems = medicalItemBank.getMedicalItemsByName((String) mNameSpinner.getSelectedItem());

        for (MedicalItem item: medicalItems) {
            Log.d(TAG, item.toString());
        }

        if (mMedicalItemRecyclerView != null) {
            if (mAdapter == null) {
                mAdapter = new MedicalItemAdapter(medicalItems);
                mMedicalItemRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setMedicalItems(medicalItems);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_data_list, container, false);

        mNoDataTextView = (TextView) view.findViewById(R.id.no_data_text_view);
        mMedicalItemRecyclerView = (RecyclerView) view.findViewById(R.id.med_data_list);
        mNameSpinner = (Spinner) view.findViewById(R.id.name_spinner);

        if (savedInstanceState != null) {
            mSpinnerData = savedInstanceState.getStringArrayList(SPINNER_DATA_KEY);
        } else {
            setSpinnerData();
        }

        if (getActivity() != null) {
            mMedicalItemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mMedicalItemRecyclerView.addItemDecoration(
                    new DividerItemDecoration(getActivity(),
                            DividerItemDecoration.VERTICAL)
            );
        }

        attachAdapter(mNameSpinner);

        updateMedicalItemDisplay((String) mNameSpinner.getSelectedItem());
        return view;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SPINNER_DATA_KEY, mSpinnerData);
    }


    private void attachAdapter(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(super.getActivity(),
                android.R.layout.simple_spinner_item,
                mSpinnerData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void setSpinnerData() {
        String[] data = getResources().getStringArray(R.array.medical_array);
        mSpinnerData = new ArrayList<>(Arrays.asList(data));
    }


    public static Fragment newInstance() {
       return new MedicalItemListFragment();
    }

    private class MedicalItemHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

        private TextView mValueTextView;
        private TextView mTimeTextView;
        private TextView mDateTextView;

        public MedicalItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_med_data, parent, false));

            mValueTextView = (TextView) itemView.findViewById(R.id.value_text_view);
            mTimeTextView = (TextView) itemView.findViewById(R.id.time_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        public void bind(MedicalItem medicalItem) {
            String value = Double.toString(medicalItem.getValue()) + " " + medicalItem.getUnits();
            Log.d(TAG, value);

            mValueTextView.setText(value);
            mTimeTextView.setText(FormatDateTime.getFormattedTimeString(medicalItem.getDateTime()));
            mDateTextView.setText(FormatDateTime.getFormattedDateString(medicalItem.getDateTime()));
        }


    }

    private class MedicalItemAdapter extends RecyclerView.Adapter<MedicalItemHolder> {

        private List<MedicalItem> mMedicalItems;

        private MedicalItemAdapter(List<MedicalItem> medicalItems) {
            mMedicalItems = medicalItems;
        }

        @NonNull
        @Override
        public MedicalItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new MedicalItemHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull MedicalItemHolder holder, int position) {
            MedicalItem item = mMedicalItems.get(position);
            holder.bind(item);
        }


        @Override
        public int getItemCount() {
            return mMedicalItems.size();
        }

        public void setMedicalItems(List<MedicalItem> medicalItems) {
            mMedicalItems = medicalItems;
        }
    }
}
