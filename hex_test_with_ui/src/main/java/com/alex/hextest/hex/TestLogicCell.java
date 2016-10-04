package com.alex.hextest.hex;
import android.util.Log;

import com.alex.hextest.hex.di.TestPortCreator;
import com.alex.hextest.hex.test_logic.TestObjectA;
import com.alex.hextest.hex.test_logic.TestObjectBSingleton;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamResult;

import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.Port;

/**
 * Created by lodoss on 03/10/16.
 */
public class TestLogicCell extends LogicCell {

    private static final String LOG_TAG = TestLogicCell.class.getSimpleName();

    @Inject
    public TestPortAdapter testPortAdapter;
    @Inject
    public TestObjectA testObjectA;
    @Inject
    public TestObjectBSingleton testObjectBSingleton;




    public TestPort getTestPort(){
        Log.i(LOG_TAG, "getTestPort()");
        return testPortAdapter;
    }


    @Override
    public void init() {
        Log.i(LOG_TAG, "init() method is called");


    }


    @Override
    protected void injectThisCell() {
        Log.i(LOG_TAG, "Injecting logi cell TestLogicCell ");
        TestPortCreator portCreator = (TestPortCreator) getAdaperCreator();
        portCreator.injectTestLogicCell(this);
    }
}
