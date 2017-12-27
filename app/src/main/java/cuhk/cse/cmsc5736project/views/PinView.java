package cuhk.cse.cmsc5736project.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.icu.util.Freezable;
import android.util.AttributeSet;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.models.Friend;
import cuhk.cse.cmsc5736project.models.Pin;

public class PinView extends SubsamplingScaleImageView {

    private static final float textXOffset = -70;
    private static final float textYMargin = 40;
    private static final float textYPadding = 20;


    private final Paint iconPaint;
    private final Paint markerPaint;
    private final Paint textPaint;
    private final Paint strokePaint;
    private final Paint friendmPaint;
    private final Paint friendStrokePaint;
    private final Paint markerTextPaint;
    //private final float friendmHeight;

    private Bitmap markerBitmap;
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

        iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(50);
        //textPaint.setShadowLayer(5.0f, 0.0f, 0.0f, Color.BLACK);

        strokePaint = new Paint();
        strokePaint.setColor(Color.BLACK);
        strokePaint.setTextAlign(Paint.Align.CENTER);
        strokePaint.setTextSize(50);
        //strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(15);

        friendmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        friendmPaint.setColor(Color.RED);
        friendmPaint.setTypeface(Typeface.DEFAULT_BOLD);
        friendmPaint.setTextAlign(Paint.Align.RIGHT);
        friendmPaint.setTextSize(50);
        //Paint.FontMetrics fm = friendmPaint.getFontMetrics();
        //friendmHeight = fm.descent - fm.ascent;

        friendStrokePaint = new Paint();
        friendStrokePaint.setColor(Color.WHITE);
        friendStrokePaint.setTextAlign(Paint.Align.RIGHT);
        friendStrokePaint.setTextSize(50);
        //strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
        friendStrokePaint.setStyle(Paint.Style.STROKE);
        friendStrokePaint.setStrokeWidth(15);

        markerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerTextPaint.setColor(Color.BLACK);
        //markerTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        markerTextPaint.setTextAlign(Paint.Align.CENTER);
        markerTextPaint.setTextSize(50);

        float density = getResources().getDisplayMetrics().densityDpi;
        markerBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.map_marker_icon);
        float w = (density/420f) * markerBitmap.getWidth();
        float h = (density/420f) * markerBitmap.getHeight();
        markerBitmap = Bitmap.createScaledBitmap(markerBitmap, (int)w, (int)h, true);
    }

    public void addPinList(List<Pin> pinList) {
        //this.sPin = new PointF(10 + 70,0 + 500);

        for (Pin pin : pinList) {
            // modify pin location before adding
//            PointF tarPin = new PointF();

//            viewToSourceCoord(pin.getPin(), tarPin);
//            Log.i("pin view", "changing pin: " + pin.getPin().x + ", " + pin.getPin().y + ", " + tarPin.x + ", " + tarPin.y + ", " + getScale());
//            pin.setPin(tarPin);
            this.pinList.add(pin);
        }
        //this.pinList.addAll(pinList);

        refreshPins();
    }

    public void addPin(Pin pin) {

        // modify pin location before adding
        PointF tarPin = new PointF();

        viewToSourceCoord(pin.getPin(), tarPin);
        Log.i("pin view", "changing pin: " + pin.getPin().x + ", " + pin.getPin().y + ", " + tarPin.x + ", " + tarPin.y + ", " + getScale());
        pin.setPin(tarPin);
        this.pinList.add(pin);

        refreshPins();
    }

/*    void toPinViewCoord(Pin pin){
        pin.setPin(viewToSourceCoord(pin.getPin()));
        //return pin;
    }*/

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

    public void refreshPins() {
/*        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.map_marker);
        float w = (density/420f) * pin.getWidth();
        float h = (density/420f) * pin.getHeight();
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);*/

        pinList.get(0).setShowPin(true);

        for (Pin pin:pinList){
            pin.setScale();
            iconPaint.setColorFilter(new ColorMatrixColorFilter(pin.getColorMatrix()));
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        //paint.setAntiAlias(true);

        //if (sPin != null && pin != null) {
        if (pinList!=null)
        for (Pin pin : pinList){
            if (pin.getPin() != null) {
                sourceToViewCoord(pin.getPin(), vPin);
                float vX = vPin.x - (pin.getBitmap().getWidth() / 2);
                float vY = vPin.y - (pin.getBitmap().getHeight() / 2);
                canvas.drawBitmap(pin.getBitmap(), vX, vY, pin.getIconPaint());

                // print description for pin
                if (!pin.getDescription().isEmpty()) {
                    //textPaint.setTextSize(pin.getTextSize());
                    float vXText = vPin.x;
                    float vYText = vPin.y + (pin.getBitmap().getHeight() / 2) + textYMargin;
                    canvas.drawText(pin.getDescription(), vXText, vYText, strokePaint);
                    canvas.drawText(pin.getDescription(), vXText, vYText, textPaint);
                }

                // print friends for pin
                List<Friend> friendList = pin.getFriendList();
                if (friendList != null && friendList.size()>0){
                    float vXText = vPin.x + (pin.getBitmap().getWidth() / 2);
                    float vYText = vPin.y - (pin.getBitmap().getHeight() / 2) + textYMargin;
                    String friendText = Integer.toString(friendList.size());
                    canvas.drawText(friendText, vXText, vYText, friendStrokePaint);
                    canvas.drawText(friendText, vXText, vYText, friendmPaint);
                }
/*                List<Friend> friendList = pin.getFriendList();
                if (friendList != null) {
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setTextSize(pin.getTextSize() * 0.75f);
                    Rect bounds = new Rect();
                    paint.getTextBounds("a", 0, 1, bounds);
                    int textHeight = bounds.height();
                    // print 3 friends maximum
                    for (int i = 0; i < 3 && i < friendList.size(); i++) {
                        float vXText = vPin.x + pin.getBitmap().getWidth() + textXOffset;
                        float vYText = vPin.y - pin.getBitmap().getHeight() + ((textHeight + textYMargin) * (i+1));
                        canvas.drawText(friendList.get(i).getName(), vXText, vYText, paint);
                    }
                }*/

                //print marker
                if (pin.isShowPin()){
                    float vXMarker = vPin.x - (markerBitmap.getWidth() / 2);
                    float vYMarker = vPin.y - (markerBitmap.getHeight());
                    canvas.drawBitmap(markerBitmap, vXMarker, vYMarker, markerPaint);
                    float vXText = vPin.x;
                    float vYText = vYMarker;
                    canvas.drawText(pin.getDistanceDescription(), vXText, vYText, markerTextPaint);
                }
                //Log.i("pin view", "onDraw: " + vX + ", " + vY + ", " + pin.getPin().x + ", " + pin.getPin().y + ", " + getScale());
            }
        }

    }

}
