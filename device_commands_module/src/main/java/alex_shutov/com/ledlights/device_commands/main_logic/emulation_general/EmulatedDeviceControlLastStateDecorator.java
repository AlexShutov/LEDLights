package alex_shutov.com.ledlights.device_commands.main_logic.emulation_general;

import android.graphics.Color;

/**
 * Created by Alex on 12/26/2016.
 */

/**
 * Device emulation is running in background Service, but UI bind to that service. At that
 * point UI doesn't know current device state (LED color, if strobe is ON or OFF).
 * We need to update UI with current state.
 * This decorator know current device state and can give it to device on request.
 * When user bind to Service, he will set UI as emulated device to that service and
 * emulation frame will tell this decorator to give current device state.
 */
public class EmulatedDeviceControlLastStateDecorator implements EmulatedDeviceControl {

    private int currColor;
    private boolean isStrobeOn;
    private boolean isInitialized;

    private EmulatedDeviceControl decoree;

    public EmulatedDeviceControlLastStateDecorator(){
        isInitialized = false;
        isStrobeOn = false;
        currColor = Color.BLACK;
        decoree = null;
    }

    @Override
    public void setColor(int color) {
        initialize();
        currColor = color;
        decoree.setColor(color);
    }

    @Override
    public void turnStrobeOn() {
        initialize();
        isStrobeOn = true;
        decoree.turnStrobeOn();
    }

    @Override
    public void turnStrobeOff() {
        initialize();
        isStrobeOn = false;
        decoree.turnStrobeOff();
    }

    public void feedCurrentState() {
        if (isInitialized) {
            decoree.setColor(currColor);
            if (isStrobeOn) {
                decoree.turnStrobeOn();
            } else {
                decoree.turnStrobeOff();
            }
        }
    }

    public void resetState() {
        isInitialized = false;
    }

    // accessors

    public int getCurrColor() {
        return currColor;
    }

    public void setCurrColor(int currColor) {
        this.currColor = currColor;
    }

    public boolean isStrobeOn() {
        return isStrobeOn;
    }

    public void setStrobeOn(boolean strobeOn) {
        isStrobeOn = strobeOn;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public EmulatedDeviceControl getDecoree() {
        return decoree;
    }

    public void setDecoree(EmulatedDeviceControl decoree) {
        this.decoree = decoree;
    }

    /**
     * Mark as initialize if it is not
     */
    private void initialize() {
        if (!isInitialized) {
            isInitialized = true;
        }
    }
}
