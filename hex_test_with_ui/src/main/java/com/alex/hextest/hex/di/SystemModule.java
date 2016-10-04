package com.alex.hextest.hex.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 03/10/16.
 */
@Module
public class SystemModule {
    private Context context;

    public SystemModule(Context context){
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideContext(){
        return context;
    }


}
