package managers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;

public class BluetoothMessenger {

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;
    BluetoothSocket clientSocket;
    OutputStream os;

    public BluetoothMessenger(){
        initBluetooth();
    }

    public void sendMessage(String textToSend) {
        if ( clientSocket!= null && os != null) {
            write(textToSend);
        } else {
            initBluetooth();
            sendMessage(textToSend);
        }
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                String addr = "";
                for (BluetoothDevice dev : pairedDevices) {
                    String deviceName = dev.getName();
                    if (deviceName.equals("HC-05")) {
                        bluetoothAdapter.cancelDiscovery();
                        addr = dev.getAddress();
                        device = bluetoothAdapter.getRemoteDevice(addr);
                        try {
                            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                            clientSocket = (BluetoothSocket) m.invoke(device, 1);
                            clientSocket.connect();
                            os = clientSocket.getOutputStream();
                        } catch (Exception e) {
                            initBluetooth();
                            Log.d("xxx", "clientsocketError: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("BLUETOOTH ERROR ", e.getMessage());
        }
    }

    public void write(String textToSend) {
        byte[] bytes = new byte[textToSend.length()];
        try {
            for ( int i = 0;i < textToSend.length(); i++) {
                int temp = Character.codePointAt(textToSend.toCharArray(), i);
                switch (temp){
                    case 336: temp = 213; break;
                    case 337: temp = 245; break;
                    case 368: temp = 219; break;
                    case 369: temp = 251; break;
                    default: ;
                }
                bytes[i]= (byte)temp;
            }
            os.write(bytes);
            Log.d("xxx", "sended" + bytes);
        } catch (IOException e) {
            Log.d("xxx", "write exception " + e.getMessage());
        }
    }

    public BluetoothDevice getDevice(){
        return device;
    }

    public BluetoothSocket getClientSocket(){
        return clientSocket;
    }
}