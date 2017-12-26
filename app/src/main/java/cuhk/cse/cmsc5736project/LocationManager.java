package cuhk.cse.cmsc5736project;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import cuhk.cse.cmsc5736project.interfaces.OnFriendListChangeListener;
import cuhk.cse.cmsc5736project.interfaces.OnFriendResultListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIListChangeListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIResultListener;
import cuhk.cse.cmsc5736project.models.Beacon;
import cuhk.cse.cmsc5736project.models.Friend;
import cuhk.cse.cmsc5736project.models.POI;
import cuhk.cse.cmsc5736project.models.RSSIModel;
import cuhk.cse.cmsc5736project.utils.Utility;

/**
 * Created by TCC on 12/18/2017.
 */

public class LocationManager {

    private Date lastScanningDate;

    //private List<Beacon> scanBeacon = new ArrayList<Beacon>();

    // ----- POI -----
    private static HashMap<String, POI> poiHM = new HashMap<>();
    private OnPOIListChangeListener poiChangedMapFragmentListener = null;
    private OnPOIListChangeListener poiChangedListener = null;

    // ----- Friends -----
    private static HashMap<String, Friend> friendHM = new HashMap<>();
    private OnFriendListChangeListener friendChangedMapFragmentListener = null;
    private OnFriendListChangeListener friendChangedListener = null;

    private static HashMap<String, Friend> notFriendHM = new HashMap<>();
    private OnFriendListChangeListener notFriendChangedListener = null;

    // ----- Singleton class -----
    private static LocationManager instance;

    // ----- URLs -----
    //218.191.44.226
    public static String ROOT_URL = "http://192.168.0.103/cmsc5736_project";
    private static String GET_ALL_BEACON_DATA_URL = ROOT_URL + "/get_all_beacon_data.php";
    private static String GET_ALL_POI_DATA_URL = ROOT_URL + "/get_all_poi_data.php";
    private static String GET_ALL_FRIEND_DATA_URL = ROOT_URL + "/get_all_user_friends.php";
    private static String GET_ALL_NOT_FRIEND_DATA_URL = ROOT_URL + "/get_all_user_not_friend.php";

    private static String USER_ADD_FRIEND_URL = ROOT_URL + "/user_add_friend.php";
    private static String USER_REMOVE_FRIEND_URL = ROOT_URL + "/user_remove_friend.php";

    private static String UPDATE_USER_DATA_URL = ROOT_URL + "/update_user_data.php";

    // Current device variables
    public static PointF userPos = new PointF(0, 0);
    public static String userName = "test-user";
    public static String userMAC = "Not-init-BLE-yet";

    // BLE service and states
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btLeScanne;
    private boolean isScanning = false;
    private Handler scanHandler = new Handler();

    // Friend location poller
    PeriodicExecutor friendPoller;

    // Constructor
    public static LocationManager getInstance() {
        if (instance == null) {
            instance = new LocationManager();
        }
        return instance;
    }

    static public String getUserMAC(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
    }

    public void iniUserData(Context context)
    {
        if(BluetoothAdapter.getDefaultAdapter() == null) {
            return;
        }

        //set default user data
        userName = BluetoothAdapter.getDefaultAdapter().getName();
        userMAC = this.getUserMAC(context);
    }
    // LocationManager service
    // The caller class (i.e. MainActivity, etc.) should be able to start/stop service as wished.
    public void startService(final Context context) {
        // init BLE; and check if bluetooth capability is enabled
        btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(btManager == null) {
            Toast.makeText(context, "This device does not support Bluetooth!", Toast.LENGTH_SHORT).show();
            return;
        }
        btAdapter = btManager.getAdapter();
        if(btAdapter == null) {
            Toast.makeText(context, "This device does not support Bluetooth!", Toast.LENGTH_SHORT).show();
            return;
        }
        btLeScanne = btAdapter.getBluetoothLeScanner();
        if(btLeScanne == null) {
            Toast.makeText(context, "This device does not support Bluetooth!", Toast.LENGTH_SHORT).show();
            return;
        }

        // init/update user server data
        updateUserData(context,null );

        //init/update RSSI-distance model
        RSSIModel.getInstance().updateModel(context);

        //start beacon scan
        if (!isScanning) {
            scanHandler.post(scanRunnable);
            Toast.makeText(context, "Scanning started!", Toast.LENGTH_SHORT);
        }

        // start friend poller
        friendPoller = new PeriodicExecutor(3000);
        friendPoller.execute(new Runnable() {
            @Override
            public void run() {
                //LocationManager.this.updateFriendDefintion(context);
                LocationManager.this.updateSimulatedFriendPositions();
            }
        });
    }

