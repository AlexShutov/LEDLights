package alex_shutov.com.ledlights.device_commands;


import javax.inject.Inject;
import javax.inject.Named;

import alex_shutov.com.ledlights.device_commands.ControlPort.ControlPort;
import alex_shutov.com.ledlights.device_commands.ControlPort.ControlPortAdapter;
import alex_shutov.com.ledlights.device_commands.ControlPort.EmulationCallback;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPort;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortAdapter;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortListener;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;
import alex_shutov.com.ledlights.device_commands.main_logic.CommandExecutor;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.DeviceEmulationFrame;
import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.CompositeSerializer;
import alex_shutov.com.ledlights.hex_general.LogicCell;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * This cell contains:
 *  - logic, used for converting Application's commands into device commands.
 *  - logic for sending commands to device. The point is, that sending data need some time, so,
 *    we have to wait until current command is sent before attempting to send another command.
 *  - Port, responsible for communication with actual device. This logic cell know nothing about
 *    how connection to device is established and how data transferred.
 *  - Emulation of device. User may not have assembled device yet (DIY), but want to see if
 *    this app is a good idea and worth buying details and soldering a device. To do so we need
 *    to emulate device workflow (UI will show changes in device state).
 */
public class DeviceCommandsLogicCell extends LogicCell {
    private EmulationCallback dummyEmulatedDevice = new EmulationCallback() {
        @Override
        public void onLEDColorChanged(int color) {}

        @Override
        public void onStrobeOn() {}

        @Override
        public void onStrobeOff() {}
    };

    private DeviceCommPortAdapter commPortAdapter;
    private ControlPortAdapter controlPortAdapter;
    // Reference to interface for sending serialized command to connected device.
    // DeviceCommPortAdapter adapt this interface.
    private DeviceSender sendInterface;

    /**
     * This is a composite command executor, containing serializers for all commands.
     */
    @Inject
    @Named("CommandSerializationStore")
    CompositeSerializer serializationStore;

    @Inject
    @Named("CommandEmulator")
    DeviceEmulationFrame deviceEmulator;

    @Inject
    @Named("CommandCellExecutor")
    CommandExecutor topLevelComposite;

    /**
     * Initialize components, used in this logic cell in this method
     */
    @Override
    public void init() {
        // initialize port adapters
        commPortAdapter.initialize();
        controlPortAdapter.initialize();

        // initialize device emulator
        deviceEmulator.init();
        // connection adapter adapt external device emulation interface to
        // used internally EmulatedDeviceControl interface, set it to device emulator.
        deviceEmulator.setDevice(controlPortAdapter);

        // use adapter to output port for sending data.
        sendInterface = commPortAdapter;
        // connect serializers to device interface
        serializationStore.setDeviceSender(sendInterface);

        // set this logic cell as executor for control port after all real executors is
        // ready (serialization store and device emulator).
        controlPortAdapter.setExecutor(topLevelComposite);
        // tell connection adapter that emulator control emulations.
        controlPortAdapter.setEmulationControl(deviceEmulator);
    }

    /**
     * Stop activity of all components here - app is about to be destroyed
     */
    @Override
    public void suspend() {
        // suspend device emulator
        deviceEmulator.suspend();
    }

    /**
     * Initialize all objects in this cell by DI
     */
    @Override
    protected void injectThisCell() {
        // get reference to DI component
        DeviceCommandsPortAdapterCreator creator =
                (DeviceCommandsPortAdapterCreator) getAdaperCreator();
        // init all objects
        creator.injectLogicCell(this);
    }

    /**
     * Inherited from CommandExecutor
     */

    // accessors

    /**
     * Communication port
     */
    public DeviceCommPort getCommPort() {
        return commPortAdapter;
    }

    public void setCommPortAdapter(DeviceCommPortAdapter commPortAdapter) {
        this.commPortAdapter = commPortAdapter;
    }

    /**
     * Control port
     */

    public ControlPort getControlPort() {
        return controlPortAdapter;
    }

    public void setControlPortAdapter(ControlPortAdapter controlPortAdapter) {
        this.controlPortAdapter = controlPortAdapter;
    }

    public void setEmulationCallback(EmulationCallback callback) {
        controlPortAdapter.setCallback( null == callback ? dummyEmulatedDevice : callback);
    }

    /**
     * Set port listener to adapter, responsible for external communications.
     * @param listener
     */
    public void setDeviceCommPortListener(DeviceCommPortListener listener) {
        commPortAdapter.setPortListener(listener);
    }



}
