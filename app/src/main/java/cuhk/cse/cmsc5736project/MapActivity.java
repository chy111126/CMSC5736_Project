package cuhk.cse.cmsc5736project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cuhk.cse.cmsc5736project.fragments.MapFragment;
import cuhk.cse.cmsc5736project.interfaces.OnFriendListChangeListener;
import cuhk.cse.cmsc5736project.interfaces.OnFriendResultListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIListChangeListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIResultListener;
import cuhk.cse.cmsc5736project.models.Friend;
import cuhk.cse.cmsc5736project.models.POI;
import cuhk.cse.cmsc5736project.models.Pin;
import cuhk.cse.cmsc5736project.views.PinView;

import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP;

public class MapActivity extends AppCompatActivity {
    PinView imageView;
    float lastKnownX;
    float lastKnownY;
    LocationManager locationManager;

    private List<Friend> friendList = new ArrayList<>();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        context = MapActivity.this;

        imageView = (PinView)findViewById(R.id.mapViewTest);
        setImageViewListeers(imageView);

        locationManager = LocationManager.getInstance();
        locationManager.getSimulatedPOIDefinitions(this, new OnPOIResultListener(){
            public void onRetrieved(List< POI > poiList){
                List<Pin> pinList = new ArrayList<Pin>() ;
                for(POI poi: poiList) {
                    //PointF pinPosition = poi.getPosition();
                    pinList.add(new Pin(MapActivity.this, poi, R.drawable.map_marker_icon));
                }
                imageView.addPinList(pinList);
            }
            public void onChanged(){
                imageView.refreshPins();
            }
        });

        LocationManager.getInstance().setOnPOIChangedMapFragmentListener(new OnPOIListChangeListener() {
            @Override
            public void onChanged() {
                imageView.refreshPins();
            }
        });

        locationManager.getSimulatedFriendDefinitions(this, new OnFriendResultListener(){
            public void onRetrieved(List< Friend > friendList){
                //TODO: handle friend list
                for (Friend friend: friendList) {
                    Log.i("init: ", "friend " + friend.getName() + " at " + friend.getNearestLocation());
                }
            }
        });

        LocationManager.getInstance().setOnFriendListChangeMapFragmentListener(new OnFriendListChangeListener() {
            @Override
            public void onAdded(Friend item) {
                friendList.add(item);
                sortViewList();
                //FriendListAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onDeleted(Friend item) {
                friendList.remove(item);
                sortViewList();
                //FriendListAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onChanged() {
                sortViewList();
                //FriendListAdapter.this.notifyDataSetChanged();
            }
        });
    }

    private void sortViewList() {
        Collections.sort(friendList, new Comparator<Friend>() {
            @Override
            public int compare(Friend f1, Friend f2) {
                if(f1.getBeacon().getRSSI() > f2.getBeacon().getRSSI()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }
    void setImageViewListeers(final PinView imageView){
        imageView.setImage(ImageSource.resource(R.drawable.floorplan));
        imageView.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);
        //imageView.setPin(new PointF(1000,100));

        //final PinView imageViewF = imageView;

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Log.i("image view: ", "onTouch: " + motionEvent.getAction());
                if (view.getId()== imageView.getId() && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    lastKnownX= motionEvent.getX();
                    lastKnownY= motionEvent.getY();
                    //Log.i("image view: ", "onTouch: " + lastKnownX + ", " + lastKnownY);
                    //imageViewF.addPin(new Pin(MapActivity.this, new PointF(lastKnownX,lastKnownY),R.drawable.map_marker,"marked"));
                }
                return false;
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (view.getId() == imageView.getId()) {
                    Toast.makeText(MapActivity.this, "Long clicked "+lastKnownX+" "+lastKnownY, Toast.LENGTH_SHORT).show();
                    Log.d("EditPlanFragment","Scale "+imageView.getScale());
                    imageView.addPin(new Pin(MapActivity.this, new PointF(lastKnownX,lastKnownY),R.drawable.map_marker,"long clicked"));

/*                    imageViewF.post(new Runnable(){
                        public void run(){
                            imageViewF.getRootView().postInvalidate();
                        }
                    });*/

                    return true;
                }
                return false;
            }
        });

    }

}
