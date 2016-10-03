package com.alex.hextest.hex.test_logic;

import android.content.Context;
import android.util.Log;

/**
 * Created by lodoss on 03/10/16.
 */
public class TestObjectBSingleton {

    private static final String LOG_TAG = TestObjectBSingleton.class.getSimpleName();

    private String someString;
    private String someString2;
    private Context context;

    public TestObjectBSingleton(Context context){
        Log.i(LOG_TAG, "object created");
        this.context = context;
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    public String getSomeString2() {
        return someString2;
    }

    public void setSomeString2(String someString2) {
        this.someString2 = someString2;
    }

    public Context getContext() {
        return context;
    }


}
