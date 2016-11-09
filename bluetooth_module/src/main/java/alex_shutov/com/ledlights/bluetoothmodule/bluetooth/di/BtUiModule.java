package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import javax.inject.Named;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtUiPort.BtUiPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtUiPort.hex.BtUiAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Alex on 11/8/2016.
 */
@Module
public class BtUiModule {

    @Provides
    @Singleton
    public BtUiAdapter provideUiAdapter(){
        BtUiAdapter adapter = new BtUiAdapter();
        return adapter;
    }

    @Provides
    @Singleton
    @Named("ActualInstance")
    public BtUiPort provideBtUiPort(BtUiAdapter adapter){
        return adapter;
    }

}
