package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.events;

/**
 * Created by lodoss on 01/12/16.
 */

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;

/**
 * View Obtains instance to Presenter from EventBus event. I don't pass reference to
 * DI component, because View doesn't have to know everything.
 */
public class PresenterInstanceEvent {
    private AnotherDevicePresenter presenter;

    public AnotherDevicePresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(AnotherDevicePresenter presenter) {
        this.presenter = presenter;
    }
}
