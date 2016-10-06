package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtDevice;
import alex_shutov.com.ledlights.hex_general.Port;

/**
 * Created by Alex on 7/25/2016.
 */

/**
 * This interface is very similar to 'BluetoothChatService'. The difference is that
 * communication with device ( input side) is moved from custom 'Handler' into
 * 'PortListener'
 */
public interface BtConnPort extends Port {

    public String getUuidSecure();
    void setUuidSecure(String uuidSecure) throws IllegalStateException;

    String getUuidInsecure();
    void setUuidInsecure(String uuidInsecure) throws IllegalStateException;

    String getNameSecure();
    void setNameSecure(String nameInsecure) throws IllegalStateException;

    String getNameInsecure();
    void setNameInsecure(String nameInsecure) throws IllegalStateException;

    /**
     * Tell bluetooth device to begin accepting incoming connections matching
     * Port's parameters (uuid, address) - the same as 'start()' method in
     * BluetoothChatService
     */
    void startListening();

    /**
     * Work as 'stop()' method from BluetoothChatService - cancel all
     * connection ( and inform
     */
    void close();

    /**
     * Check if port has device connected to it
     * @return
     */
    boolean isBtConnected();

    /**
     * try connecting to Bluetooth device.
     * TODO: DO NOT use BluetoothDevice because it depend on implementation - use
     * TODO: some own class carrying information about device instead
     * Dispatch call to BluetoothChatService's namesake
     * @param device
     */
    void connect(BtDevice device);

    /**
     * Connection may take too long by some reason - in this case we should cancel that
     * connection
     */
    void stopConnecting();

    /**
     * send some data via Bluetooth device (receiving side is
     * handled in BtConnPortListener)
     * @param out
     */
    void writeBytes(byte[] out);


}
