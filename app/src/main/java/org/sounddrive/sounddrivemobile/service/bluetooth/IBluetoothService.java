package org.sounddrive.sounddrivemobile.service.bluetooth;

public interface IBluetoothService {
    void connect();
    void disconnect();

    void sendData(byte message);

    void sendData(byte[] message, int length);

    void sendData(String message);

    void onDestroy();

    boolean isConnected();

    void setDataReceiveHandler(IDataReceiveHandler handler);
}
