package cuhk.cse.cmsc5736project.models;

import android.content.Context;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cuhk.cse.cmsc5736project.LocationManager;
import cuhk.cse.cmsc5736project.MainActivity;

/**
 * Created by alexchung on 16/12/2017.
 */

public class RSSIModel implements AsyncResponse {

    /*
    RSSIModel for beacon setup activity
     */

    private static HashMap<String,Beacon> beaconHM = new HashMap<>();
    private static RSSIModel instance;


    public static RSSIModel getInstance() {
        if(instance == null) {
            instance = new RSSIModel();
        }
        return instance;
    }

    public boolean updateModel(Context context)
    {
        HashMap postData = new HashMap();
        PostResponseAsyncTask task = new PostResponseAsyncTask(context, postData,this);
        task.execute(LocationManager.ROOT_URL+"/get_all_beacon_data.php");

        return true;
    }

    @Override
    public void processFinish(String s) {

        JSONObject mainObject = null;
        beaconHM.clear();
        try {
            mainObject = new JSONObject(s);
            JSONArray beacons = mainObject.getJSONArray("beacons");

            for (int i = 0; i < beacons.length(); i++) {
                JSONObject beacon = beacons.getJSONObject(i);
                Beacon b=new Beacon();
                b.setUUID(beacon.getString("uuid"));
                b.setMajor(beacon.getInt("major"));
                b.setMinor(beacon.getInt("minor"));

                double pos_x = beacon.getDouble("position_x");
                double pos_y = beacon.getDouble("position_y");
                double n1 = beacon.getDouble("rssi_half_m_signal");
                double n2 = beacon.getDouble("rssi_one_m_signal");
                double n3 = beacon.getDouble("rssi_two_m_signal");
                double n4 = beacon.getDouble("rssi_four_m_signal");
                double n = (Math.log(n1/n2) / Math.log(0.5/1) + Math.log(n3/n2) / Math.log(2/1) + Math.log(n4/n2) / Math.log(4/1)) / 3;
                b.setPathLossExponent(n);
                b.setOneMeterPower(n2);
                b.setPos(pos_x,pos_y);
                String uuid_major_minor =beacon.getString("uuid_major_minor");
                beaconHM.put(uuid_major_minor,b);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
