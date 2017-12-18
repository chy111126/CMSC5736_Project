package cuhk.cse.cmsc5736project;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cuhk.cse.cmsc5736project.fragments.FriendsFragment;
import cuhk.cse.cmsc5736project.models.Friend;

public class AddFriendActivity extends AppCompatActivity {
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 5000;
    private boolean isScanning = false;

    private List<Friend> scanDevices =new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friend);

        // init BLE
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();


        scanHandler.post(scanRunnable);


        ListView devicesListView = (ListView)findViewById(R.id.list_devices);
        AddFriendActivity.DevicesListAdapter deviceListAdapter = new AddFriendActivity.DevicesListAdapter();
        devicesListView.setAdapter(deviceListAdapter);
        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //FriendsFragment.AddFriend(scanDevices.get(position));
                finish();
            }

        });


    }

    private Runnable scanRunnable = new Runnable()
    {
        @Override
        public void run() {

            if(btAdapter.isDiscovering()){
                btAdapter.cancelDiscovery();
                Log.d("bt", "btnDiscover: Canceling discovery.");


                btAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }
            if(!btAdapter.isDiscovering()){


                btAdapter.startDiscovery();
                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
            }

            scanHandler.postDelayed(this, scan_interval_ms);
        }
    };
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("bt", "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                boolean isVaild = false;
                String name = device.getName();
                if(name!= null && !name.contains("beacon")) {
                    isVaild = true;
                }
                if(isVaild) {
                    Friend friend = new Friend(device.getName(),"", device.toString());

                    boolean isUpdated = false;
                    for (Friend f : scanDevices) {
                        if (friend.isSame(f)) {
                            isUpdated = true;
                            break;
                        }
                    }
                    if (!isUpdated) {
                        scanDevices.add(friend);
                    }
                    ListView deviceListView = (ListView) findViewById(R.id.list_devices);
                    deviceListView.invalidateViews();
                }
            }
        }
    };
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord)
        {
            int startByte = 2;
            boolean patternFound = true;
            Log.d("test", device.getName());
            //exclude beacon
            while (startByte <= 5)
            {
                if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
                { //Identifies correct data length
                    patternFound = false;
                    break;
                }
                startByte++;
            }

            if (patternFound)
            {

            }

        }
    };


    class DevicesListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return scanDevices.size();
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
            view =getLayoutInflater().inflate(R.layout.device_list_item,null);
            TextView textView_name =view.findViewById(R.id.textView_beacon_name);
            TextView textView_mac=view.findViewById(R.id.textView_friend_mac);

            textView_name.setText(scanDevices.get(i).getName());
            textView_mac.setText(scanDevices.get(i).getMAC());

            return view;
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    }