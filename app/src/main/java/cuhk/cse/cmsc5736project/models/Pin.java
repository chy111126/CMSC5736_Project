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

    private PointF position;
    private int drawable;
    private Bitmap pin;

    public Pin(Context context, PointF position, int drawable){
        this.position = position;
        this.drawable = drawable;
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

    public Bitmap getBitmap(){
        return pin;
    }

    private void initialise(Context context) {
        float density = context.getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_marker);
        float w = (density/420f) * pin.getWidth();
        float h = (density/420f) * pin.getHeight();
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);
    }

}
