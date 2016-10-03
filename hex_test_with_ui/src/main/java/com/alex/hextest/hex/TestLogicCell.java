package com.alex.hextest.hex;
import android.util.Log;

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


    public TestPort getTestPort(){
        Log.i(LOG_TAG, "getTestPort()");
        return testPortAdapter;
    }


    @Override
    public void init() {
        Log.i(LOG_TAG, "init() method is called");

    }

}
