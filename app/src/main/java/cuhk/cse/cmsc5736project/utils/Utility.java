package cuhk.cse.cmsc5736project.utils;

import android.graphics.Color;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import cuhk.cse.cmsc5736project.models.Beacon;
import cuhk.cse.cmsc5736project.models.Friend;
import cuhk.cse.cmsc5736project.models.POI;

/**
 * Created by alexchung on 16/12/2017.
 */

public class Utility {
    static private Utility instance;

    public static Utility getInstance() {
        if(instance == null) {
            instance = new Utility();
        }
        return instance;
    }

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int getLighterColor(int color) {
        // Lighter color = 10% of given color hex
        return Color.argb(10,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    public static int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

    public static Friend createNotFriendFromJsonObject(JSONObject jsonObj) {
        try {
            String mac = jsonObj.getString("mac");
            String name = jsonObj.getString("name");
            Friend friend = new Friend(mac,name);

            return friend;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Friend createFriendFromJsonObject(JSONObject jsonObj, HashMap<String, POI> poiHM) {
        try {
            String mac = jsonObj.getString("mac");
            String name = jsonObj.getString("name");

            POI poi = null;
            if(jsonObj.has("near_POI_ID")) {
                String nearPOIID = Integer.toString(jsonObj.getInt("near_POI_ID"));
                String nearPOIName = jsonObj.getString("near_POI_name");
                String nearPOIUUID = jsonObj.getString("near_POI_UUID");
                String nearPOIDescription = jsonObj.getString("near_POI_description");

                if(poiHM.get(nearPOIUUID) != null) {
                    //poi = new POI(nearPOIID, nearPOIName, nearPOIDescription);
                    poi = poiHM.get(nearPOIUUID);
                }
            }

            String updateTimeString = jsonObj.getString("update_time");
            Date updateTime =(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(updateTimeString);

            Friend friend = new Friend(mac,name);
            friend.setLastUpdated(updateTime);
            friend.setNearPOI(poi);

            return friend;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static POI createPOIFromJsonObject(JSONObject jsonObj) {
        try {
            //String id =jsonObj.getString("id");
            Log.i("createPOIFromJsonObject", jsonObj.toString());
            String id =jsonObj.getString("id");
            String name =jsonObj.getString("name");
            String description =jsonObj.getString("description");

            POI poi = new POI(id,name,description);
            Beacon beacon = new Beacon();
            beacon.setUUID(jsonObj.getString("uuid"));
            beacon.setMajor(jsonObj.getInt("major"));
            beacon.setMinor(jsonObj.getInt("minor"));
            beacon.setPos(jsonObj.getDouble("position_x"),jsonObj.getDouble("position_y"));

            double n1 = jsonObj.getDouble("rssi_half_m_signal");
            double n2 = jsonObj.getDouble("rssi_one_m_signal");
            double n3 = jsonObj.getDouble("rssi_two_m_signal");
            double n4 = jsonObj.getDouble("rssi_four_m_signal");
            double n = (Math.log(n1/n2) / Math.log(0.5/1) + Math.log(n3/n2) / Math.log(2/1) + Math.log(n4/n2) / Math.log(4/1)) / 3;
            beacon.setPathLossExponent(n);
            beacon.setOneMeterPower(n2);

            poi.setBeacon(beacon);
            return poi;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String randomMACAddress(){
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte)(macAddr[0] & (byte)254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);
        for(byte b : macAddr){

            if(sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }


        return sb.toString();
    }


}
