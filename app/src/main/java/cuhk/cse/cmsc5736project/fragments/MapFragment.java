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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cuhk.cse.cmsc5736project.LocationManager;
import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.interfaces.OnFriendListChangeListener;
import cuhk.cse.cmsc5736project.interfaces.OnFriendResultListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIListChangeListener;
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

    private List<Friend> friendList = new ArrayList<>();

    //List<Pin> pinList = new ArrayList<Pin>();
    HashMap<PointF, Pin> pinHm = new HashMap<>();

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
        locationManager = LocationManager.getInstance();
        locationManager.getSimulatedPOIDefinitions(getContext(), new OnPOIResultListener(){
            public void onRetrieved(List< POI > poiList){
                List<Pin> pinList = new ArrayList<Pin>() ;
                for(POI poi: poiList) {
                    //PointF pinPosition = poi.getPosition();
                    pinList.add(new Pin(getContext(), poi, R.drawable.map_marker_icon));
                }
                imageView.addPinList(pinList);
            }
            public void onChanged(){
                imageView.refreshPins();
            }
        });

        LocationManager.getInstance().setOnPOIChangedMapFragmentListener(new OnPOIListChangeListener() {
            @Override
            public void onChanged() {
                imageView.refreshPins();
            }
        });

        locationManager.getSimulatedFriendDefinitions(getContext(), new OnFriendResultListener(){
            public void onRetrieved(List< Friend > friendList){
                //TODO: handle friend list
                for (Friend friend: friendList) {
                    Log.i("init: ", "friend " + friend.getName() + " at " + friend.getNearestLocation());
                }
            }
        });

        LocationManager.getInstance().setOnFriendListChangeMapFragmentListener(new OnFriendListChangeListener() {
            @Override
            public void onAdded(Friend item) {
                friendList.add(item);
                sortViewList();

                //FriendListAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onDeleted(Friend item) {
                friendList.remove(item);
                sortViewList();
                //FriendListAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onChanged() {
                sortViewList();
                //FriendListAdapter.this.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    private void sortViewList() {
        /*
        Collections.sort(friendList, new Comparator<Friend>() {
            @Override
            public int compare(Friend f1, Friend f2) {
                if(f1.getBeacon().getProximity() > f2.getBeacon().getProximity()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        */
    }
    void setImageViewListeers(final PinView imageView){
        imageView.setImage(ImageSource.resource(R.drawable.floorplan));
        imageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);
        //imageView.setPin(new PointF(1000,100));

        //final PinView imageViewF = imageView;

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Log.i("image view: ", "onTouch: " + motionEvent.getAction());
                if (view.getId()== imageView.getId() && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    lastKnownX= motionEvent.getX();
                    lastKnownY= motionEvent.getY();
                    //Log.i("image view: ", "onTouch: " + lastKnownX + ", " + lastKnownY);
                    //imageViewF.addPin(new Pin(MapActivity.this, new PointF(lastKnownX,lastKnownY),R.drawable.map_marker,"marked"));
                }
                return false;
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (view.getId() == imageView.getId()) {
                    Toast.makeText(getContext(), "Long clicked "+lastKnownX+" "+lastKnownY, Toast.LENGTH_SHORT).show();
                    Log.d("EditPlanFragment","Scale "+imageView.getScale());
                    imageView.addPin(new Pin(getContext(), new PointF(lastKnownX,lastKnownY),R.drawable.map_marker,"long clicked"));

/*                    imageViewF.post(new Runnable(){
                        public void run(){
                            imageViewF.getRootView().postInvalidate();
                        }
                    });*/

                    return true;
                }
                return false;
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