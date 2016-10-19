package alex_shutov.com.ledlights.hex_general.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * Created by user on 08/02/16.
 */
public interface UiUtil {

    void hideKeyboard(Activity activity);

    void showKeyboard(Activity activity);
    void showKeyboard(Activity activity, View targetView);

    void changeSpinnerTriangleColor(Spinner spinner, Resources r, int colorResId);

    Animation getAnimation(Context c, int animresId);


    void makeWidthForBothOrientation(View targetView, int portraitWidth, int landscapeWidth, Resources r);

    void makeHeightForBothOrientation(View targetView, int portraitHeight, int landscapeHeight, Resources r);

    /**
     * in portrait mode, content height will be <code>portraitModeHeight</code>, in landscape mode it will be above <code>bottomAnchor</code>
     * @param targetView
     * @param lp
     * @param portraitModeHeight
     * @param bottomAnchor
     */
    void makeDialogContentHeightFromOrientation(View targetView, RelativeLayout.LayoutParams lp, int portraitModeHeight, int bottomAnchor, Resources r);


    int getScreenWidth(Activity a);

    int getScreenHeight(Activity a);

    Drawable getDrawable(int resourceId, Context c);

    void setDrawable(View view, Drawable target);

}
