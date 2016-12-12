package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.events;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;

/**
 * Created by lodoss on 12/12/16.
 */

public class ConnectionAttemptFailedEvent {
    private BtDevice device;

    public BtDevice getDevice() {
        return device;
    }

    public void setDevice(BtDevice device) {
        this.device = device;
    }
}
