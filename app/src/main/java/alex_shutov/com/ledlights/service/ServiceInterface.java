package alex_shutov.com.ledlights.service;

/**
 * Created by lodoss on 04/01/17.
 */

import alex_shutov.com.ledlights.device_commands.ControlPort.EmulationCallback;
import alex_shutov.com.ledlights.device_commands.main_logic.Command;
import alex_shutov.com.ledlights.device_commands.main_logic.emulation_general.EmulationControl;
import alex_shutov.com.ledlights.service.device_comm.DeviceControl;
import alex_shutov.com.ledlights.service.device_comm.DeviceControlFeedback;
import rx.Observable;

/**
 * Interface for communicating with Service, having all objects
 */
public interface ServiceInterface {

    /**
     * Section for controlling device communication
     */

    /**
     * Access interface for controlling device
     * @return
     */
    DeviceControl getDeviceControl();

    /**
     * Set listener for tracking changes in device state.
     * @param deviceControlFeedback
     */
    void setDeviceControlFeedback(DeviceControlFeedback deviceControlFeedback);

    /**
     * Get implementation of interface, turning on and off emulation.
     * @return
     */
    EmulationControl getEmulationControl();

    /**
     * Connect some view of emulated device to this Service. Don't forget to detach it
     * when UI goes out of scope and attach it again.
     * @param device
     */
    void setEmulatedDevice(EmulationCallback device);

    void execute(Command command);

    /**
     * Specify if application has to connect at startup (true by default
     * @param needToConnect
     */
    void setConnectAtStart(boolean needToConnect);

    /**
     * @return if true, app will try to connect at startup
     */
    Observable<Boolean> isConnectingAtStart();

}
