package com.seanschlaefli.medstat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MedicalGraphFragment extends Fragment {

    public static final String TAG = "GraphViewFragment";
    private static final String ARG_NAME = "name";
    private static final String ARG_FILTER = "filter";

    private LineChart mLineChart;
    private LineDataSet mLineData;

    private TextView mNoData;

    private String mName = null;
    private long mFilterInMS = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_graph, container, false);

        mLineChart = view.findViewById(R.id.line_chart_id);
        mNoData = view.findViewById(R.id.no_data_text_view);

        Bundle args = getArguments();
        if (args != null) {
            mName = args.getString(ARG_NAME);
            mFilterInMS = args.getLong(ARG_FILTER);
        }

        updateGraph(mName, mFilterInMS);
        return view;
    }

    public void updateGraph(String name, long filterInMS) {
        if (name == null || filterInMS == -1) {
            return;
        }
        if (mLineChart != null) {
            List<MedicalItem> filteredItems =
                    MedicalItemFilter.filterByTime(
                            MedicalItemBank.get(getContext()).getMedicalItemsByName(name),
                            Calendar.getInstance().getTimeInMillis(),
                            filterInMS
                    );
            if (filteredItems.size() == 0) {
                updateVisibility(View.INVISIBLE, View.VISIBLE);
            } else {
                initializeLineData(name, filteredItems);
                updateVisibility(View.VISIBLE, View.INVISIBLE);
            }
        }
    }

    private void initializeLineData(String name, List<MedicalItem> medicalItems) {
        if (isAdded()) {
            List<Entry> values = new ArrayList<>();
            for (MedicalItem item : medicalItems) {
                values.add(new Entry(
                        (float) item.getDateTime().getMillis(),
                        (float) item.getValue()));
            }
            mLineData = new LineDataSet(values, name);
            mLineData.setDrawCircles(false);
            mLineData.setColor(ContextCompat.getColor(getActivity(), R.color.graph_color));
            mLineChart.getDescription().setText("");
            mLineChart.getXAxis().setDrawGridLines(false);
            setLineChartData();
        }
    }

    private void setLineChartData() {
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(mLineData);
        LineData data = new LineData(dataSets);
        mLineChart.setData(data);
        mLineChart.invalidate();
    }

    private List<MedicalItem> filterByTimeInMillis(List<MedicalItem> items, long minimum) {
        List<MedicalItem> filteredItems = new ArrayList<>();
        for (MedicalItem item: items) {
            long itemMillis = item.getDateTime().getMillis();
            if (itemMillis >= minimum) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    private void updateVisibility(int graphVisibility, int noDataVisibility) {
        mLineChart.setVisibility(graphVisibility);
        mNoData.setVisibility(noDataVisibility);
    }



    public static MedicalGraphFragment newInstance(String name, long filterInMS) {
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putLong(ARG_FILTER, filterInMS);

        MedicalGraphFragment fragment = new MedicalGraphFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
