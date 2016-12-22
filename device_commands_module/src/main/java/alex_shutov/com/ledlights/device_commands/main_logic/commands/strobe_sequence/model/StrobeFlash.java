package alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.model;

/**
 * Created by lodoss on 22/12/16.
 */

/**
 * Class for modelling strobe flash. StrobeFlash is a combination of times when strobe is ON and OFF.
 * StrobeFlash is modelled by specifying ON and OFF times, not duty cycle.
 */
public class StrobeFlash {

    private long timeOn;
    private long timeOff;


    public long getTimeOn() {
        return timeOn;
    }

    public void setTimeOn(long timeOn) {
        this.timeOn = timeOn;
    }

    public long getTimeOff() {
        return timeOff;
    }

    public void setTimeOff(long timeOff) {
        this.timeOff = timeOff;
    }
}
