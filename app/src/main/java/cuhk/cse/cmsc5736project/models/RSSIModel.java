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

import cuhk.cse.cmsc5736project.MainActivity;

/**
 * Created by alexchung on 16/12/2017.
 */

public class RSSIModel implements AsyncResponse {

    static public List<Beacon> beaconList =new ArrayList<Beacon>();
    static private RSSIModel instance;


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
        task.execute("http://"+ MainActivity.domain+"/cmsc5736_project/get_all_beacon_data.php");

        return true;
    }

    @Override
    public void processFinish(String s) {

        JSONObject mainObject = null;
        beaconList.clear();
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
                int n1 = beacon.getInt("rssi_half_m_signal");
                int n2 = beacon.getInt("rssi_one_m_signal");
                int n3 = beacon.getInt("rssi_two_m_signal");
                int n4 = beacon.getInt("rssi_four_m_signal");
                double n = (Math.log(n1/n2) / Math.log(0.5/1) + Math.log(n3/n2) / Math.log(2/1) + Math.log(n4/n2) / Math.log(4/1)) / 3;
                b.setPathLossExponent(n);
                b.setOneMeterPower(n2);
                b.setPos(pos_x,pos_y);
                beaconList.add(b);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
