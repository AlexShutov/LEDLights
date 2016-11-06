package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnection;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionCallback;

/**
 * Created by Alex on 11/5/2016.
 */
public abstract class EstablishConnectionStrategy implements EstablishConnection {
    private static EstablishConnectionCallback stubCallback = new EstablishConnectionCallback() {
        @Override
        public void onConnectionEstablished(BtDevice conenctedDevice) {        }

        @Override
        public void onAttemptFailed() {        }
    };
    private EstablishConnectionCallback callback;

    public EstablishConnectionStrategy(){
        setCallback(stubCallback);
    }

    /**
     * Get everything this algorithm need from DataProvider.
     * Here I suppose that all entities are the same during all lifetime of that logic cell -
     * those are singletons and we don't need to provide data every time strategy is triggered
     * @param dataProvider
     */
    public abstract void init(DataProvider dataProvider);

    public void setCallback(EstablishConnectionCallback callback) {
        this.callback = callback;
    }

    public EstablishConnectionCallback getCallback() {
        return callback;
    }
}
