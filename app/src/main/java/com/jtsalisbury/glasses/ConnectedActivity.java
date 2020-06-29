package com.jtsalisbury.glasses;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ConnectedActivity extends AppCompatActivity {
    private BluetoothDevice connectedDevice = null;
    private final BluetoothGatt[] connection = new BluetoothGatt[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        // Run our connection code post view creation
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                connectDevice();
            }
        });
    }

    private void connectDevice() {
        // Get the device we want to connect to
        Intent connectIntent = getIntent();
        connectedDevice = connectIntent.getParcelableExtra("device");

        // Invalid device supplied
        if (connectedDevice == null) {
            System.out.println("Invalid device supplied");
            return;
        }

        // For use in updating connection state

        // Try connecting!
        System.out.println("Attempting to connect...");
        connectedDevice.connectGatt(this, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                // Will be sent to the UI to update status
                final String[] uiText = new String[1];

                connection[0] = gatt;

                // Connection created!
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    System.out.println("Connected to " + connectedDevice.getName());

                    uiText[0] = "Connected to " + connectedDevice.getName();
                }

                // Connection destroyed
                if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    System.out.println("Disconnected from " + connectedDevice.getName());

                    uiText[0] = "Disconnected from " + connectedDevice.getName();
                }

                // Because we are running anonymously, we can't guarantee we're on the same thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final TextView deviceStatusView = findViewById(R.id.deviceStatus);

                        deviceStatusView.setText(uiText[0]);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (connection[0] == null) {
            return;
        }

        // Disconnect the current device if we're connected
        System.out.println("Closing connection..");

        connection[0].disconnect();

        super.onBackPressed();
    }
}