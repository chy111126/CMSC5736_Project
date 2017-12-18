package cuhk.cse.cmsc5736project;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cuhk.cse.cmsc5736project.interfaces.OnFriendResultListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIResultListener;
import cuhk.cse.cmsc5736project.models.Friend;
import cuhk.cse.cmsc5736project.models.POI;

/**
 * Created by TCC on 12/18/2017.
 */

public class LocationManager {

    // ----- POI -----
    private static List<POI> poiBeacons = new ArrayList<>();
    private static HashMap<String, POI> poiHM = new HashMap<>();
    private OnPOIResultListener poiListener = null;

    // ----- Friends -----
    private static List<Friend> friendBeacons = new ArrayList<>();
    private static HashMap<String, Friend> friendHM = new HashMap<>();
    private OnFriendResultListener friendListener = null;

    // ----- Singleton class -----
    private static LocationManager instance;

    // Constructor
    public static LocationManager getInstance() {
        if(instance == null) {
            instance = new LocationManager();
        }
        return instance;
    }

    // LocationManager service
    // The caller class (i.e. MainActivity, etc.) should be able to start/stop service as wished.

    public void startService(Context context) {
        // TODO: startService methods
    }

    public void stopService(Context context) {
        // TODO: stopService methods
    }

    // TODO: Service callbacks methods

    // TODO: End

    // POI methods
    public void setOnPOIResultListener(OnPOIResultListener listener) {
        // Caller method can update through the listener when this manager class sent updates
        // this.poiListener.OnRetrieved method would be invoked after service successfully acquired new location information
        this.poiListener = listener;
    }

    public void setOnFriendResultListener(OnFriendResultListener listener) {
        // Caller method can update through the listener when this manager class sent updates
        // this.friendListener.OnRetrieved method would be invoked after service successfully acquired new location information
        this.friendListener = listener;
    }

}

