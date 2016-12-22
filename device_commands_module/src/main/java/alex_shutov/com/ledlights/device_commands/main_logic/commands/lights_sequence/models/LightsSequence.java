package alex_shutov.com.ledlights.device_commands.main_logic.commands.lights_sequence.models;

/**
 * Created by lodoss on 22/12/16.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Model for sequence of lights. It consist of number of lights and repetition options
 */
public class LightsSequence {
    // lights from this sequence
    private List<Light> lights = new ArrayList<>();
    // should this sequence repeat after completion
    private boolean isRepeating;
    // mode for changing color (experimental)
    private boolean isSmoothSwitching;


    public void addLight(Light light) {
        lights.add(light);
    }

    public void clearLightList() {
        lights.clear();
    }

    public List<Light> getLights() {
        return lights;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }

    public boolean isRepeating() {
        return isRepeating;
    }

    public void setRepeating(boolean repeating) {
        isRepeating = repeating;
    }

    public boolean isSmoothSwitching() {
        return isSmoothSwitching;
    }

    public void setSmoothSwitching(boolean smoothSwitching) {
        isSmoothSwitching = smoothSwitching;
    }
}
