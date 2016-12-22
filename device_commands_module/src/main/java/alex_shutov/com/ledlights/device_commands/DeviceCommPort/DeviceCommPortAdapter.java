package alex_shutov.com.ledlights.device_commands.DeviceCommPort;

import android.util.Log;

import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * LogicCell does actual job, this Adapter only receives response and in informs cell of it.
 ! Initial idea was to introduce feedback to device - device send '+' symbol if sending is
 * successful and '-' or nothing otherwise. DeviceCommPortAdapter meant to track that feedback
 * with some timeout and send next command only when previous command is sent or sending failed.
 * If device send no response to command, this adapter should retry to send that command.
 * Application were supposed to be having command queue. When current command is sent, another is
 * taken from the queue and processed. This approach was half- implemented (easy with rxJava), but
 * there was a bug in device ('+' isn't always sent), so I decided (I know it's bad) to keep it
 * simple. By the way, this is very overengineered, considering environment device is supposed
 * to work in (careering motorcycle). We just send command without any feedback.
 */
public class DeviceCommPortAdapter extends Adapter implements DeviceCommPort,
        DeviceSender {


    @Override
    public PortInfo getPortInfo() {
        PortInfo portInfo = new PortInfo();
        portInfo.setPortCode(PortInfo.PORT_DEVICE_COMMANDS_COMM);
        portInfo.setPortDescription("Port for sending command to device (as byte array)");
        return portInfo;
    }

    @Override
    public void initialize() {

    }

    /**
     * Inherited from DeviceCommPort
     */

    @Override
    public void onDataSent() {
        Log.i("iosjoif", "oijsdofi");
    }

    @Override
    public void onResponse(byte[] response) {

    }

    /**
     * Inherited from DeviceSender
     */

    /**
     * Method, called by LogicCell for sending data to connected device.
     * Redirect call to external listener
     * @param data
     */
    @Override
    public void sendData(byte[] data) {
        DeviceCommPortListener listener = (DeviceCommPortListener) getPortListener();
        listener.sendData(data);
    }

}

