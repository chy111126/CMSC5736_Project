package cuhk.cse.cmsc5736project.fragments;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

import cuhk.cse.cmsc5736project.LocationManager;
import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.interfaces.OnFriendListChangeListener;
import cuhk.cse.cmsc5736project.interfaces.OnFriendResultListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIResultListener;
import cuhk.cse.cmsc5736project.models.Friend;
import cuhk.cse.cmsc5736project.models.POI;
import cuhk.cse.cmsc5736project.models.Pin;
import cuhk.cse.cmsc5736project.views.PinView;

import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP;


public class MapFragment extends Fragment {

    public static final String TAG = MapFragment.class.getSimpleName();

    // Variable for fragment coloring
    private static final String ARG_COLOR = "color";
    private int color;

    PinView imageView;
    float lastKnownX;
    float lastKnownY;
    LocationManager locationManager;

    //List<Pin> pinList = new ArrayList<Pin>();

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

        imageView = (PinView)rootView.findViewById(R.id.mapView);
        setImageViewListeers(imageView);

        //TODO: Map test
/*
        locationManager = LocationManager.getInstance();
        locationManager.getSimulatedPOIDefinitions(getContext(), new OnPOIResultListener(){
            public void onRetrieved(List<POI> poiList){
                for(POI poi: poiList) {
                    List<Pin> pinList = new ArrayList<Pin>() ;
                    PointF pinPosition = poi.getPosition();
                    pinList.add(new Pin(MapFragment.this.getContext(), pinPosition, R.drawable.map_marker));
                    imageView.addPinList(pinList);
                }
            }
        });
        locationManager.getSimulatedFriendDefinitions(getContext(), new OnFriendResultListener(){
            public void onRetrieved(List<Friend> friendList){
                //TODO: handle friend list
            }
        });
*/
        return rootView;
    }

    void setImageViewListeers(PinView imageView){
        imageView.setImage(ImageSource.resource(R.drawable.shb_00));
        imageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);
        //imageView.setPin(new PointF(1000,100));

        final PinView imageViewF = imageView;

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Log.i("", "onTouch: " + motionEvent.getAction());
                if (view.getId()== R.id.mapView && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    lastKnownX= motionEvent.getX();
                    lastKnownY= motionEvent.getY();
                    Log.i("image view: onTouch: ", lastKnownX + ", " + lastKnownY);
                }
                return false;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.imageView) {
                    Toast.makeText(getActivity(), "Long clicked "+lastKnownX+" "+lastKnownY, Toast.LENGTH_SHORT).show();
                    Log.d("EditPlanFragment","Scale "+imageViewF.getScale());
                    //pinList.add(new Pin(MapFragment.this.getContext(), new PointF(lastKnownX,lastKnownY),R.drawable.map_marker));
                    //imageViewF.setPin(new PointF(lastKnownX,lastKnownY));

                    imageViewF.post(new Runnable(){
                        public void run(){
                            imageViewF.getRootView().postInvalidate();
                        }
                    });

                    //return true;
                }
                //return false;
            }
        });

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