package cuhk.cse.cmsc5736project;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cuhk.cse.cmsc5736project.models.Beacon;
import cuhk.cse.cmsc5736project.models.RSSIModel;
import cuhk.cse.cmsc5736project.utils.Utility;

import static cuhk.cse.cmsc5736project.BeaconSetupActivity.STATE.IDLE;
import static cuhk.cse.cmsc5736project.BeaconSetupActivity.STATE.LOADDATA;
import static cuhk.cse.cmsc5736project.BeaconSetupActivity.STATE.SAVEDATA;

public class BeaconSetupActivity extends AppCompatActivity implements AsyncResponse {

    public enum STATE {LOADDATA,SAVEDATA,IDLE};

    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btLeScanne;
    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 5000;
    private boolean isScanning = false;
    STATE curState = IDLE;
    String uuid;
    int major,minor;
    TextView textView_id,textView_major,textView_minor,textView_rssi;

    int rssiCountAvg = 0;
    int prevRSSI = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        setContentView(R.layout.activity_beacon_setup);

        // init BLE
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btLeScanne = btAdapter.getBluetoothLeScanner();


        textView_id=findViewById(R.id.textview_becon_setup_id);
        textView_major=findViewById(R.id.textview_becon_setup_major);
        textView_minor=findViewById(R.id.textview_becon_setup_minor);
        textView_rssi=findViewById(R.id.textview_becon_setup_rssi);

        Intent intent = getIntent();

        uuid = intent.getStringExtra("uuid");
        major = intent.getIntExtra("major",0);
        minor = intent.getIntExtra("minor",0);

        //load data
        LoadData();

        textView_id.setText(uuid);
        textView_major.setText(Integer.toString(major));
        textView_minor.setText(Integer.toString(minor));


        scanHandler.post(scanRunnable);
    }
    public void LoadData()
    {
        curState = LOADDATA;
        HashMap postData = new HashMap();
        postData.put("uuid_major_minor",uuid + "_" + Integer.toString(major)+ "_" + Integer.toString(minor));
        PostResponseAsyncTask task = new PostResponseAsyncTask(BeaconSetupActivity.this,postData,this);
        task.execute(LocationManager.ROOT_URL+"/load_beacon_data.php");
    }
    public void onClick(View v) {
        TextView textView;
        switch (v.getId()) {
            case R.id.btn_half_meter_calibrate:
                textView = findViewById(R.id.textview_half_meter_power);
                textView.setText(textView_rssi.getText());
                break;
            case R.id.btn_one_meter_calibrate:
                textView = findViewById(R.id.textview_one_meter_power);
                textView.setText(textView_rssi.getText());
                break;
            case R.id.btn_two_meter_calibrate:
                textView = findViewById(R.id.textview_two_meter_power);
                textView.setText(textView_rssi.getText());
                break;
            case R.id.btn_four_meter_calibrate:
                textView = findViewById(R.id.textview_four_meter_power);
                textView.setText(textView_rssi.getText());
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_save:
                curState = SAVEDATA;
                textView = findViewById(R.id.textview_half_meter_power);
                int n1 = Integer.valueOf(textView.getText().toString());
                textView = findViewById(R.id.textview_one_meter_power);
                int n2 = Integer.valueOf(textView.getText().toString());
                textView = findViewById(R.id.textview_two_meter_power);
                int n3 = Integer.valueOf(textView.getText().toString());
                textView = findViewById(R.id.textview_four_meter_power);
                int n4 = Integer.valueOf(textView.getText().toString());
                textView = (EditText)findViewById(R.id.textview_location_x);
                float px = Float.valueOf(textView.getText().toString());
                textView = (EditText)findViewById(R.id.textview_location_y);
                float py = Float.valueOf(textView.getText().toString());
                HashMap postData = new HashMap();
                postData.put("uuid_major_minor",uuid + "_" + Integer.toString(major)+ "_" + Integer.toString(minor));
                postData.put("uuid",uuid);
                postData.put("major",Integer.toString(major));
                postData.put("minor",Integer.toString(minor));
                postData.put("position_x", Float.toString(px));
                postData.put("position_y", Float.toString(py));
                postData.put("rssi_half_m_signal", Integer.toString(n1));
                postData.put("rssi_one_m_signal", Integer.toString(n2));
                postData.put("rssi_two_m_signal", Integer.toString(n3));
                postData.put("rssi_four_m_signal",Integer.toString(n4));
                PostResponseAsyncTask task = new PostResponseAsyncTask(BeaconSetupActivity.this,postData,this);
                task.execute(LocationManager.ROOT_URL+"/save_beacon_data.php");
                finish();
                break;
        }
    }



    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            byte[] scanRecord = result.getScanRecord().getBytes();
            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5)
            {
                if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
                { //Identifies correct data length
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound)
            {
                //Convert to hex String
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = Utility.getInstance().bytesToHex(uuidBytes);

                //UUID detection
                String d_uuid =  hexString.substring(0,8) + "-" +
                        hexString.substring(8,12) + "-" +
                        hexString.substring(12,16) + "-" +
                        hexString.substring(16,20) + "-" +
                        hexString.substring(20,32);
                final int d_major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
                final int d_minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

                int avgRSSI;
                if(uuid.equals(d_uuid) && d_major == major && d_minor == minor) {
                    if(rssiCountAvg ==0)
                    {
                        avgRSSI = result.getRssi();
                        prevRSSI = avgRSSI;
                        rssiCountAvg++;
                    }
                    else {
                        avgRSSI = (prevRSSI * rssiCountAvg + result.getRssi()) / (rssiCountAvg + 1);
                        prevRSSI = avgRSSI;
                        if (rssiCountAvg <= 10) {
                            rssiCountAvg++;
                        }
                    }
                    textView_rssi.setText(Integer.toString(avgRSSI));
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d("", "onBatchScanResults: " + results.size() + " results");
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w("", "LE Scan Failed: " + errorCode);
        }
    };



    private Runnable scanRunnable = new Runnable()
    {
        @Override
        public void run() {


            ScanFilter beaconFilter = new ScanFilter.Builder()
                    .build();

            ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
            filters.add(beaconFilter);

            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            ;
            btLeScanne.startScan(filters, settings, mScanCallback);
        }
    };
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
        {


        }
    };


    @Override
    public void processFinish(String output) {
        switch (curState)
        {
            case SAVEDATA:
                Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
                RSSIModel.getInstance().updateModel(BeaconSetupActivity.this);
                break;
            case LOADDATA:
                TextView textView;
                JSONObject mainObject = null;
                try {
                    mainObject = new JSONObject(output);
                    textView = (EditText)findViewById(R.id.textview_location_x);
                    textView.setText(mainObject.getString("position_x"));
                    textView = (EditText)findViewById(R.id.textview_location_y);
                    textView.setText(mainObject.getString("position_y"));
                    textView = findViewById(R.id.textview_half_meter_power);
                    textView.setText(mainObject.getString("rssi_half_m_signal"));
                    textView = findViewById(R.id.textview_one_meter_power);
                    textView.setText(mainObject.getString("rssi_one_m_signal"));
                    textView = findViewById(R.id.textview_two_meter_power);
                    textView.setText(mainObject.getString("rssi_two_m_signal"));
                    textView = findViewById(R.id.textview_four_meter_power);
                    textView.setText(mainObject.getString("rssi_four_m_signal"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        curState = STATE.IDLE;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}