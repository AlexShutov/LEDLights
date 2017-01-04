package alex_shutov.com.ledlights.service;

/**
 * Created by lodoss on 04/01/17.
 */

import alex_shutov.com.ledlights.service.device_comm.DeviceControl;
import alex_shutov.com.ledlights.service.device_comm.DeviceControlFeedback;

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


}
