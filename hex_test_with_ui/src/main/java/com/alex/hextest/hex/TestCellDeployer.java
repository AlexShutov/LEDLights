package com.alex.hextest.hex;

import android.content.Context;

import com.alex.hextest.hex.di.DaggerTestPortCreator;
import com.alex.hextest.hex.di.SystemModule;
import com.alex.hextest.hex.di.TestLogicModule;
import com.alex.hextest.hex.di.TestPortCreator;

import alex_shutov.com.ledlights.hex_general.CellDeployer;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.PortAdapterCreator;

/**
 * Created by lodoss on 03/10/16.
 */
public class TestCellDeployer extends CellDeployer {
    // We need context, because CellDeployer creates di component, responsible for
    // injecting context
    private Context context;

    private SystemModule systemModule;
    private TestLogicModule testLogicModule;

    public TestCellDeployer(Context context){
        this.context = context;
    }

    /**
     * In this method we have to create Dagger2 component, which will be
     * instantiating all objects inside this logic cell TestLogicCell
     * @return
     */
    @Override
    public PortAdapterCreator createPortCreator() {
        // port creator is dagger component. create modules, used by it.
        createModules();
        TestPortCreator portCreator = DaggerTestPortCreator.builder()
                .systemModule(systemModule)
                .testLogicModule(testLogicModule)
                .build();
        return portCreator;
    }

    private void createModules(){
        systemModule = new SystemModule(context);
        testLogicModule = new TestLogicModule();
    }

    @Override
    public void connectPorts(LogicCell logicCell) {

    }

}
