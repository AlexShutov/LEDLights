package alex_shutov.com.ledlights.hex_general.hexagonal_basics_tests.hex.test_logic;

import android.util.Log;

/**
 * Created by lodoss on 03/10/16.
 */
public class TestObjectA {

    private static final String LOG_TAG = TestObjectA.class.getSimpleName();

    private String someString;
    private Integer someInt;
    private Long someLong;

    public TestObjectA(){
        Log.i(LOG_TAG, "Object created");
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

}
