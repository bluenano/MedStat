package com.seanschlaefli.medstat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MedicalItemListFragment extends Fragment {

    public static final String TAG = "MedicalItemListFragment";
    private static final String ARG_NAME = "name";

    private RecyclerView mMedicalItemRecyclerView;
    private MedicalItemAdapter mAdapter;
    private TextView mNoDataTextView;

    private String mName = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_list, container, false);

        mNoDataTextView = (TextView) view.findViewById(R.id.no_data_text_view);
        mMedicalItemRecyclerView = (RecyclerView) view.findViewById(R.id.med_data_list);
        if (getActivity() != null) {
            mMedicalItemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            DividerItemDecoration decor = new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL);
            decor.setDrawable(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.custom_divider, null
            ));
            mMedicalItemRecyclerView.addItemDecoration(decor);
        }

        Bundle args = getArguments();
        if (args != null) {
            mName = args.getString(ARG_NAME);
        }

        updateList(mName);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList(mName);
    }

    private void setVisibility(boolean visibility) {
        if (visibility) {
            mMedicalItemRecyclerView.setVisibility(View.VISIBLE);
            mNoDataTextView.setVisibility(View.INVISIBLE);
        } else {
            mMedicalItemRecyclerView.setVisibility(View.INVISIBLE);
            mNoDataTextView.setVisibility(View.VISIBLE);
        }
    }

    public void updateList(String name) {
        MedicalItemBank medicalItemBank = MedicalItemBank.get(SingleFragmentActivity.sContext);
        List<MedicalItem> medicalItems = medicalItemBank.getMedicalItemsByName(name);
        if (mMedicalItemRecyclerView != null) {
            Log.d(TAG, "Updating list with new data from " + name);
            if (mAdapter == null) {
                mAdapter = new MedicalItemAdapter(medicalItems);
                mMedicalItemRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setMedicalItems(medicalItems);
                mAdapter.notifyDataSetChanged();
            }
            if (medicalItems.size() > 0) {
                setVisibility(true);
            } else {
                setVisibility(false);
            }
        }
    }

    public static MedicalItemListFragment newInstance(String name) {
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        MedicalItemListFragment fragment = new MedicalItemListFragment();
        fragment.setArguments(args);
        return fragment;
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
