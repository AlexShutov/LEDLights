package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.di;


import javax.inject.Inject;
import javax.inject.Singleton;

import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.TestPortAdapter;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectA;
import alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic.TestObjectBSingleton;
import dagger.Module;
import dagger.Provides;

/**
 * Created by lodoss on 03/10/16.
 */

/**
 * Its methods need Context for creating objects. That is why it depends on
 * SystemModule
 */
@Module
public class TestLogicModule {

    public TestLogicModule(){
    }

    @Provides
    @Inject
    public TestObjectA provideTestObjectA(){
        TestObjectA ret = new TestObjectA();
        return ret;
    }

    @Provides
    @Inject
    @Singleton
    public TestObjectBSingleton provideTestObjectB(){
        TestObjectBSingleton testObjectBSingleton = new TestObjectBSingleton();
        return testObjectBSingleton;
    }

    @Provides
    @Inject
    @Singleton
    public TestPortAdapter provideTestPortAdapter(){
        TestPortAdapter testPortAdapter = new TestPortAdapter();
        return testPortAdapter;
    }




}
