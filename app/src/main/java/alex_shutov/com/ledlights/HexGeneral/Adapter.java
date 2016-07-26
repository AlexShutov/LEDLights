package alex_shutov.com.ledlights.HexGeneral;

/**
 * Created by lodoss on 22/07/16.
 */
public abstract class Adapter {
    private boolean isConnected;
    private Port port;
    private PortListener portListener;

    public Adapter(){
        port = null;
        portListener = null;
        isConnected = false;
    }

    void connectToPort(Port port) throws IllegalArgumentException{
        checkIfCompatible(port);
        this.port = port;
        this.portListener = port.getPortListener();
    }

    /**
     * Assume that Adapter can only be connected to single port
     */
    public void disconnectFromPort(){

    }

    public abstract void checkIfCompatible(Port port) throws IllegalArgumentException;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Port getPort() {
        return port;
    }

    public void setPort(Port port) {
        this.port = port;
    }

    public PortListener getPortListener() {
        return portListener;
    }

    public void setPortListener(PortListener portListener) {
        this.portListener = portListener;
    }
}
