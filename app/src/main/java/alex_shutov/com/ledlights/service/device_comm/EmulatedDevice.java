package alex_shutov.com.ledlights.service.device_comm;

/**
 * Created by lodoss on 04/01/17.
 */

public interface EmulatedDevice {

    void onLEDColorChanged(int color);

    void onStrobeOn();

    void onStrobeOff();
}
