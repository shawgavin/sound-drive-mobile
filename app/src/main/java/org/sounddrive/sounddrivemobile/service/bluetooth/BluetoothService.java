package org.sounddrive.sounddrivemobile.service.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothService implements IBluetoothService {
    private static final String TAG = "bluetooth";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String address = "20:16:12:07:72:20";
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private Activity activity;
    private Timer timer = new Timer();
    private IDataReceiveHandler dataReceiveHandler;
    private InputStream inStream;
    private BufferedReader inBufferedReader;

    public BluetoothService(final Activity activity) {
        this.activity = activity;
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkBTState();
                    }
                });
            }
        }, 0, 200);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void error(String title, String message) {

        Log.d(TAG, title + " - " + message);
    }

    public void sendData(byte message) {
        Log.d(TAG, "Send byte: " + Integer.valueOf(message).toString());

        byte[] bytes = new byte[1];
        bytes[0] = message;
        sendData(bytes, 1);

    }

    public void sendData(byte[] message, int length) {
        Log.d(TAG, "Send bytes : 0x" + bytesToHex(message));

        try {
            outStream.write(message, 0, length);
            dataReceiveHandler.onLineReceived(inBufferedReader.readLine());

        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            error("Fatal Error", msg);
        }
    }

    public void sendData(String message) {
        Log.d(TAG, "Send String: " + message);

        sendData(message.getBytes(), message.length());
    }

    public boolean isConnected() {
        return (btSocket != null) && (btSocket.isConnected());
    }

    @Override
    public void setDataReceiveHandler(IDataReceiveHandler handler) {

        dataReceiveHandler = handler;
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            error("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                if (!isConnected())
                    connect();
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }


    public void disconnect() {
        toast("BT Disconnecting..");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                error("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try {
            if (btSocket != null)
                btSocket.close();
        } catch (IOException e2) {
            error("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
        toast("BT Disconnected!");

    }

    public void toast(final String text) {
        Log.i(TAG,text);
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (!activity.isFinishing()) {
//                    Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    public void connect() {
        Log.d(TAG, "...onResume - try connect...");
        toast("BT Connecting..");
        if (btAdapter != null) {
            // Set up a pointer to the remote node using it's address.
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the
            //     UUID for SPP.

            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e1) {
                error("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
                return;
            }

            // Discovery is resource intensive.  Make sure it isn't going on
            // when you attempt to connect and pass your message.
            btAdapter.cancelDiscovery();

            // Establish the connection.  This will block until it connects.
            Log.d(TAG, "...Connecting...");
            try {
                btSocket.connect();
                Log.d(TAG, "...Connection ok...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    error("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                    return;
                }
            }

            // Create a data stream so we can talk to server.
            Log.d(TAG, "...Create Socket...");

            try {
                outStream = btSocket.getOutputStream();
                inStream = btSocket.getInputStream();
                inBufferedReader = new BufferedReader(new InputStreamReader(inStream));
            } catch (IOException e) {
                error("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
                return;
            }
            toast("BT Connected!");
        }
    }

    public void onDestroy() {
        timer.cancel();
        disconnect();
    }
}
