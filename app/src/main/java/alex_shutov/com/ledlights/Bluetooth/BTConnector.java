package alex_shutov.com.ledlights.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lodoss on 01/07/16.
 */
public class BTConnector {

    private static final String LOG_TAG = BTConnector.class.getSimpleName().toString();
    // my Samsung Galaxy Tab 3 - initiates connection
    private static final String ADDRESS_TABLET = "18:1E:B0:52:42:AD";
    // test phone from work (Xiaomi MI) - accept connection
    private static final String ADDRESS_PHONE = "A0:86:C6:8F:73:1A";

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

    AcceptThread acceptThread;


    public BTConnector(Context context){
        this.context = context;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        acceptThread = null;
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
     * @param isSecure
     */
    public void acceptConnection(boolean isSecure){
        showToast("Accepting connection ");
        // one thread instance at a time
        stopAcceptingConnection();
        acceptThread = new AcceptThread(isSecure);
        acceptThread.start();
    }

    /**
     * cancel BluetoothServerSocket if it wait for connection
     */
    public void stopAcceptingConnection(){
        if (null != acceptThread){
            showToast("stopping accepting connection");
            acceptThread.cancel();
            acceptThread = null;
        }
    }

    /**
     * Connect insecure to a given device
     * @param isSecure
     */
    public void connect(boolean isSecure){

    }

    /**
     * Stop thread trying to establish connection
     */
    public void stopConnecting(){

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

    

    
}
