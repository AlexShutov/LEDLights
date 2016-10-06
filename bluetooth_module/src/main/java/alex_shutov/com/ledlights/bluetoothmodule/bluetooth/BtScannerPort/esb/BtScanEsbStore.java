package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb;

import java.util.Set;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;

/**
 * Created by lodoss on 06/10/16.
 */
public class BtScanEsbStore {

    public static class ArgumentPairedDevicesReceived {
        public Set<BtDevice> devices;
    }

    public static class ArgumentDeviceFound {
        public BtDevice device;
    }

    public static class ArgumentScanComplete {}
    public static class ArgumentOnScanPortReady{
        public int portId;
    }
    public static class ArgumentOnScanPortFailure{
        public int portId;
        public Exception exception;
    }

}
