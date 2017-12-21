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
import java.util.Random;
import java.util.UUID;

import cuhk.cse.cmsc5736project.interfaces.OnFriendListChangeListener;
import cuhk.cse.cmsc5736project.interfaces.OnFriendResultListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIListChangeListener;
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
    private OnPOIListChangeListener poiChangedListener = null;

    // ----- Friends -----
    private static HashMap<String, Friend> friendHM = new HashMap<>();
    private OnFriendListChangeListener friendChangedListener = null;

    private static HashMap<String, Friend> notFriendHM = new HashMap<>();
    private OnFriendListChangeListener notFriendChangedListener = null;

    // ----- Singleton class -----
    private static LocationManager instance;

    // ----- URLs -----
    //218.191.44.226
    private static String ROOT_URL = "http://192.168.0.103/cmsc5736_project";
    private static String GET_ALL_BEACON_DATA_URL = ROOT_URL + "/get_all_beacon_data.php";
    private static String GET_ALL_FRIEND_DATA_URL = ROOT_URL + "/get_all_user_friends.php";
    private static String GET_ALL_POI_DATA_URL = ROOT_URL + "/get_all_poi_data.php";
    private static String GET_ALL_NOT_FRIEND_DATA_URL = ROOT_URL + "/get_all_user_not_friend.php";


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





    // ----- POI methods -----
    public void getPOIDefinitions(Context context, final OnPOIResultListener initListener) {
        // Get POI definitions from server, and materialize for client upgrades to each approximation
        // ~= RSSIModel.updateModel method
        HashMap postData = new HashMap();
        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject resultJson = new JSONObject(s);
                    JSONArray poiArr = resultJson.getJSONArray("pois");
                    for (int i = 0; i < poiArr.length(); i++) {
                        // Transform raw result to object
                        JSONObject row = poiArr.getJSONObject(i);
                        POI poi = Utility.createPOIFromJsonObject(row);
                        String id = poi.getID();
                        // Put objects to accessing array/Hashmap
                        poiHM.put(id, poi);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<POI> poiList = new ArrayList<>(poiHM.values());
                initListener.onRetrieved(poiList);
            }
        });
        task.execute(GET_ALL_POI_DATA_URL);
    }

    public void updatePOIDefintion() {
        // TODO: Do update POI beacon info here!

        // Invoke callback method
        if (this.poiChangedListener !=  null) {
            this.poiChangedListener.onChanged();
        }
    }

    public void setOnPOIChangedListener(OnPOIListChangeListener listener) {
        // Caller method can update through the listener when this manager class sent updates
        // this.poiListener.OnRetrieved method would be invoked after service successfully acquired new location information
        this.poiChangedListener = listener;
    }




    // ----- Friend methods -----
    public void getFriendDefinitions(Context context, final OnFriendResultListener initListener) {
        // TODO: Get friend definitions from server/scanning nearby devices
        // For add new friend activity to scan all nearby devices
        // This is not related to the instance's friendHM, which stores user bookmarked Friends
        HashMap postData = new HashMap();
        String macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        postData.put("mac",macAddress);
        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject resultJson = new JSONObject(s);
                    JSONArray friendArr = resultJson.getJSONArray("not_friends");
                    for (int i = 0; i < friendArr.length(); i++) {
                        // Transform raw result to object
                        JSONObject row = friendArr.getJSONObject(i);
                        Friend friend = Utility.creatFriendFromJsonObject(row);
                        String id = friend.getMAC();
                        // Put objects to accessing array/Hashmap
                        notFriendHM.put(id, friend);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<Friend> notFriendList = new ArrayList<>(notFriendHM.values());
                initListener.onRetrieved(notFriendList);
            }
        });
        task.execute(GET_ALL_NOT_FRIEND_DATA_URL);
    }

    public void updateFriendDefintion() {
        // TODO: Do update friend beacon info here!

        // Invoke callback method
        if (this.friendChangedListener !=  null) {
            this.friendChangedListener.onChanged();
        }
    }

    public void getCurrentUserFriendList(Context context, final OnFriendResultListener initListener) {
        // Get list of user-stored friends for caller method
        // When this class updates the friend objects, the UI would be updated as well (through setOnFriendResultListener)
        HashMap postData = new HashMap();
        String macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        postData.put("mac",macAddress);
        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject resultJson = new JSONObject(s);
                    JSONArray friendArr = resultJson.getJSONArray("friends");
                    for (int i = 0; i < friendArr.length(); i++) {
                        // Transform raw result to object
                        JSONObject row = friendArr.getJSONObject(i);
                        Friend friend = Utility.creatFriendFromJsonObject(row);
                        String id = friend.getMAC();
                        // Put objects to accessing array/Hashmap
                        friendHM.put(id, friend);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<Friend> friendList = new ArrayList<>(friendHM.values());
                initListener.onRetrieved(friendList);
            }
        });
        task.execute(GET_ALL_FRIEND_DATA_URL);

        List<Friend> friendList = new ArrayList<>(friendHM.values());
        initListener.onRetrieved(friendList);
    }

    public void setOnFriendListChangeListener(OnFriendListChangeListener listener) {
        // On friend list changed
        // onAdd: After adding a new friend
        // onDelete: After deleting a friend
        // onChange: After getting updated friend info (e.g. beacon information)
        this.friendChangedListener = listener;
    }

    public void putFriend(Friend newFriend) {
        // Put a new friend to location manager service for tracking updates
        friendHM.put(newFriend.getMAC(), newFriend);

        // Invoke callback method
        if (this.friendChangedListener !=  null) {
            this.friendChangedListener.onAdded(newFriend);
        }
    }

    public void removeFriend(Friend toRemoveFriend) {
        // Remove a friend from location manager service
        friendHM.remove(toRemoveFriend.getMAC());

        // Invoke callback method
        if (this.friendChangedListener != null) {
            this.friendChangedListener.onDeleted(toRemoveFriend);
        }
    }




    // ----- Simulated methods -----
    public void getSimulatedPOIDefinitions(Context context, final OnPOIResultListener initListener) {
        poiHM = new HashMap<>();
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
                //POI poi = new POI("POI test", uuid);

                // Put objects to accessing array/Hashmap
                //poiHM.put(uuid, poi);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        List<POI> poiList = new ArrayList<>(poiHM.values());
        initListener.onRetrieved(poiList);
    }

    public void getSimulatedFriendDefinitions(Context context, final OnFriendResultListener initListener) {
        // For add new friend activity to scan all nearby devices
        // This is not related to the instance's friendHM, which stores user bookmarked Friends
        HashMap<String, Friend> friendHM = new HashMap<>();
        for (int i=0; i < 10; i++) {
            Beacon beacon = new Beacon();
            beacon.setUUID(UUID.randomUUID().toString());
            beacon.setMajor(1);
            beacon.setMinor(123);
            beacon.setRSSI(-i);

            String macAddr = Utility.randomMACAddress();

            Friend friend = new Friend(macAddr, "Friend " + i);
            friend.setBeacon(beacon);

            friendHM.put(macAddr, friend);
        }
        List<Friend> friendList = new ArrayList<>(friendHM.values());
        initListener.onRetrieved(friendList);
    }

    public void updateSimulatedFriendPositions() {
        // Simulated method for getting updated friend position

        // Update positions
        for(Friend friend : friendHM.values()) {
            friend.getBeacon().setRSSI( -1 + -1 * new Random().nextInt(10));
        }

        // Invoke callback method
        if (this.friendChangedListener !=  null) {
            this.friendChangedListener.onChanged();
        }
    }
}

