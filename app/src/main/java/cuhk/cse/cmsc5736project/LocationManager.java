package cuhk.cse.cmsc5736project;

import android.content.Context;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cuhk.cse.cmsc5736project.interfaces.OnFriendResultListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIResultListener;
import cuhk.cse.cmsc5736project.models.Beacon;
import cuhk.cse.cmsc5736project.models.Friend;
import cuhk.cse.cmsc5736project.models.POI;
import cuhk.cse.cmsc5736project.utils.Utility;

/**
 * Created by TCC on 12/18/2017.
 */

public class LocationManager {

    // ----- POI -----
    private static HashMap<String, POI> poiHM = new HashMap<>();
    private OnPOIResultListener poiListener = null;

    // ----- Friends -----
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

                List<POI> poiList = new ArrayList<>(poiHM.values());
                initListener.onRetrieved(poiList);
            }
        });
        task.execute(GET_ALL_BEACON_DATA_URL);
    }

    public void getSimulatedPOIDefinitions(Context context, final OnPOIResultListener initListener) {
        // Get POI definitions from server, and materialize for client upgrades to each approximation
        // ~= RSSIModel.updateModel method
        String poiResult =  "{\n" +
                            "  \"beacons\": [\n" +
                            "    {\n" +
                            "      \"uuid_major_minor\": \"E3A513C7-EAB1-4988-AA99-C2C5145437E2_1_5891\",\n" +
                            "      \"uuid\": \"E3A513C7-EAB1-4988-AA99-C2C5145437E2\",\n" +
                            "      \"major\": \"1\",\n" +
                            "      \"minor\": \"5891\",\n" +
                            "      \"position_x\": \"5\",\n" +
                            "      \"position_y\": \"60\",\n" +
                            "      \"rssi_half_m_signal\": \"-60\",\n" +
                            "      \"rssi_one_m_signal\": \"-79\",\n" +
                            "      \"rssi_two_m_signal\": \"-89\",\n" +
                            "      \"rssi_four_m_signal\": \"-93\"\n" +
                            "    },\n" +
                            "    {\n" +
                            "      \"uuid_major_minor\": \"B5B182C7-EAB1-4988-AA99-B5C1517008D9_1_63496\",\n" +
                            "      \"uuid\": \"B5B182C7-EAB1-4988-AA99-B5C1517008D9\",\n" +
                            "      \"major\": \"1\",\n" +
                            "      \"minor\": \"63496\",\n" +
                            "      \"position_x\": \"85\",\n" +
                            "      \"position_y\": \"60\",\n" +
                            "      \"rssi_half_m_signal\": \"-68\",\n" +
                            "      \"rssi_one_m_signal\": \"-69\",\n" +
                            "      \"rssi_two_m_signal\": \"-66\",\n" +
                            "      \"rssi_four_m_signal\": \"-66\"\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}";
        try {
            JSONObject resultJson = new JSONObject(poiResult);
            JSONArray beaconsArr = resultJson.getJSONArray("beacons");
            for (int i = 0; i < beaconsArr.length(); i++) {
                // Transform raw result to object
                JSONObject row = beaconsArr.getJSONObject(i);
                Beacon beacon = Utility.createBeaconFromJsonObject(row);
                String uuid = beacon.getUUID();
                POI poi = new POI("POI test", uuid);

                // Put objects to accessing array/Hashmap
                poiHM.put(uuid, poi);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<POI> poiList = new ArrayList<>(poiHM.values());
        initListener.onRetrieved(poiList);
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

