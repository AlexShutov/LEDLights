package alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.serialization;

/**
 * Created by lodoss on 22/12/16.
 */

/**
 * Data header for 'lights sequence' command
 */
public class LightsSequenceDataHeader {
    private boolean isRepeating;
    private int numberOfLights;
    private boolean isSmoothSwitching;

    public boolean isRepeating() {
        return isRepeating;
    }

    public void setRepeating(boolean repeating) {
        isRepeating = repeating;
    }

    public int getNumberOfLights() {
        return numberOfLights;
    }

    public void setNumberOfLights(int numberOfLights) {
        this.numberOfLights = numberOfLights;
    }

    public boolean isSmoothSwitching() {
        return isSmoothSwitching;
    }

    public void setSmoothSwitching(boolean smoothSwitching) {
        isSmoothSwitching = smoothSwitching;
    }
}
