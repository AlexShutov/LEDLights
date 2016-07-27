package alex_shutov.com.ledlights.HexGeneral;

/**
 * Created by lodoss on 22/07/16.
 */
public class PortInfo {

    public static final int PORT_BLUETOOTH_RAW = 1;

    /**
     * we may want to assign numbers to ports
     */
    private int portCode;
    private String portDescription;

    public PortInfo(){
        portCode = -1;
        portDescription = "";
    }

    public int getPortCode() {
        return portCode;
    }

    public void setPortCode(int portCode) {
        this.portCode = portCode;
    }

    public String getPortDescription() {
        return portDescription;
    }

    public void setPortDescription(String portDescription) {
        this.portDescription = portDescription;
    }
}