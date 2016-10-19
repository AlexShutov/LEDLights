package alex_shutov.com.ledlights.hex_general.common.utils.impl;

import android.content.Context;
import android.content.res.Resources;

import com.lodoss.childtracker.utils.ContextWrapper;

/**
 * Created by lodoss on 19/10/16.
 */

public class ContextWrapperImpl implements ContextWrapper {

    private Context mContext;

    public ContextWrapperImpl(Context c){
        mContext = c;
    }

    @Override
    public String getString(int resId) {
        if(mContext == null){
            return "";
        }
        return mContext.getString(resId);
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public Resources getResources() {
        return mContext.getResources();
    }
}
