package alex_shutov.com.ledlights.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import alex_shutov.com.ledlights.device_commands.ControlPort.ControlPort;
import alex_shutov.com.ledlights.device_commands.ControlPort.EmulationCallback;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationControl;
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
        // connect to device once service is started
        DeviceControl deviceControl = getDeviceControl();
        deviceControl.connectToDevice();
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
}
