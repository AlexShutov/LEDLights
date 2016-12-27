package alex_shutov.com.ledlights.device_commands.di;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.device_commands.ControlPort.DeviceControlPortAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Alex on 12/24/2016.
 */

@Module
public class ControlPortModule {
    @Provides
    @Singleton
    DeviceControlPortAdapter provideControlPortAdapter() {
        DeviceControlPortAdapter adapter = new DeviceControlPortAdapter();
        return adapter;
    }

}
