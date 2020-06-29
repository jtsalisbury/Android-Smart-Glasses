package com.jtsalisbury.glasses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothDevice DeviceReference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set which action bar to use
        Toolbar actionBar = findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);

        // Setup the action handler for our bluetooth device search
        FloatingActionButton populateDeviceButton = findViewById(R.id.refreshDevices);
        populateDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateBluetoothDevices();
            }
        });

        populateBluetoothDevices();
    }

    protected void populateBluetoothDevices() {
        System.out.println("Beginning to find devices...");

        // Get adapter and populate list
        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();

        if (pairedDevices.size() == 0) {
            System.out.println("No devices found");

            return;
        }

        int deviceCount = 0;

        LinearLayout deviceLayout = (LinearLayout) findViewById(R.id.deviceLayoutList);
        deviceLayout.removeAllViews();

        // Loop through all bonded devices
        BluetoothDevice curDevice = null;
        for (final BluetoothDevice device : pairedDevices) {
            String name = device.getName();
            String address = device.getAddress();

            if (!name.toLowerCase().contains("bluefruit")) {
                continue;
            }

            // Cache in case we only have one paired bluetooth module
            curDevice = device;

            deviceCount++;

            // Create a new list item for our device
            Button deviceButton = new Button(this);
            deviceButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            deviceButton.setText(name);
            deviceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setConnectedDevice(device);
                }
            });

            System.out.println("Device found: " + name);

            // Add the device
            deviceLayout.addView(deviceButton);
        }

        // Only one, let's assume that's the one we want
        if (deviceCount == 1) {
            setConnectedDevice(curDevice);
        }

        // Display how many results we had
        TextView searchStatus = (TextView) findViewById(R.id.searchStatus);
        searchStatus.setText("Found " + deviceCount + " devices");
    }

    protected void setConnectedDevice(BluetoothDevice device) {
        Intent connectIntent = new Intent(this, ConnectedActivity.class);
        connectIntent.putExtra("device", device);
        startActivity(connectIntent);
    }
}