    public void stopService(Context context) {
        // TODO: stopService methods
        if (isScanning) {
            scanHandler.post(scanRunnable);
            Toast.makeText(context, "Scanning stopped!", Toast.LENGTH_SHORT);
        }
    }

    // TODO: Service callbacks methods
    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            if(!isScanning) {
                ScanFilter beaconFilter = new ScanFilter.Builder()
                        .build();

                ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
                filters.add(beaconFilter);

                ScanSettings settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                ;
                btLeScanne.startScan(filters, settings, mScanCallback);
                isScanning = true;
            }
            else {
                btLeScanne.stopScan(mScanCallback);
                isScanning = false;
            }
        }
    };


    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            byte[] scanRecord = result.getScanRecord().getBytes();

            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5) {
                if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                //Convert to hex String
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = Utility.getInstance().bytesToHex(uuidBytes);

                //UUID detection
                String uuid = hexString.substring(0, 8) + "-" +
                        hexString.substring(8, 12) + "-" +
                        hexString.substring(12, 16) + "-" +
                        hexString.substring(16, 20) + "-" +
                        hexString.substring(20, 32);
                final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
                final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

                /*
                Beacon beacon = new Beacon();
                beacon.setUUID(uuid);
                beacon.setMajor(major);
                beacon.setMinor(minor);
                beacon.setRSSI(result.getRssi());

                boolean isUpdated = false;
                for (Beacon beaconItem : scanBeacon) {
                    if (beacon.isSameBeacon(beaconItem)) {
                        beaconItem.setRSSI(beacon.getRSSI());
                        isUpdated = true;
                        break;
                    }
                }
                if (!isUpdated) {
                    scanBeacon.add(beacon);
                }
                */

                // Check beacon entry in POI Hashmap
                POI targetPOI = poiHM.get(uuid);
                if (targetPOI != null) {
                    targetPOI.getBeacon().setRSSI(result.getRssi());
                    //Log.i("targetPOI", " " + targetPOI.toString());
                    //Log.i("targetPOI RSSI", " " + result.getRssi());
                }

                // If threshold passed, trigger POI change listener
                Date nowDate = new Date();
                int scanning_threshold = 1;
                if (lastScanningDate == null || (nowDate.getTime() - lastScanningDate.getTime()) / 1000 > scanning_threshold) {
                    //LocationManager.this.updatePOIDefintion();
                    poiChangedListener.onChanged();
                    lastScanningDate = new Date();
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d("", "onBatchScanResults: " + results.size() + " results");
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w("", "LE Scan Failed: " + errorCode);
        }
    };
    // TODO: End


    // ----- User methods -----
    // Insert user item to users table if not exists, update the corresponding item if it is exists
    public void updateUserData(Context context, POI nearPOI) {
        HashMap postData = new HashMap();
        postData.put("mac", userMAC);
        postData.put("name", userName);
        postData.put("position_x", Float.toString(userPos.x));
        postData.put("position_y", Float.toString(userPos.y));
        if (nearPOI!= null) {
            postData.put("near_POI", nearPOI.getID());
        }
        //last_update use server time

        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject resultJson = new JSONObject(s);
                    if (resultJson.getBoolean("is_succeed")) {
                        Log.d("LocationManager", "updateUserData succeed ");
                    } else {
                        Log.d("LocationManager", "updateUserData failed ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute(UPDATE_USER_DATA_URL);
    }

    // ----- POI methods -----
    public void initPOIDefinitions(Context context, final OnPOIResultListener initListener) {
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
                        //Log.i("getPOIDefinitions", poi.toString());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(initListener != null) {
                    initListener.onRetrieved(LocationManager.this.getPOIList());
                }
            }
        });
        task.execute(GET_ALL_POI_DATA_URL);
    }

    public void getPOIDefinitions(Context context, final OnPOIResultListener initListener) {
        // Already initialized. returning old list
        initListener.onRetrieved(LocationManager.this.getPOIList());
    }

    public List<POI> getPOIList() {
        // Translate updated POI Hashmap to list
        List<POI> poiList = new ArrayList<>(poiHM.values());
        return poiList;
    }

    public void updatePOIDefintion(Context context) {
        // TODO: Do update POI beacon info here!
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
                        //Log.i("getPOIDefinitions", poi.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Invoke callback method
                if (poiChangedListener != null) {
                    poiChangedListener.onChanged();
                }
                if (poiChangedMapFragmentListener != null) {
                    poiChangedMapFragmentListener.onChanged();
                }
            }
        });

        task.execute(GET_ALL_POI_DATA_URL);
    }

    public void setOnPOIChangedListener(OnPOIListChangeListener listener) {
        // Caller method can update through the listener when this manager class sent updates
        // this.poiListener.OnRetrieved method would be invoked after service successfully acquired new location information
        this.poiChangedListener = listener;
    }
    public void setOnPOIChangedMapFragmentListener(OnPOIListChangeListener listener) {
        // Caller method can update through the listener when this manager class sent updates
        // this.poiListener.OnRetrieved method would be invoked after service successfully acquired new location information
        this.poiChangedMapFragmentListener = listener;
    }


    // ----- Friend methods -----
    public void getFriendDefinitions(Context context, final OnFriendResultListener initListener) {
        // TODO: Get friend definitions from server/scanning nearby devices
        // For add new friend activity to scan all nearby devices
        // This is not related to the instance's friendHM, which stores user bookmarked Friends
        HashMap postData = new HashMap();
        postData.put("mac", userMAC);
        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject resultJson = new JSONObject(s);
                    JSONArray friendArr = resultJson.getJSONArray("not_friends");
                    for (int i = 0; i < friendArr.length(); i++) {
                        // Transform raw result to object
                        JSONObject row = friendArr.getJSONObject(i);
                        Friend friend = Utility.createNotFriendFromJsonObject(row);
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

    public void updateFriendDefintion(Context context) {
        // TODO: Do update friend beacon info here!
        HashMap postData = new HashMap();
        postData.put("mac", userMAC);
        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject resultJson = new JSONObject(s);
                    JSONArray friendArr = resultJson.getJSONArray("not_friends");
                    for (int i = 0; i < friendArr.length(); i++) {
                        // Transform raw result to object
                        JSONObject row = friendArr.getJSONObject(i);
                        Friend friend = Utility.createNotFriendFromJsonObject(row);
                        String id = friend.getMAC();
                        // Put objects to accessing array/Hashmap
                        notFriendHM.put(id, friend);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<Friend> notFriendList = new ArrayList<>(notFriendHM.values());
                // Invoke callback method
                if (friendChangedListener != null) {
                    friendChangedListener.onChanged();
                }
                if (friendChangedMapFragmentListener != null) {
                    friendChangedMapFragmentListener.onChanged();
                }
            }
        });
        task.execute(UPDATE_USER_DATA_URL);
    }

    public void initCurrentUserFriendList(Context context, final OnFriendResultListener initListener) {
        // Get list of user-stored friends for caller method
        // When this class updates the friend objects, the UI would be updated as well (through setOnFriendResultListener)
        HashMap postData = new HashMap();
        postData.put("mac", userMAC);
        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject resultJson = new JSONObject(s);
                    JSONArray friendArr = resultJson.getJSONArray("friends");
                    for (int i = 0; i < friendArr.length(); i++) {
                        // Transform raw result to object
                        JSONObject row = friendArr.getJSONObject(i);
                        Friend friend = Utility.createFriendFromJsonObject(row, poiHM);
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
    }

    public void getCurrentUserFriendList(Context context, final OnFriendResultListener initListener) {
        List<Friend> friendList = new ArrayList<>(friendHM.values());
        initListener.onRetrieved(friendList);
    }

    public void setOnFriendListChangeListener(OnFriendListChangeListener listener) {
        // On friend list changed
        // onAdd: After adding a new friend
        // onDelete: After deleting a friend
        // onChange: After getting updated friend info (e.g. beacon information)
        if (listener != null) {
            this.friendChangedListener = listener;
        }
    }

    public void setOnFriendListChangeMapFragmentListener(OnFriendListChangeListener listener) {
        // On friend list changed
        // onAdd: After adding a new friend
        // onDelete: After deleting a friend
        // onChange: After getting updated friend info (e.g. beacon information)
        if (listener != null) {
            this.friendChangedMapFragmentListener = listener;
        }
    }

    public void putFriend(Context context, Friend newFriend) {
        // Put a new friend to location manager service for tracking updates
        friendHM.put(newFriend.getMAC(), newFriend);
        notFriendHM.remove(newFriend.getMAC());
        addFriendToServer(newFriend, context);

        // Invoke callback method
        if (this.friendChangedListener != null) {
            this.friendChangedListener.onAdded(newFriend);
        }
        if (this.friendChangedMapFragmentListener != null) {
            this.friendChangedMapFragmentListener.onAdded(newFriend);
        }
    }

    private void addFriendToServer(Friend newFriend, Context context) {
        // Get POI definitions from server, and materialize for client upgrades to each approximation
        // ~= RSSIModel.updateModel method
        HashMap postData = new HashMap();
        postData.put("mac", userMAC);
        postData.put("friend_mac", newFriend.getMAC());
        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject resultJson = new JSONObject(s);
                    if (resultJson.getBoolean("is_succeed")) {
                        Log.d("LocationManager", "addFriendToServer succeed ");
                    } else {
                        Log.d("LocationManager", "addFriendToServer failed ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute(USER_ADD_FRIEND_URL);
    }

    public void removeFriend(Context context, Friend toRemoveFriend) {
        // Remove a friend from location manager service
        friendHM.remove(toRemoveFriend.getMAC());
        notFriendHM.put(toRemoveFriend.getMAC(),toRemoveFriend);
        removeFriendFromServer(toRemoveFriend, context);

        // Invoke callback method
        if (this.friendChangedListener != null) {
            this.friendChangedListener.onDeleted(toRemoveFriend);
        }
        if (this.friendChangedMapFragmentListener != null) {
            this.friendChangedMapFragmentListener.onDeleted(toRemoveFriend);
        }
    }

    private void removeFriendFromServer(Friend toRemoveFriend, Context context) {
        // Get POI definitions from server, and materialize for client upgrades to each approximation
        // ~= RSSIModel.updateModel method
        HashMap postData = new HashMap();
        postData.put("mac", userMAC);
        postData.put("friend_mac", toRemoveFriend.getMAC());

        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData, new AsyncResponse() {
            @Override
            public void processFinish(String s) {
                try {
                    JSONObject resultJson = new JSONObject(s);
                    if (resultJson.getBoolean("is_succeed")) {
                        Log.d("LocationManager", "removeFriendFromServer succeed ");
                    } else {
                        Log.d("LocationManager", "removeFriendFromServer failed ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        task.execute(USER_REMOVE_FRIEND_URL);
    }


    // ----- Simulated methods -----
    public void getSimulatedPOIDefinitions(Context context, final OnPOIResultListener initListener) {
        poiHM = new HashMap<>();
        // Get POI definitions from server, and materialize for client upgrades to each approximation
        // ~= RSSIModel.updateModel method
        /*
        String poiResult = "{\n" +
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
                POI poi = new POI(uuid, "POI " + i, "Description!");
                poi.setBeacon(beacon);
                poi.setPosition(beacon.getPos_x(), beacon.getPos_y());

                // Put objects to accessing array/Hashmap
                poiHM.put(uuid, poi);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        for(int i=0; i < 5; i++) {
            Beacon beacon = new Beacon();
            beacon.setUUID(UUID.randomUUID().toString());
            beacon.setPos(i * 5, i * 10);
            beacon.setRSSI(-30 - new Random().nextInt(70));

            double n1 = -30 - new Random().nextInt(70);
            double n2 = n1 + 2;
            double n3 = n2 + 2;
            double n4 = n3 + 6;
            double n = (Math.log(n1/n2) / Math.log(0.5/1) + Math.log(n3/n2) / Math.log(2/1) + Math.log(n4/n2) / Math.log(4/1)) / 3;
            beacon.setPathLossExponent(n);
            beacon.setOneMeterPower(n2);

            POI poi = new POI(beacon.getUUID(), "Toilet " + i, "Description " + i);
            poi.setBeacon(beacon);
            poi.setPosition(beacon.getPos_x(), beacon.getPos_y());

            // Put objects to accessing array/Hashmap
            poiHM.put(beacon.getUUID(), poi);
        }

        List<POI> poiList = new ArrayList<>(poiHM.values());
        initListener.onRetrieved(poiList);
    }

    public void getSimulatedFriendDefinitions(Context context, final OnFriendResultListener initListener) {
        // For add new friend activity to scan all nearby devices
        // This is not related to the instance's friendHM, which stores user bookmarked Friends
        HashMap<String, Friend> friendHM = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String macAddr = Utility.randomMACAddress();
            Friend friend = new Friend(macAddr, "Friend " + i);

            friendHM.put(macAddr, friend);
        }
        List<Friend> friendList = new ArrayList<>(friendHM.values());
        initListener.onRetrieved(friendList);
    }

    public void getSimulatedCurrentUserFriendList(Context context, final OnFriendResultListener initListener) {
        // For friend fragment to scan all nearby devices
        friendHM = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            String macAddr = Utility.randomMACAddress();
            Friend friend = new Friend(macAddr, "Friend " + i);

            friendHM.put(macAddr, friend);
        }

        List<Friend> friendList = new ArrayList<>(friendHM.values());
        initListener.onRetrieved(friendList);
    }

    public void updateSimulatedFriendPositions() {
        // Simulated method for getting updated friend position
        List<POI> poiList = new ArrayList<>(poiHM.values());
        List<Friend> friendList = new ArrayList<>(friendHM.values());

        if(poiList.size() == 0 || friendList.size() == 0) {
            return;
        }

        // Update positions
        for (int i=0; i < 5; i++) {
            Friend friend = friendList.get(i);
            POI poi = poiList.get(i);

            friend.setNearPOI(poi);
            friend.setLastUpdated(new Date());
        }

        // Invoke callback method
        if (this.friendChangedListener != null) {
            this.friendChangedListener.onChanged();
        }
    }

    public void simulateBluetoothPOIScanning() {
        for(POI poi : poiHM.values()) {
            poi.getBeacon().setRSSI(-30 - new Random().nextInt(70));
        }

        // Invoke callback method
        if (this.poiChangedListener != null) {
            this.poiChangedListener.onChanged();
        }
    }

}