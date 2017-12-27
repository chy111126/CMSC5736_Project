package cuhk.cse.cmsc5736project.fragments;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
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
import java.util.Map;

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

    private Map<Friend, Pin> friendHm = new HashMap<>();

    //List<Pin> pinList = new ArrayList<Pin>();
    private HashMap<PointF, Pin> pinHm = new HashMap<>();

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
        setImageViewListeners(imageView);

        //TODO: Map test
        locationManager = LocationManager.getInstance();
        locationManager.getPOIDefinitions(getContext(), new OnPOIResultListener(){
            public void onRetrieved(List< POI > poiList){
                List<Pin> pinList = new ArrayList<Pin>() ;
                for(POI poi: poiList) {
                    //PointF pinPosition = poi.getPosition();
                    Pin newPin = null;
                    if (poi.getName().contains("Toilet"))
                        newPin = new Pin(getContext(), poi, R.drawable.toilet_small_icon);
                    else
                        newPin = new Pin(getContext(), poi, R.drawable.booth_small_icon);
                    pinList.add(newPin);
                    pinHm.put(poi.getPosition(), newPin);
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

        locationManager.getCurrentUserFriendList(getContext(), new OnFriendResultListener(){
            public void onRetrieved(List< Friend > friendList){
                //TODO: handle friend list
                for (Friend friend: friendList) {
                    Log.i("init: ", "friend " + friend.getName() + " at " + friend.getNearestLocation());
                    Pin friendAtPin = pinHm.get(friend.getNearestLocation());//new PointF(1008.75f, 995.209f));
                    if (friendAtPin!=null) {
                        Log.i("init: ", "add friend " + friend.getName() + " to " + friendAtPin.getDescription());
                        friendAtPin.addFriend(friend);
                        friendHm.put(friend, friendAtPin);
                    }
                }
            }
        });

        LocationManager.getInstance().setOnFriendListChangeMapFragmentListener(new OnFriendListChangeListener() {
            @Override
            public void onAdded(Friend friend) {
                //friendList.add(item);
                //sortViewList();
                Pin friendAtPin = pinHm.get(friend.getNearestLocation());
                if (friendAtPin!=null) {
                    friendAtPin.addFriend(friend);
                    friendHm.put(friend, friendAtPin);
                }
                //FriendListAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onDeleted(Friend friend) {
                //friendList.remove(item);
                Pin friendAtPin = pinHm.get(friend.getNearestLocation());
                if (friendAtPin!=null)
                    friendAtPin.removeFriend(friend);
                friendHm.remove(friend);

                //sortViewList();
                //FriendListAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onChanged() {
                //sortViewList();
                //FriendListAdapter.this.notifyDataSetChanged();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        updateFriendList();
                    }
                });
            }
        });

        return rootView;
    }

    void updateFriendList(){
        Map<Friend, Pin> tempHm = new HashMap<>(friendHm);
        for (Map.Entry<Friend, Pin> entry : tempHm.entrySet()) {
            Friend friend = entry.getKey();
            Pin pin = entry.getValue();
            Pin friendAtPin = pinHm.get(friend.getNearestLocation());
            if (friendAtPin!=pin){
                if (pin!=null){
                    pin.removeFriend(friend);
                    friendHm.remove(friend);
                }
                if (friendAtPin!=null){
                    friendAtPin.addFriend(friend);
                    friendHm.put(friend, friendAtPin);
                }
            }
        }
    }

/*    private void sortViewList() {
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
    }*/
    void setImageViewListeners(final PinView imageView){
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
                    Toast.makeText(getContext(), "Marked "+lastKnownX+" "+lastKnownY, Toast.LENGTH_SHORT).show();
                    Log.d("EditPlanFragment","Scale "+imageView.getScale());
                    imageView.addPin(new Pin(getContext(), new PointF(lastKnownX,lastKnownY),R.drawable.map_marker,"marked"));

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