package alex_shutov.com.ledlights.HexGeneral;

/**
 * Created by lodoss on 22/07/16.
 */
public abstract class Adapter implements Port {
    private boolean isConnected;

    private PortListener portListener;

    public Adapter(){
        portListener = null;
        isConnected = false;
    }


    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public PortListener getPortListener() {
        return portListener;
    }

    public void setPortListener(PortListener portListener) {
        this.portListener = portListener;
    }
}
