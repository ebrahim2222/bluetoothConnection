package com.example.bluetoothconnection;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Button scann;
    BluetoothAdapter bluetoothAdapter;
    DevicesAdapter adapter;
    List<Devices> bluetoothDevicesList;
    static  final int REQUEST_ENABLE_BT = 0;
    static  final int REQUEST_DISCOVERABLE_BT = 1;
    static  final int REQUEST_BLUETOTH_PERMISSION = 2;

    // Create a BroadcastReceiver for ACTION_FOUND

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) ){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(MainActivity.this, "STATE Turned OF", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(MainActivity.this, "STATE OF", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(MainActivity.this, "STATE On", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(MainActivity.this, "STATE Turning on", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };


     // Broadcast Receiver for listing devices that are not yet paired

    final BroadcastReceiver receiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED) ){
                final int state = intent.getIntExtra(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED,bluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(MainActivity.this, "SCAN_MODE_CONNECTABLE_DISCOVERABLE", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Toast.makeText(MainActivity.this, "SCAN_MODE_CONNECTABLE", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Toast.makeText(MainActivity.this, "SCAN_MODE_NONE", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Toast.makeText(MainActivity.this, "STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Toast.makeText(MainActivity.this, "STATE_CONNECTED", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };
    final BroadcastReceiver receiver4 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED) ){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                    Toast.makeText(MainActivity.this, "BOND_BONDED", Toast.LENGTH_SHORT).show();

                }
                if(device.getBondState() == BluetoothDevice.BOND_BONDING){
                    Toast.makeText(MainActivity.this, "BOND_BONDING", Toast.LENGTH_SHORT).show();

                }
                if(device.getBondState() == BluetoothDevice.BOND_NONE){
                    Toast.makeText(MainActivity.this, "BOND_NONE", Toast.LENGTH_SHORT).show();

                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        scann = findViewById(R.id.scanButton);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setUpRecycler();

        if (bluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        scann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH)!= PackageManager.PERMISSION_GRANTED){
                   if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this , Manifest.permission.BLUETOOTH)){

                   }else{
                       ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.BLUETOOTH},REQUEST_BLUETOTH_PERMISSION);
                   }
               }else{
                   if (!bluetoothAdapter.isEnabled()) {
                       Toast.makeText(MainActivity.this, "Enable bluetooth", Toast.LENGTH_SHORT).show();
                       Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                       startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
                   }else{
                       dicoverAble();
                   }
               }
            }
        });




    }

    private void setUpRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(manager);
        adapter = new DevicesAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_BLUETOTH_PERMISSION:
                if(grantResults.length>0 && grantResults[0] > PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    dicoverAble();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    dicoverAble();
                }else{
                    Toast.makeText(MainActivity.this, "you didn't enable bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_DISCOVERABLE_BT:
                if(resultCode == RESULT_OK){
                    showDevices();
                }else
                {
                   // Toast.makeText(MainActivity.this, "no devices", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void dicoverAble() {
        if(!bluetoothAdapter.isDiscovering()){
            Toast.makeText(this, "making your device discoverable", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(intent,REQUEST_DISCOVERABLE_BT);
            showDevices();
        }
    }

    private void showDevices() {
        bluetoothDevicesList = new ArrayList<>();
        if(bluetoothAdapter.isEnabled()){
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for(BluetoothDevice foundDevices : devices){
                bluetoothDevicesList.add(new Devices(foundDevices.getName(),foundDevices.getAddress()));
            }
            adapter.setData(MainActivity.this,bluetoothDevicesList);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(MainActivity.this, "please turn on bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

}