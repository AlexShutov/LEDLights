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
     * We assume that Adapter can only be connected to single port
     */
    public void disconnectFromPort(){

    }

    public abstract void checkIfCompatible(Port port) throws IllegalArgumentException;
}
