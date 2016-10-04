package alex_shutov.com.ledlights.hex_general.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 24/08/16.
 */

@Module
public class SystemModule {
    private Context context;

    public SystemModule(Context context){
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContext(){
        return context;
    }

}