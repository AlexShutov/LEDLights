package alex_shutov.com.ledlights.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import alex_shutov.com.ledlights.device_commands.ControlPort.ControlPort;
import alex_shutov.com.ledlights.device_commands.ControlPort.EmulationCallback;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationControl;
import alex_shutov.com.ledlights.service.device_comm.CommFacade;
import alex_shutov.com.ledlights.service.device_comm.DeviceControl;
import alex_shutov.com.ledlights.service.device_comm.DeviceControlFeedback;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by lodoss on 04/01/17.
 */
public class BackgroundService extends Service implements ServiceInterface {
    private static final String LOG_TAG = BackgroundService.class.getSimpleName();
    private static final String PREFS_NAME = "SERVICE_PREFS";
    // save flag into SharedPreferences if app should try to connect when it is started
    private static final String PREFS_KEY_DONT_CONNECT_ON_START = "PREFS_KEY_DONT_CONNECT_ON_START";

    public class Binder extends android.os.Binder {

        /**
         * Get interface to service, running in background.
         * @return
         */
        public ServiceInterface getServiceInterface() {
            return BackgroundService.this;
        }
    }

    private CommFacade commFacade;

    private Binder binder = new Binder();

    /**
     * Inherited from Service.
     */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        initialize();
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        commFacade.teardown();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * Inherited from ServiceInterface
     */

    @Override
    public DeviceControl getDeviceControl() {
        return commFacade;
    }

    @Override
    public void setDeviceControlFeedback(DeviceControlFeedback deviceControlFeedback) {
        commFacade.setControlFeedback(deviceControlFeedback);
    }

    private void initialize() {
        if (null != commFacade) {
            return;
        }
        commFacade = new CommFacade(this);
        commFacade.start();
        // check if user prefer connecting to device at start
        isConnectingAtStart()
                .filter(t -> t == true)
                // and connect needed
                .subscribe(t -> {
                    // connect to device once service is started
                    DeviceControl deviceControl = getDeviceControl();
                    deviceControl.connectToDevice();
                });
    }

    @Override
    public EmulationControl getEmulationControl() {
        ControlPort controlPort = commFacade.getCommandControlPort();
        return controlPort;
    }

    @Override
    public void setEmulatedDevice(EmulationCallback device) {
        ControlPort controlPort = commFacade.getCommandControlPort();
        controlPort.setCallback(device);
    }

    @Override
    public void execute(Command command) {
        ControlPort controlPort = commFacade.getCommandControlPort();
        controlPort.execute(command);
    }

    /**
     * Remove old flag from SharedPreferences and put it again if user doesn't want to
     * connect without approval.
     * @param needToConnect
     */
    @Override
    public void setConnectAtStart(boolean needToConnect) {
        Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.io())
                .subscribe(t -> {
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor e = prefs.edit();
                    e.remove(PREFS_KEY_DONT_CONNECT_ON_START);
                    if (!needToConnect) {
                        e.putBoolean(PREFS_KEY_DONT_CONNECT_ON_START, true);
                    }
                    e.commit();
                });
    }

    /**
     * Get user's preference about connecting at program start (in background)
     * @return
     */
    @Override
    public Observable<Boolean> isConnectingAtStart() {
        return Observable.defer(() -> Observable.just(""))
                .subscribeOn(Schedulers.io())
                .map(t -> {
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    // connect if preferences doesn't have a suppression flag
                    boolean connect = !prefs.contains(PREFS_KEY_DONT_CONNECT_ON_START);
                    return connect;
                });
    }
}
