package com.alex.hextest.hex.test_logic;

import android.content.Context;
import android.util.Log;

/**
 * Created by lodoss on 03/10/16.
 */
public class TestObjectA {

    private static final String LOG_TAG = TestObjectA.class.getSimpleName();

    private String someString;
    private Integer someInt;
    private Long someLong;
    private Context deviceContext;

    public TestObjectA(Context context){
        Log.i(LOG_TAG, "Object created");
        this.deviceContext = context;
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    public Integer getSomeInt() {
        return someInt;
    }

    public void setSomeInt(Integer someInt) {
        this.someInt = someInt;
    }

    public Long getSomeLong() {
        return someLong;
    }

    public void setSomeLong(Long someLong) {
        this.someLong = someLong;
    }

    public Context getDeviceContext() {
        return deviceContext;
    }
}
