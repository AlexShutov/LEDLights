package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 01/12/16.
 */

@Module
public class BtPresenterModule {

    @Provides
    @Singleton
    public AnotherDevicePresenter provideAnotherDeviceSelectionPresenter(EventBus eventBus,
                                                                         Context context) {
        AnotherDevicePresenter presenter = new AnotherDevicePresenter(eventBus, context);
        return presenter;
    }
}
