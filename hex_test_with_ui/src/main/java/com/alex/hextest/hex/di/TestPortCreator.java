package com.alex.hextest.hex.di;

import com.alex.hextest.hex.TestCellDeployer;
import com.alex.hextest.hex.TestLogicCell;

import javax.inject.Singleton;

import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;
import dagger.Component;

/**
 * Created by lodoss on 03/10/16.
 */

@Singleton
@Component(modules = { SystemModule.class,
        TestLogicModule.class
})
public interface TestPortCreator extends PortAdapterCreator {
    void injectTestLogicCell(TestLogicCell testLogicCell);
    void injectTestCellDeployed(TestCellDeployer testCellDeployer);

}
