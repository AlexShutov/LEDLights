package alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.serialization;

import alex_shutov.com.ledlights.device_commands.main_logic.serialization_general.DataHeader;

/**
 * Created by lodoss on 22/12/16.
 */

public class StrobeDataHeader implements DataHeader {

    private boolean permanent;
    private boolean isOn;
    private boolean repeat;
    private int numberOfFlashes;

    /**
     * Inherited from DataHeader
     */

    /**
     * 1 byte for each field.
     * @return
     */
    @Override
    public int getHeaderSize() {
        return 4;
    }

    @Override
    public void writeToResult(byte[] result, int offset) {
        result[offset + 0] = (byte) (permanent ? 1 : 0);
        result[offset + 1] = (byte) (isOn ? 1 : 0);
        result[offset + 2] = (byte) (repeat ? 1 : 0);
        result[offset + 3] = (byte) numberOfFlashes;
    }

    // accessors


    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public int getNumberOfFlashes() {
        return numberOfFlashes;
    }

    public void setNumberOfFlashes(int numberOfFlashes) {
        this.numberOfFlashes = numberOfFlashes;
    }
}
