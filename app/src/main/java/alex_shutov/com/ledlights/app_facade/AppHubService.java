package alex_shutov.com.ledlights.app_facade;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.service.BtCellService;
import alex_shutov.com.ledlights.device_commands.ControlPort.DeviceControlPort;
import alex_shutov.com.ledlights.device_commands.ControlPort.EmulationCallback;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortListener;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.service.DeviceCommandService;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

/**
 * Created by lodoss on 27/12/16.
 */

/**
 * Service, which bind to all other services and connects them.
 * It run in background all the time.
 */
public class AppHubService extends Service implements AppFacade {
    private static final String LOG_TAG = AppHubService.class.getSimpleName();

    public class AppHubBinder extends Binder {
        public AppFacade getAppFacade() {
            return AppHubService.this;
        }
    }

    private PublishSubject<Boolean> btConnectedSource;
    private PublishSubject<Boolean> commandSerializedConnectedSource;
    private Subscription initSubscription;

    // external listeners
    AppFacadeDeviceListener deviceListener;

    // interfacing with Bluetooth communication port
    private BtCommPort btComm;
    // listener for feedback with Bluetooth port.
    private BtCommPortListener btListener = new BtCommPortListener() {
        @Override
        public void onConnectionStarted(BtDevice btDevice) {
            if (null != deviceListener) {
                deviceListener.onDeviceConnected();
            }
        }

        @Override
        public void onConnectionFailed() {
            if (null != deviceListener) {
                deviceListener.onDeviceConnectionFailed();
            }
        }

        @Override
        public void onReconnected(BtDevice btDevice) {
            if (null != deviceListener) {
                deviceListener.onDeviceReconnected();
            }
        }

        @Override
        public void onDummyDeviceSelected() {
            if (null != deviceListener) {
                deviceListener.onDumyDeviceSelected();
            }
        }

        @Override
        public void onDataSent() {

        }

        @Override
        public void onDataSendFailed() {

        }

        @Override
        public void receiveData(byte[] data, int size) {

        }

        @Override
        public void onPortReady(int portID) {

        }

        @Override
        public void onCriticalFailure(int portID, Exception e) {

        }
    };

    /**
     * Service connections
     */

    // interfacing with module, serializing commands to device format
    private DeviceControlPort deviceDeviceControlPort;
    private DeviceCommPortListener deviceCommandsListener = new DeviceCommPortListener() {
        /**
         * Receives serialized commands - send it to actual device
         * @param data
         */
        @Override
        public void sendData(byte[] data) {
            btComm.sendData(data);
        }

        // called when port is ready - not used in here
        @Override
        public void onPortReady(int portID) {

        }

        @Override
        public void onCriticalFailure(int portID, Exception e) {

        }
    };

    /**
     * save service connection for unbinding from that service later
     */
    private ServiceConnection btServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BtCellService.BtCellBinder binder =
                    (BtCellService.BtCellBinder) service;
            // get references to communicaiton port
            BtCommPort commPort = binder.getBluetoothCommunicationPort();
            btComm = commPort;
            // use implementation from this hub as port listener
            binder.setCommPortListener(btListener);
            btConnectedSource.onNext(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    /**
     * save connection to DeviceCommandsService command serializer
     */
    private ServiceConnection deviceCommandSerializerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DeviceCommandService.DeviceCommandsBinder binder =
                    (DeviceCommandService.DeviceCommandsBinder) service;
            // get communication port
            deviceDeviceControlPort = binder.getDeviceControlPort();
            binder.setCommandPortListener(deviceCommandsListener);
            // device command parser is connected, inform of it
            commandSerializedConnectedSource.onNext(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        gatherAllComponents();
        return new AppHubBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        gatherAllComponents();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        tearDown();
        super.onDestroy();
    }

    /**
     * Start binding to all components, needed to app to function -
     * bluetooth module,
     */
    private void gatherAllComponents() {
        // check if this service is initialized
        if (initSubscription != null) {
            // initialization is in progress or already finished
            return;
        }
        btConnectedSource = PublishSubject.create();
        commandSerializedConnectedSource = PublishSubject.create();
        connectToBluetoothCommunicationCell();
        connectToDeviceSerializer();
        // wait until all components is initialized
        Observable<Boolean> waitTask =
                Observable.zip(btConnectedSource, commandSerializedConnectedSource,
                        (t1, t2) -> true);
        initSubscription = Observable.defer(() -> waitTask)
                .subscribe(t -> {
                    onAllComponentsConnected();
                });
    }

    /**
     * Disconnect from all components and stop all services;
     */
    private void tearDown() {
        // remove sticky event from EventBus, indicating hub readiness
        EventBus bus = EventBus.getDefault();
        bus.removeStickyEvent(AppHubInitializedEvent.class);
        // disconnect bluetooth module
        unbindService(btServiceConnection);
        unbindService(deviceCommandSerializerConnection);
    }

    /**
     * Bind to Service, which has cell for sending messages via Bluetooth.
     */
    private void connectToBluetoothCommunicationCell() {
        Intent startIntent = new Intent(this, BtCellService.class);
        bindService(startIntent, btServiceConnection, BIND_AUTO_CREATE);
    }

    private void connectToDeviceSerializer() {
        Intent startIntent = new Intent(this, DeviceCommandService.class);
        bindService(startIntent, deviceCommandSerializerConnection, BIND_AUTO_CREATE);
    }

    /**
     * Called when all components is connected to this hub.
     * Post sticky event on EventBus, so all interested entities will know
     */
    private void onAllComponentsConnected() {
        Log.i(LOG_TAG, "All components is initialized");
        EventBus bus = EventBus.getDefault();
        bus.postSticky(new AppHubInitializedEvent());
    }

    /**
     * Inherited from AppFacade
     */

    /**
     * Device control
     */

    @Override
    public void connectToDevice() {
        btComm.startConnection();
    }

    @Override
    public void selectAnotherDevice() {
        btComm.selectAnotherDevice();
    }

    @Override
    public void disconnectFromDevice() {
        btComm.disconnect();
    }

    @Override
    public boolean isDeviceConnected() {
        return btComm.isDeviceConnected();
    }

    @Override
    public void setDeviceListener(AppFacadeDeviceListener listener) {
        deviceListener = listener;
    }

    /**
     * Command serialization section
     */

    @Override
    public void sendCommand(Command command) {
        deviceDeviceControlPort.execute(command);
    }

    @Override
    public void enableEmulation() {
        deviceDeviceControlPort.enableEmulation();
    }

    @Override
    public void disableEmulation() {
        deviceDeviceControlPort.disableEmulation();
    }

    @Override
    public void setEmulationCallback(EmulationCallback emulatedDevice) {
        deviceDeviceControlPort.setCallback(emulatedDevice);
    }





}
