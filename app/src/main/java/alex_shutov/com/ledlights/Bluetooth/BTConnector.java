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


    // name for SDP record when creating server socket
    private static final String NAME_SECURE = "AppSecure";
    private static final String NAME_INSECURE = "AppInsecure";


    /**
     * We can use the one and only uuid when communicating between handhelds,
     * but this app is meant to be used with phone or  BT-05 adapter (IoT), which has
     * predefined UUID, and it has to b
     */
    private final UUID uuidSecure;
    private final UUID uuidInsecure;

    private Context context;
    private final BluetoothAdapter btAdapter;



    public BTConnector(Context context, String uuidSecure, String uuidInsecure){
        this.context = context;
        this.uuidSecure = UUID.fromString(uuidSecure);
        this.uuidInsecure = UUID.fromString(uuidInsecure);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // clear accept threads
    }

    /**
     * Just helper method, switches thread and show popup
     * TODO: move to interface with outer world
     * @param msg
     */
    private void showToast(String msg){
        Observable<String> r = Observable.just(msg)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
        Observable.defer(() -> r).subscribe(s -> {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        });
    }
    
}
