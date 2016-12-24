package alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.serialization;

/**
 * Created by lodoss on 23/12/16.
 */

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceSender;

/**
 * When we process command for saving another command, that command contain data for
 * one or two wrapped commands. We need to get it without sending to actual device.
 * This implementation store result byte array.
 */
public class DataSenderStorage implements DeviceSender {

    private byte[] serializedCommand;

    @Override
    public void sendData(byte[] data) {
        serializedCommand = data;
    }

    public byte[] getSerializedCommand() {
        return serializedCommand;
    }
}
