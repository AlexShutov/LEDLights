package alex_shutov.com.ledlights.device_commands.di;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.device_commands.DeviceCommPort.DeviceCommPortAdapter;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.response.ResponseParser;
import alex_shutov.com.ledlights.device_commands.DeviceCommPort.response.ResponseParserImpl;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 21/12/16.
 */

@Module
public class CommPortModule {

    /**
     * Create adapter, which will be sending data to actual device.
     * @return
     */
    @Provides
    @Singleton
    DeviceCommPortAdapter provideCommAdapter(ResponseParser responseParser) {
        DeviceCommPortAdapter adapter = new DeviceCommPortAdapter(responseParser);
        return adapter;
    }

    @Provides
    @Singleton
    public ResponseParser provideResponseParser() {
        ResponseParserImpl responseParser = new ResponseParserImpl();
        return responseParser;
    }

}
