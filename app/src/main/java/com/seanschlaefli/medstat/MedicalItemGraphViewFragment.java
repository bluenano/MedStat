package com.seanschlaefli.medstat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicalItemGraphViewFragment extends Fragment {

    public static final String TAG = "GraphViewFragment";
    private static final String ARG_NAME = "name";

    private static final String ALL = "All";
    private static final String YESTERDAY = "1 day";
    private static final String LAST_WEEK = "7 days";
    private static final String LAST_MONTH = "30 days";
    private static final String LAST_YEAR = "365 days";

    private static final long MILLIS_PER_DAY = 86400000;

    private Spinner mGraphFilterSpinner;
    private GraphView mGraphView;
    private String mMedItemName;
    private LineGraphSeries<DataPoint> mSeries;
    private TextView mNoDataTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_view_med_item_graph, container, false);
        mGraphFilterSpinner = (Spinner) view.findViewById(R.id.graph_filter_spinner_id);
        mGraphView = (GraphView) view.findViewById(R.id.graph_view_id);
        mSeries = new LineGraphSeries<>();
        mGraphView.addSeries(mSeries);
        mNoDataTextView = (TextView) view.findViewById(R.id.no_data_text_view);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(super.getActivity(),
                R.array.filter_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGraphFilterSpinner.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null) {
            mMedItemName = args.getString(ARG_NAME);
        }

        updateGraphViewDisplay(mMedItemName);
        return view;
    }

    public void updateGraphViewDisplay(String name) {
        if (mGraphView != null) {
            MedicalItemBank medicalItems = MedicalItemBank.get(getActivity());
            List<MedicalItem> items = medicalItems.getMedicalItemsByName(name);
            if (items.size() > 1) {
                long current = new Date().getTime();
                long millisFilterAmount = getMillisAmountFromFilter((String) mGraphFilterSpinner.getSelectedItem(), current);
                items = filterByTimeInMillis(items, current - millisFilterAmount);

                List<DataPoint> listPoints = new ArrayList<>();
                List<Date> dates = new ArrayList<>();
                for (MedicalItem item : items) {
                    Date date = item.getDate();
                    dates.add(date);
                    listPoints.add(new DataPoint(date, item.getValue()));
                }
                setupGraphAxes(mGraphView, items, listPoints, dates);
                updateVisibility(View.VISIBLE, View.INVISIBLE);
            } else {
                updateVisibility(View.INVISIBLE, View.VISIBLE);
            }
        }
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


    private static long getMillisAmountFromFilter(String filter, long currentTime) {
        switch (filter) {
            case ALL:
                return currentTime;
            case YESTERDAY:
                return MILLIS_PER_DAY;
            case LAST_WEEK:
                return 7 * MILLIS_PER_DAY;
            case LAST_MONTH:
                return 30 * MILLIS_PER_DAY;
            case LAST_YEAR:
                return 365 * MILLIS_PER_DAY;
            default:
                return 0;
        }
    }


    private void setupGraphAxes(GraphView graph,
                                List<MedicalItem> items,
                                List<DataPoint> listOfPoints,
                                List<Date> dates) {
        DataPoint[] points = new DataPoint[listOfPoints.size()];
        points = listOfPoints.toArray(points);
        // you must make sure the list is sorted
        mSeries.resetData(points);

        Viewport viewport = graph.getViewport();

        MedicalItem max = MathStat.findMax(items);
        MedicalItem min = MathStat.findMin(items);
        int numVerticalLabels = 2;
        int numHorizontalLabels = points.length < 5 ? 2 : 5;
        if (max != null && min != null) {
            double maxLabel = Math.ceil(max.getValue());
            double minLabel = Math.floor(min.getValue());
            setYAxisBounds(viewport, maxLabel, minLabel);
            numVerticalLabels = calculateNumVerticalLabels((int) maxLabel, (int) minLabel);
        }

        Log.d(TAG, "vertical labels " + numVerticalLabels);
        GridLabelRenderer renderer = graph.getGridLabelRenderer();
        renderer.setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        renderer.setNumVerticalLabels(numVerticalLabels);
        renderer.setNumHorizontalLabels(numHorizontalLabels);
        renderer.setHumanRounding(false);

    }

    public static Fragment newInstance() {
        Bundle args = new Bundle();
        //args.putString(ARG_NAME, name);

        Fragment fragment = new MedicalItemGraphViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    /*
    private void setXAxisBounds(Viewport viewport, long max, long min) {
        Log.d(TAG, "max x: " + max);
        Log.d(TAG, "min x: " + min);
        viewport.setMaxX(max);
        viewport.setMinX(min);
        viewport.setXAxisBoundsManual(true);
    }
    */

    private void setYAxisBounds(Viewport viewport, double max, double min) {
        viewport.setMaxY(max);
        viewport.setMinY(min);
        viewport.setYAxisBoundsManual(true);
    }


    private int calculateNumVerticalLabels(int max, int min) {
        int diff = max-min;
        int numLabels = 1;
        for (int i = 2; i < 10; i++) {
            if (diff % i == 0) {
                numLabels = i;
            }
        }
        return numLabels+1;
    }

    private void updateVisibility(int graphVisibility, int noDataVisibility) {
        mGraphView.setVisibility(graphVisibility);
        mNoDataTextView.setVisibility(noDataVisibility);
    }
}
