package alex_shutov.com.ledlights.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lodoss on 01/07/16.
 */
public class BTConnector {

    private static final String LOG_TAG = BTConnector.class.getSimpleName().toString();


    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // name for SDP record when creating server socket
    private static final String NAME_SECURE = "AppSecure";
    private static final String NAME_INSECURE = "AppInsecure";

    private Context context;
    private final BluetoothAdapter btAdapter;

    /**
     * When we're managing handhelds, connection can be secure or insecure, but in case of
     * HC-05 (04) we need only insecure thread, because these adapters does not support
     * encription.
     * We can accept connection either secure or insecure simultaneously, but there is single
     * thread for connection.
     */
    private AcceptThread acceptSecure;
    private AcceptThread acceptInsecure;
    private ConnectThread connectThread;


    public BTConnector(Context context){
        this.context = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // clear accept threads
        acceptSecure = null;
        acceptInsecure = null;
    }

    private void showToast(String msg){
        Observable<String> r = Observable.just(msg)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
        Observable.defer(() -> r).subscribe(s -> {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * start listening for Bluetooth incoming connection by starting new thread.
     */
    public void acceptConnection(){
        // one thread instance at a time
        stopAcceptingConnection();
        showToast("Accepting connection ");
        // init accept threads
        acceptSecure = new AcceptThread(true);
        acceptSecure.start();
        acceptInsecure = new AcceptThread(false);
        acceptInsecure.start();
    }

    /**
     * cancel BluetoothServerSocket if it wait for connection
     */
    public void stopAcceptingConnection(){
        if (null != acceptSecure || null != acceptInsecure){
            showToast("stopping accepting connection");
        }
        if (null != acceptSecure){
            acceptSecure.cancel();
            acceptSecure = null;
        }
        if (null != acceptInsecure){
            acceptInsecure.cancel();
            acceptInsecure = null;
        }
    }

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket serverSocket;
        private String socketType;

        public AcceptThread(boolean secure){
            BluetoothServerSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";

            try {
                if (secure){
                    tmp = btAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                            MY_UUID_SECURE);
                } else {
                    tmp = btAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE,
                            MY_UUID_INSECURE);
                }
            } catch (IOException e){
                Log.e(LOG_TAG, "Socket type: " + socketType + " listen() failed, ", e);
            }
            serverSocket = tmp;
        }

        @Override
        public void run() {
            Log.i(LOG_TAG, "Socket type: " + socketType + " AcceptThread is running " + this);
            setName("AcceptThread" + socketType);

            BluetoothSocket socket = null;
            // TODO: wile not connected (comm between threads is required)
            while (true){
                try {
                    socket = serverSocket.accept();
                } catch (IOException e){
                    Log.e(LOG_TAG, "Socket type: " + socketType + " accept() failed ", e);
                    break;
                }
                showToast("Connection accepted (" + socketType + ")");
                if (null != socket){
                    // TODO: add synchronization object interface
                    synchronized (BTConnector.this){
                        int N = 2;
                        Log.i(LOG_TAG, "Server socket accepted connection");
                        break;
                    }
                }
            }
        }
        public void cancel(){
            String msg = "Socket type " + socketType + " cancel " + this;
            Log.d(LOG_TAG, msg);
            showToast(msg);
            try {
                serverSocket.close();
            } catch (IOException e){
                msg = "Socket type " + socketType + " closed() of server failed ";
                showToast(msg);
                Log.e(LOG_TAG, msg, e);
            }
            msg = "Socet type " + socketType + " closed";
            Log.d(LOG_TAG, msg);
            showToast(msg);
        }
    }

    /**
     * Connect insecure to a given device
     * @param isSecure
     */
    public void connect(BluetoothDevice device,  boolean isSecure){
        // stop connecting first
        stopConnecting();
        connectThread = new ConnectThread(device, isSecure);
        connectThread.start();
    }

    /**
     * Stop thread trying to establish connection
     */
    public void stopConnecting(){
        if (null != connectThread){
            connectThread.cancel();
            connectThread = null;
        }
    }


    /**
     * Thread for connecting to Bluettoth device in background
     */
    private class ConnectThread extends Thread {

        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private String socketType;

        public ConnectThread (BluetoothDevice device, boolean secure){
            this.device = device;
            BluetoothSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";

            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                }
                else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            } catch (IOException e){
                Log.e(LOG_TAG, "can't create socket (" + socketType + ") in create() method ");
            }
            socket = tmp;
        }


        @Override
        public void run() {
            Log.i(LOG_TAG, "Connection thread is started (" + socketType + ")");
            setName("ConnectThread" + socketType);

            //TODO: it will be moved out of here
            btAdapter.cancelDiscovery();
            // Connect to BluetoothSocket
            try {
                socket.connect();
            } catch (IOException e){
                Log.i(LOG_TAG, "Connection failed");
                // close socket
                try {
                    socket.close();
                } catch (IOException closeExc){
                    Log.e(LOG_TAG, "Can't close socket after failed connection (" + socketType +
                    ")", closeExc);
                }
                // TODO: notify user about failed connection


                return;
            }

            synchronized (BTConnector.this){
                connectThread = null;
            }

            // TODO: start another thread for exchanging the data (see bluetoothChat sample)
            String msg = "Bluetooth device connected";
            showToast(msg);
            Log.i(LOG_TAG, msg);

        }

        public void cancel(){
            try {
                socket.close();
            } catch (IOException e){
                Log.e(LOG_TAG, "Closing connection (" + socketType + ") failed", e);
            }
        }
    }

    
}
