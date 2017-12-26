package cuhk.cse.cmsc5736project.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

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

    public Pin(Context context, PointF position, int drawable, String description){
        this(context, position, drawable);
        this.description = description;
    }

    public Pin(Context context, PointF position, int drawable){
        this.id = next_id;
        next_id ++;

        this.position = position;
        this.drawable = drawable;
        initialise(context);
    }

    public Pin(Context context, POI poi, int drawable){
        this.id = next_id;
        next_id ++;

        this.poi = poi;
        this.drawable = drawable;
        this.description = poi.getDescription();
        initialise(context);
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

    public Bitmap getBitmap(){
        return pin;
    }

    private void initialise(Context context) {
        float density = context.getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_marker);
        float w = (density/420f) * pin.getWidth();
        float h = (density/420f) * pin.getHeight();
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);

        //position.x += 70;
        //position.y += 500;

    }

}
