package alex_shutov.com.ledlights.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import alex_shutov.com.ledlights.service.device_comm.CommFacade;
import alex_shutov.com.ledlights.service.device_comm.DeviceControl;
import alex_shutov.com.ledlights.service.device_comm.DeviceControlFeedback;

/**
 * Created by lodoss on 04/01/17.
 */
public class BackgroundService extends Service implements ServiceInterface {

    private static final String LOG_TAG = BackgroundService.class.getSimpleName();

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

    /**
     * Inherited from Service.
     */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        initialize();
        return null;
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
        commFacade = new CommFacade(this);
        commFacade.start();
        // connect to device once service is started
        DeviceControl deviceControl = getDeviceControl();
        deviceControl.connectToDevice();

    }




}
