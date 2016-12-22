package alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.serialization;

/**
 * Created by lodoss on 22/12/16.
 */

import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * Data header for 'lights sequence' command
 */
public class LightsSequenceDataHeader implements DataHeader{
    private boolean isRepeating;
    private int numberOfLights;
    private boolean isSmoothSwitching;

    /**
     * Header has 3 fields, one byte each.
     * @return
     */
    @Override
    public int getHeaderSize() {
        return 3;
    }

    @Override
    public void writeToResult(byte[] result, int offset) {
        result[offset + 0] = (byte) (isRepeating ? 1 : 0 );
        result[offset + 1] = (byte) numberOfLights;
        result[offset + 2] = (byte) (isSmoothSwitching ? 1 : 0);
    }


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
