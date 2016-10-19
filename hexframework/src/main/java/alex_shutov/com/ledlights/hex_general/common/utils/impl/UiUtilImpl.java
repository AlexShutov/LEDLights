package alex_shutov.com.ledlights.hex_general.common.utils.impl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.lodoss.childtracker.utils.ContextWrapper;
import com.lodoss.childtracker.utils.UiUtil;

/**
 * Created by user on 08/02/16.
 */
public class UiUtilImpl implements UiUtil {

    final int ORIENTATION_PORTRAIT = 1;
    final int ORIENTATION_LANDSCAPE = 2;
    private ContextWrapper cw;

    public UiUtilImpl(ContextWrapper cw){
        this.cw = cw;
    }

    @Override
    public void hideKeyboard(Activity activity) {
        if(activity == null){
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void showKeyboard(Activity activity) {
        if(activity == null){
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
//        imm.showSoftInput(view, 0);

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void showKeyboard(Activity activity, View targetView) {
        if(activity == null){
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (targetView == null) {
            return;
        }
        imm.showSoftInput(targetView, 0);
    }

    @Override
    public void changeSpinnerTriangleColor(Spinner spinner, Resources r, int colorResId) {
        Drawable spinnerDrawable = spinner.getBackground().getConstantState().newDrawable();

        spinnerDrawable.setColorFilter(r.getColor(colorResId), PorterDuff.Mode.SRC_ATOP);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setBackground(spinnerDrawable);
        }else{
            spinner.setBackgroundDrawable(spinnerDrawable);
        }
    }

    @Override
    public Animation getAnimation(Context c, int animresId) {
        return AnimationUtils.loadAnimation(c, animresId);
    }


    @Override
    public void makeWidthForBothOrientation(View targetView, int portraitWidth, int landscapeWidth, Resources r) {
        ViewGroup.LayoutParams lp = targetView.getLayoutParams();
        int orientation = r.getConfiguration().orientation;
        if(orientation == ORIENTATION_PORTRAIT){
            lp.width = portraitWidth;
        } else if(orientation == ORIENTATION_LANDSCAPE) {
            lp.width = landscapeWidth;
        }
        targetView.setLayoutParams(lp);
    }

    @Override
    public void makeHeightForBothOrientation(View targetView, int portraitHeight, int landscapeHeight, Resources r) {
        ViewGroup.LayoutParams lp = targetView.getLayoutParams();
        int orientation = r.getConfiguration().orientation;
        if(orientation == ORIENTATION_PORTRAIT){
            lp.height = portraitHeight;
        } else if(orientation == ORIENTATION_LANDSCAPE) {
            lp.height = landscapeHeight;
        }
        targetView.setLayoutParams(lp);
    }

    @Override
    public void makeDialogContentHeightFromOrientation(View targetView,
                                                       RelativeLayout.LayoutParams lp,
                                                       int portraitModeHeight, int bottomAnchor,
                                                       Resources r) {
        int orientation = r.getConfiguration().orientation;
        if(orientation == ORIENTATION_PORTRAIT){
            lp.height = portraitModeHeight;
            lp.addRule(RelativeLayout.ABOVE, 0);    //remove rule
        } else if(orientation == ORIENTATION_LANDSCAPE) {
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.addRule(RelativeLayout.ABOVE, bottomAnchor);
        }
        targetView.setLayoutParams(lp);
    }

    @Override
    public Drawable getDrawable(int resourceId, Context c) {
        Resources resources = c.getResources();
        return ResourcesCompat.getDrawable(resources, resourceId, null);
    }

    @Override
    public void setDrawable(View view, Drawable target) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(target);
        } else {
            view.setBackground(target);
        }
    }

    @Override
    public int getScreenWidth(Activity a) {
        Display display = a.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @Override
    public int getScreenHeight(Activity a) {
        Display display = a.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
}
