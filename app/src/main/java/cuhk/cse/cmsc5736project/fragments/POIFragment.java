package cuhk.cse.cmsc5736project.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cuhk.cse.cmsc5736project.LocationManager;
import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.adapters.POIListAdapter;
import cuhk.cse.cmsc5736project.models.POI;
import cuhk.cse.cmsc5736project.utils.Utility;


public class POIFragment extends Fragment {

    public static final String TAG = POIFragment.class.getSimpleName();

    // Variable for fragment coloring
    private static final String ARG_COLOR = "color";
    private int color;

    // UI elements
    private RecyclerView recyclerView;
    private POIListAdapter adapter;

    public POIFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            color = getArguments().getInt(ARG_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutColor = Utility.getLighterColor(color);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pois, container, false);
        rootView.setBackgroundColor(layoutColor);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_poi_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setBackgroundColor(layoutColor);

        adapter = new POIListAdapter(getContext());
        recyclerView.setAdapter(adapter);

        Log.i(TAG, "onCreateView");

        return rootView;
    }

    public void checkIfArriveBookmarkedLocation() {
        // Get POI list
        List<POI> poiList = LocationManager.getInstance().getPOIList();

        // TODO: Sort POI list by RSSI value
        // TODO: Check if first POI of list if bookmarked
        // TODO: If yes, remove bookmark state, and update UI via adapter.notifyDataSetChanged();
    }

}