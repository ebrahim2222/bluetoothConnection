package com.example.bluetoothconnection;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.UUID;

public class ConnectedThread extends AsyncTask<Void, Void,String> {
    UUID myUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket bluetoothSocket;
    ProgressDialog dialog;
    BluetoothAdapter adapter;
    boolean isConnected = false;
    String macAddress;
    Context context;

    ConnectedThread(Context context,String macAddress){

        this.context = context;
        this.macAddress = macAddress;

    }
    boolean connectSuccess = true;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setTitle("wait");
        dialog.setMessage("Connecting");
        dialog.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        connectSuccess = true;
        try {
            if(bluetoothSocket == null && !isConnected){
                adapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device =  adapter.getRemoteDevice(macAddress);
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUuid);
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                bluetoothSocket.connect();
            }
        }catch (Exception e){
            connectSuccess = false;
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (!connectSuccess){
            Toast.makeText(context, "couldn't connect", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "bluetooth connected", Toast.LENGTH_SHORT).show();
            isConnected = true;
        }
        dialog.dismiss();

    }
}
