package cuhk.cse.cmsc5736project;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BeaconActivity extends AppCompatActivity {

    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 5000;
    private boolean isScanning = false;

    private List<Beacon> scanBeacon =new ArrayList<Beacon>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        // init BLE
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        scanHandler.post(scanRunnable);
        //init list
/*
        Beacon a = new Beacon();
        a.setUUID("a");
        a.setMajor(3);
        a.setMinor(3);
        a.setRSSI(3);
        scanBeacon.add(a);
        scanBeacon.add(a);
        scanBeacon.add(a);
        scanBeacon.add(a);*/
        ListView beaconListView = (ListView)findViewById(R.id.list_devices);
        BeaconListAdapter beaconListAdapter = new BeaconListAdapter();
        beaconListView.setAdapter(beaconListAdapter);
        beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(BeaconActivity.this, BeaconSetupActivity.class);

                intent.putExtra("uuid",scanBeacon.get(position).getUUID());
                intent.putExtra("major",scanBeacon.get(position).getMajor());
                intent.putExtra("minor",scanBeacon.get(position).getMinor());
                startActivity(intent);

            }

        });

    }


    class BeaconListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return scanBeacon.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            view =getLayoutInflater().inflate(R.layout.beacon_list_item,null);
            TextView textView_id =view.findViewById(R.id.textView_beacon_name);
            TextView textView_major=view.findViewById(R.id.textView_beacon_major);
            TextView textView_minor=view.findViewById(R.id.textView_beacon_minor);
            TextView textView_rssi=view.findViewById(R.id.textView_beacon_rssi);
            textView_id.setText(scanBeacon.get(i).getUUID());
            textView_major.setText( Integer.toString(scanBeacon.get(i).getMajor()));
            textView_minor.setText( Integer.toString(scanBeacon.get(i).getMinor()));
            textView_rssi.setText( Integer.toString(scanBeacon.get(i).getRSSI()));
            return view;
        }
    }

    private Runnable scanRunnable = new Runnable()
    {
        @Override
        public void run() {

            if (isScanning)
            {
                if (btAdapter != null)
                {
                    btAdapter.stopLeScan(leScanCallback);
                }
            }
            else
            {
                if (btAdapter != null)
                {
                    btAdapter.startLeScan(leScanCallback);
                }
            }

            isScanning = !isScanning;

            scanHandler.postDelayed(this, scan_interval_ms);
        }
    };
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
        {
            int startByte = 2;
            boolean patternFound = false;
            String a = device.getName();
            int b = device.getType();
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
                String uuid =  hexString.substring(0,8) + "-" +
                        hexString.substring(8,12) + "-" +
                        hexString.substring(12,16) + "-" +
                        hexString.substring(16,20) + "-" +
                        hexString.substring(20,32);
                final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
                final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

                Beacon beacon = new Beacon();
                beacon.setUUID(uuid);
                beacon.setMajor(major);
                beacon.setMinor(minor);
                beacon.setRSSI(rssi);

                boolean isUpdated = false;
                for(Beacon beaconItem : scanBeacon) {
                    if (beacon.isSameBeacon(beaconItem)) {
                        beaconItem.setRSSI(beacon.getRSSI());
                        isUpdated = true;
                        break;
                    }
                }
                if(!isUpdated)
                {
                    scanBeacon.add(beacon);
                }
                ListView beaconListView = (ListView)findViewById(R.id.list_devices);
                beaconListView.invalidateViews();
            }

        }
    };
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}
