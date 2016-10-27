package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.di;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.EstablishConnectionAlgorithm;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Alex on 10/27/2016.
 */
@Module
public class BtAlgorithmicModule {

    @Provides
    @Singleton
    EstablishConnectionAlgorithm provideEstablishConnectionAlgorithm(){
        EstablishConnectionAlgorithm algorithm = new EstablishConnectionAlgorithm();
        return algorithm;
    }
}
