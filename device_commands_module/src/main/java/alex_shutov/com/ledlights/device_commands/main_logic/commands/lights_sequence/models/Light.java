package alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models;

/**
 * Created by lodoss on 22/12/16.
 */

/**
 * Model for light, active for some time
 */
public class Light {
    private int color;
    private long duration;


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
