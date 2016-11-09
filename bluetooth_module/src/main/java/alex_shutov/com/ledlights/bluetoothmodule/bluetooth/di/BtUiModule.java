package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import android.content.Context;

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
    public BtUiAdapter provideUiAdapter(Context context){
        BtUiAdapter adapter = new BtUiAdapter(context);
        return adapter;
    }

    @Provides
    @Singleton
    public BtUiPort provideBtUiPort(BtUiAdapter adapter){
        return adapter;
    }

}
