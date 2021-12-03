package com.example.bluetoothconnection;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.MyHolder> {

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.device_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Devices devices = bluetoothDevicesList.get(position);
        holder.name.setText(devices.getName());
        holder.address.setText(devices.getMacAddress());
    }

    @Override
    public int getItemCount() {
        return bluetoothDevicesList != null ? bluetoothDevicesList.size() :0;
    }
    Context context;
    List<Devices> bluetoothDevicesList;
    public void setData(Context context, List<Devices> bluetoothDevicesList) {
        this.context = context;
        this.bluetoothDevicesList = bluetoothDevicesList;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView name , address;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.deviceName);
            address = itemView.findViewById(R.id.deviceMac);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Devices devices = bluetoothDevicesList.get(getAdapterPosition());
                        new ConnectedThread(context,devices.getMacAddress()).execute();
                }
            });
        }
    }



}
