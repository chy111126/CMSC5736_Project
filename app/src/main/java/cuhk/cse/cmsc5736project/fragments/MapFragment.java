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
import cuhk.cse.cmsc5736project.views.FriendListDialog;
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
    private HashMap<POI, Pin> pinHm = new HashMap<>();

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
                    // Set pins for each POI
                    Pin newPin = null;
                    if (poi.getName().contains("Toilet"))
                        newPin = new Pin(getContext(), poi, R.drawable.toilet_small_icon);
                    else
                        newPin = new Pin(getContext(), poi, R.drawable.booth_small_icon);
                    pinList.add(newPin);
                    pinHm.put(poi, newPin);
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
                for (Friend friend: friendList) {
                    Log.i("init: ", "friend " + friend.getName() + " at " + friend.getNearestLocation());
                    Pin friendAtPin = pinHm.get(friend.getNearestLocation());
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
                Log.i("add: ", "friend " + friend.getName() + " at " + friend.getNearestLocation());
                Pin friendAtPin = pinHm.get(friend.getNearestLocation());
                if (friendAtPin!=null) {
                    friendAtPin.addFriend(friend);
                    friendHm.put(friend, friendAtPin);
                }
            }

            @Override
            public void onDeleted(Friend friend) {
                Pin friendAtPin = pinHm.get(friend.getNearestLocation());
                if (friendAtPin!=null)
                    friendAtPin.removeFriend(friend);
                friendHm.remove(friend);
            }

            @Override
            public void onChanged() {
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

    void setImageViewListeners(final PinView imageView){
        imageView.setImage(ImageSource.resource(R.drawable.floorplan));
        imageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Log.i("image view: ", "onTouch: " + motionEvent.getAction());
                if (view.getId()== imageView.getId() && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    lastKnownX= motionEvent.getX();
                    lastKnownY= motionEvent.getY();
                    //Log.i("image view: ", "onTouch: " + lastKnownX + ", " + lastKnownY);
                }
                if (view.getId()== imageView.getId() && motionEvent.getAction() == MotionEvent.ACTION_UP){


                    //Log.i("image view: ", "onTouch: " + lastKnownX + ", " + lastKnownY);
                }
                return false;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == imageView.getId()) {
                    Pin markedPin = imageView.checkClickedPos(new PointF(lastKnownX, lastKnownY));;
                    //if (markedPin!=null)
                    imageView.setMarkedPin(markedPin);
                    imageView.post(new Runnable(){
                        public void run(){
                            imageView.invalidate();
                        }
                    });
                }
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (view.getId() == imageView.getId()) {
                    Pin markedPin = imageView.checkClickedPos(new PointF(lastKnownX, lastKnownY));;
                    if (markedPin!=null) {
                        imageView.setMarkedPin(markedPin);
                        if (markedPin.getFriendList() != null) {
                            List<Friend> friendsAtPin = markedPin.getFriendList();
                            StringBuilder message = new StringBuilder();
                            if (friendsAtPin.size()<=0) message.append("Sorry, no friends here!");
                            else {
                                int friendCount = 0;
                                for (Friend friend : friendsAtPin) {
                                    message.append(friend.getName() + "\n");
                                    friendCount++;
                                    if (friendCount>=20) {
                                        message.append("and more...");
                                        break;
                                    }
                                }
                            }

                            FriendListDialog cdd = new FriendListDialog(MapFragment.this.getActivity(), message.toString());
                            cdd.show();
                        }
                    }

                   imageView.post(new Runnable(){
                        public void run(){
                            imageView.invalidate();
                        }
                    });

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