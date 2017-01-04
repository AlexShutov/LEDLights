package alex_shutov.com.ledlights.service.device_comm;

import android.content.Context;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCellDeployer;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtLogicCell;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPort;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortListener;
import alex_shutov.com.ledlights.device_commands.DeviceCommandsCellDeployer;
import alex_shutov.com.ledlights.device_commands.DeviceCommandsLogicCell;

/**
 * Created by lodoss on 04/01/17.
 */

public class CommFacade implements DeviceControl {

    private Context context;

    /**
     * Logic cell, responsible for Bluetooth communication
     */
    private BtLogicCell btCell;
    private BtCellDeployer btCellDeployer;

    private DeviceCommandsLogicCell commCell;
    private DeviceCommandsCellDeployer commCellDeployer;

    private BtCommPort btCommPort;

    /**
     * Used for feedback to user
     */
    private DeviceControlFeedback controlFeedback;


    /**
     * Listener, used for Bluetooth feedback. It informs application about bluetooth events.
     */
    /**
     * Listener, responding to events from Bluetooth cell.
     */
    private BtCommPortListener btCommPortListener = new BtCommPortListener() {
        @Override
        public void onConnectionStarted(BtDevice btDevice) {

        }

        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onReconnected(BtDevice btDevice) {

        }

        @Override
        public void onDummyDeviceSelected() {

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

    private DeviceCommPort commPort;

    /**
     * Callback for device commands logic. It output byte arrays in format, which
     * device can understand.
     */
    private DeviceCommPortListener commPortListener = new DeviceCommPortListener() {
        @Override
        public void sendData(byte[] data) {

        }

        @Override
        public void onPortReady(int portID) {

        }

        @Override
        public void onCriticalFailure(int portID, Exception e) {

        }
    };


    public CommFacade(Context context) {
        this.context = context;
    }

    /**
     * Inherited from DeviceControl
     */

    @Override
    public void connectToDevice() {
        btCommPort.startConnection();
    }

    @Override
    public void selectAnotherDevice() {
        btCommPort.selectAnotherDevice();
    }

    @Override
    public boolean isDeviceConnected() {
        return btCommPort.isDeviceConnected();
    }

    @Override
    public void disconnect() {
        btCommPort.disconnect();
    }

    public void setControlFeedback(DeviceControlFeedback controlFeedback) {
        this.controlFeedback = controlFeedback;
    }

    /**
     * Initialize all objects within this Service. Initialization can be done
     */
    public void start() {
        if (null != btCell) {
            return;
        }
        createBtCell();
        // initialize commands cell
        createCommandCell();
    }


    public void teardown() {
        btCell.suspend();
        commCell.suspend();
    }


    private void createBtCell() {
        // initialize bluetooth cell
        btCell = new BtLogicCell();
        btCellDeployer = new BtCellDeployer(context);
        btCellDeployer.deploy(btCell);
        // get reference to communication port
        btCommPort = btCell.getBtCommPort();
        btCell.setBtCommPortListener(btCommPortListener);
    }

    private void createCommandCell() {
        commCell = new DeviceCommandsLogicCell();
        commCellDeployer = new DeviceCommandsCellDeployer();
        commCellDeployer.deploy(commCell);
        // get reference to communicaiton port
        commPort = commCell.getCommPort();
        // and set cell listener
        commCell.setDeviceCommPortListener(commPortListener);
    }

}
