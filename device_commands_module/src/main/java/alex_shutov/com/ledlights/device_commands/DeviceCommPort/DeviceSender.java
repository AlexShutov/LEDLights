package alex_shutov.com.ledlights.device_commands.DeviceCommPort;

/**
 * Created by lodoss on 22/12/16.
 */

/**
 * This is an internal interface for sending serialized command to device.
 * The point is, that DeviceCommPort is an external interface to this logic cell, but cell
 * itself use this interface DeviceSender for sending data.
 */
public interface DeviceSender {

    void sendData(byte[] data);
}
