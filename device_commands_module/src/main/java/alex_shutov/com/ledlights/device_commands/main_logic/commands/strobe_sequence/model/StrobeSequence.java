package alex_shutov.com.ledlights.device_commands.main_logic.commands.strobe_sequence.model;

import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models.Light;

/**
 * Created by lodoss on 22/12/16.
 */

public class StrobeSequence {
    private boolean permanent;
    private boolean isOn;
    private boolean repeat;

    private List<StrobeFlash> flashes = new ArrayList<>();

    public void clearFlashes() {
        flashes.clear();
    }

    public void addFlash(StrobeFlash flash) {
        flashes.add(flash);
    }

    // accessors

    public List<StrobeFlash> getFlashes() {
        return flashes;
    }

    public void setFlashes(List<StrobeFlash> flashes) {
        this.flashes = flashes;
    }

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

}
