package com.alex.hextest.hex.di;

import android.content.Context;

import com.alex.hextest.hex.test_logic.TestObjectA;
import com.alex.hextest.hex.test_logic.TestObjectBSingleton;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 03/10/16.
 */

/**
 * Its methods need Context for creating objects. That is why it depends on
 * SystemModule
 */
@Module(includes = {SystemModule.class})
public class TestLogicModule {

    public TestLogicModule(){
    }

    @Provides
    @Inject
    public TestObjectA provideTestObjectA(Context context){
        TestObjectA ret = new TestObjectA(context);
        return ret;
    }

    @Provides
    @Inject
    @Singleton
    public TestObjectBSingleton provideTestObjectB(Context context){
        TestObjectBSingleton testObjectBSingleton = new TestObjectBSingleton(context);
        return testObjectBSingleton;
    }


}
