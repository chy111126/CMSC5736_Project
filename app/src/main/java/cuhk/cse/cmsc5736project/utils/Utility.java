package cuhk.cse.cmsc5736project.utils;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static Beacon createBeaconFromJsonObject(JSONObject jsonObj) {
        try {
            Beacon beacon = new Beacon();
            beacon.setUUID(jsonObj.getString("uuid"));
            beacon.setMajor(jsonObj.getInt("major"));
            beacon.setMinor(jsonObj.getInt("major"));
            beacon.setPos(jsonObj.getInt("position_x"), jsonObj.getInt("position_y"));

            // TODO: For RSSI, need some custom logic to reflect proximity better
            beacon.setRSSI(jsonObj.getInt("rssi_two_m_signal"));

            return beacon;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Friend creatFriendFromJsonObject(JSONObject jsonObj) {
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
    public static POI createPOIFromJsonObject(JSONObject jsonObj) {
        try {
            String id =jsonObj.getString("id");
            String name =jsonObj.getString("name");
            String description =jsonObj.getString("description");

            POI poi = new POI(id,name,description);
            Beacon beacon = new Beacon();
            beacon.setUUID(jsonObj.getString("uuid"));
            beacon.setMajor(jsonObj.getInt("major"));
            beacon.setMinor(jsonObj.getInt("minor"));
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
