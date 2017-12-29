package cuhk.cse.cmsc5736project.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
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
    private static double maxDistance = 3;

    private final Paint iconPaint;
    private ColorMatrix colorMatrix;
    private boolean showPin = false;

    private List<Friend> friendList = new ArrayList<>();

    public Pin(Context context, PointF position, int drawable){
        this.id = next_id;
        next_id ++;

        this.position = position;
        this.drawable = drawable;
        this.colorMatrix = new ColorMatrix();
        this.colorMatrix.setSaturation(0);
        this.iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        refreshIconPaintFilter();
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
                setDistance(poi.getBeacon().getDistance());
                maxDistance = 5;
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

    public ColorMatrix getColorMatrix(){
        return colorMatrix;
    }

    public boolean isShowPin() {
        return showPin;
    }

    public void setShowPin(boolean showPin) {
        this.showPin = showPin;
    }

    public Paint getIconPaint() {
        return iconPaint;
    }

    public void refreshIconPaintFilter(){
        this.iconPaint.setColorFilter(new ColorMatrixColorFilter(this.getColorMatrix()));
    }

    public String getDistanceDescription(){
        return ("~" + getDistance() + "m");
    }

    public double getDistance() {
        return ((poi==null||poi.getBeacon()==null)? distance: poi.getBeacon().getDistance());
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getScale() {
        return scale;
    }

    public void setScale() {
        Log.i("Pin View", "Setting scale for " + getDescription() + " with dist=" + getDistance());
        if (this.distance>=0) {
            if (maxDistance > 0) {
                float oldScale = this.scale;
                this.scale = (float) ((1 - (getDistance()/maxDistance))) ;
                Log.i("Pin View", "Setted scale for distance " + this.distance + " as " + getScale());
                this.scale = Math.min(Math.max(this.scale, 0.1f),1.0f);
                int w = (int)(pin.getWidth() / oldScale * this.scale);
                int h = (int)(pin.getHeight() / oldScale * this.scale);
                //if (w>0 && h>0) pin = Bitmap.createScaledBitmap(pin, (int) w, (int) h, true);
                this.colorMatrix.setSaturation(this.scale);
                refreshIconPaintFilter();
            }
            //setDescription("~" + getDistance() + "m");
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
