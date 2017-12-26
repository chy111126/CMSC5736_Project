package cuhk.cse.cmsc5736project.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cuhk.cse.cmsc5736project.R;

/**
 * Created by Charmy on 24/12/2017.
 */

public class Pin {

    private static int next_id = 0;

    private int id;
    private POI poi;
    private PointF position;
    private int drawable;
    private Bitmap pin;
    private String description = "";
    private float scale = 1;
    private double distance = -1;
    private static double maxDistance = 0;

    private List<Friend> friendList = new ArrayList<>();

    public Pin(Context context, PointF position, int drawable){
        this.id = next_id;
        next_id ++;

        this.position = position;
        this.drawable = drawable;
        initialise(context);
    }

    public Pin(Context context, PointF position, int drawable, String description){
        this(context, position, drawable);
        this.description = description;
    }

    public Pin(Context context, POI poi, int drawable){
        this(context, poi.getPosition(), drawable);
        this.poi = poi;
        if (poi!=null){
            setDescription(poi.getName());
            if(poi.getBeacon()!=null) {
                setDistance(poi.getBeacon().getProximity());
                if (getDistance() > maxDistance) maxDistance = getDistance();
            }
        }
    }

    public PointF getPin() {
        return position;
    }

    public void setPin(PointF position) {
        this.position = position;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getScale() {
        return scale;
    }

    public void setScale() {
        Log.i("Pin View", "Setting scale for distance " + distance + " for " + getDescription() + " with max dist " + maxDistance);
        if (distance>=0) {
            if (maxDistance > 0) {
                this.scale = (float) ((1 - 0.5f * (distance/maxDistance))) ;
                Log.i("Pin View", "Setted scale for distance " + distance + " as " + getScale());
                this.scale = Math.min(Math.max(this.scale, 0.5f),1.0f);
                pin = Bitmap.createScaledBitmap(pin, (int) (pin.getWidth() * scale), (int) (pin.getHeight() * scale), true);
            }
            setDescription("~" + getDistance() + "m");
        }
    }

    public float getTextSize(){
        return 70*getScale();
    }

    public Bitmap getBitmap(){
        return pin;
    }

    private void initialise(Context context) {
        float density = context.getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(context.getResources(), getDrawable());
        float w = (density/420f) * pin.getWidth();
        float h = (density/420f) * pin.getHeight();
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);

        //position.x += 70;
        //position.y += 500;

    }

    public void addFriend(Friend friend){
        friendList.add(friend);
    }

    public void removeFriend(Friend friend){
        friendList.remove(friend);
    }

    public List<Friend> getFriendList(){
        return friendList;
    }
}
