package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.select_another_device_strategy.events;

/**
 * Created by lodoss on 15/12/16.
 */

/**
 * We cannot use Context for closing Activity from outside that Activity. To do so, I post
 * event to EventBus, which is received by Activity
 */
public class HideDeviceSelectionUiEvent {
}
