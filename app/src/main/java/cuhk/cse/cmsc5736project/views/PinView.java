package cuhk.cse.cmsc5736project.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.models.Pin;

public class PinView extends SubsamplingScaleImageView {
    private final Paint paint = new Paint();
    private final PointF vPin = new PointF();
    //private PointF sPin;
    //private Bitmap pin;
    private List<Pin> pinList = new ArrayList<Pin>();

    public PinView(Context context) {
        this(context, null);
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        //initialise();
    }

    public void addPinList(List<Pin> pinList) {
        //this.sPin = new PointF(10 + 70,0 + 500);

        for (Pin pin : pinList) {
            // modify pin location before adding
            PointF tarPin = new PointF();

            viewToSourceCoord(pin.getPin(), tarPin);
            Log.i("pin view", "changing pin: " + pin.getPin().x + ", " + pin.getPin().y + ", " + tarPin.x + ", " + tarPin.y + ", " + getScale());
            pin.setPin(tarPin);
            this.pinList.add(pin);
        }
        //this.pinList.addAll(pinList);

        //initialise();
        invalidate();
    }

    public void addPin(Pin pin) {

        // modify pin location before adding
        PointF tarPin = new PointF();

        viewToSourceCoord(pin.getPin(), tarPin);
        Log.i("pin view", "changing pin: " + pin.getPin().x + ", " + pin.getPin().y + ", " + tarPin.x + ", " + tarPin.y + ", " + getScale());
        pin.setPin(tarPin);
        this.pinList.add(pin);

        //initialise();
        invalidate();
    }

    void toPinViewCoord(Pin pin){
        pin.setPin(viewToSourceCoord(pin.getPin()));
        //return pin;
    }

/*    PointF viewToSourceCoord(PointF sPos){
        PointF afterTrans = new PointF();
        PointF translate = new PointF();
        PointF targetPin = new PointF();
        float scale = getScale();
        sourceToViewCoord(sPos, afterTrans);
        float sx = sPos.x;
        float sy = sPos.y;
        translate.set(afterTrans.x - sx * scale, afterTrans.y - sy * scale);
        targetPin.set((sx - translate.x), (sy - translate.y));
        if (scale!=0f) targetPin.set(targetPin.x/scale, targetPin.y/scale);

        return targetPin;
    }*/

/*    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.map_marker);
        float w = (density/420f) * pin.getWidth();
        float h = (density/420f) * pin.getHeight();
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        paint.setAntiAlias(true);

        //if (sPin != null && pin != null) {
        if (pinList!=null)
        for (Pin pin : pinList){
            sourceToViewCoord(pin.getPin(), vPin);
            float vX = vPin.x - (pin.getBitmap().getWidth()/2);
            float vY = vPin.y - pin.getBitmap().getHeight();
            canvas.drawBitmap(pin.getBitmap(), vX, vY, paint);

            if (!pin.getDescription().isEmpty()) {
                paint.setColor(Color.BLACK);
                paint.setTextSize(50);
                //float vXText = vPin.x - (pin.getBitmap().getWidth() / 2);
                //float vYText = vPin.y - pin.getBitmap().getHeight() - 20;
                canvas.drawText(pin.getDescription(), vX, vY, paint);
            }

            //Log.i("pin view", "onDraw: " + vX + ", " + vY + ", " + pin.getPin().x + ", " + pin.getPin().y + ", " + getScale());
        }

    }

}
