package alex_shutov.com.ledlights.device_commands.di;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 21/12/16.
 */

@Module
public class CellModule {

    /**
     * Create adapter, which will be sending data to actual device.
     * @return
     */
    @Provides
    @Singleton
    DeviceCommPortAdapter provideCommAdapter() {
        DeviceCommPortAdapter adapter = new DeviceCommPortAdapter();
        return adapter;
    }
}
