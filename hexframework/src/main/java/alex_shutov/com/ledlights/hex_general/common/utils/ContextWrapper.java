package alex_shutov.com.ledlights.hex_general.common.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by lodoss on 19/10/16.
 */

public interface ContextWrapper {

    String getString(int resId);

    Context getContext();

    Resources getResources();
}