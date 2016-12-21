package alex_shutov.com.ledlights.device_commands.DeviceCommPort;

import android.renderscript.Script;
import android.util.Log;

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.response.ResponseParser;
import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * LogicCell does actual job, this Adapter only receives response and in informs cell of it
 */
public class DeviceCommPortAdapter extends Adapter implements DeviceCommPort {

    private ResponseParser responseParser;

    public DeviceCommPortAdapter(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }

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

    /**
     * Parse response from device and give result to callback
     * @param response
     */
    @Override
    public void onResponse(byte[] response) {
        boolean isCommandAcceptedByDevice = responseParser.isOperationSuccessful(response);
        Log.i("oisdjfios", String.valueOf(isCommandAcceptedByDevice));
    }



}

