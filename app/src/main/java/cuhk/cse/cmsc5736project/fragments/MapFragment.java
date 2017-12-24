package cuhk.cse.cmsc5736project.fragments;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.views.PinView;

import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP;


public class MapFragment extends Fragment {

    public static final String TAG = MapFragment.class.getSimpleName();

    // Variable for fragment coloring
    private static final String ARG_COLOR = "color";
    private int color;

    public MapFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        rootView.setBackgroundColor(getLighterColor(color));

        Log.i(TAG, "onCreateView");

        PinView imageView = (PinView)rootView.findViewById(R.id.mapView);
        imageView.setImage(ImageSource.resource(R.drawable.shb_00));
        imageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);
        imageView.setPin(new PointF(50,50));

        return rootView;
    }

    private int getLighterColor(int color) {
        // Lighter color = 30% of given color hex
        return Color.argb(30,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

}