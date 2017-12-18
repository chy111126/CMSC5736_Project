package cuhk.cse.cmsc5736project;

import android.content.Context;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

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

    // ----- URLs -----
    private static String ROOT_URL = "http://218.191.44.226/cmsc5736_project";
    private static String GET_ALL_BEACON_DATA_URL = ROOT_URL + "/get_all_beacon_data.php";

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
    public void getPOIDefinitions(Context context, final OnPOIResultListener initListener) {
        // Get POI definitions from server, and materialize for client upgrades to each approximation
        // ~= RSSIModel.updateModel method
        HashMap postData = new HashMap();
        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                // TODO: process response to List of model objects

                initListener.onRetrieved(poiBeacons);
            }
        });
        task.execute(GET_ALL_BEACON_DATA_URL);
    }

    public void setOnPOIResultListener(OnPOIResultListener listener) {
        // Caller method can update through the listener when this manager class sent updates
        // this.poiListener.OnRetrieved method would be invoked after service successfully acquired new location information
        this.poiListener = listener;
    }

    // Friend methods
    public void getFriendDefinitions() {
        // TODO: Get friend definitions from server, and materialize for client upgrades to each approximation
    }

    public void setOnFriendResultListener(OnFriendResultListener listener) {
        // Caller method can update through the listener when this manager class sent updates
        // this.friendListener.OnRetrieved method would be invoked after service successfully acquired new location information
        this.friendListener = listener;
    }

}

