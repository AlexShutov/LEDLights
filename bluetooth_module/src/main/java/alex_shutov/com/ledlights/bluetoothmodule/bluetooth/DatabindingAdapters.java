package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

/**
 * Created by Alex on 11/21/2016.
 */

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Contains general method for binding attributes
 */
public class DatabindingAdapters {

    /**
     * Methods for setting image resource by Databinding
     */
    @BindingAdapter("android:srcCompat")
    public static void setImageUri(ImageView view, String imageUri) {
        if (imageUri == null) {
            view.setImageURI(null);
        } else {
            view.setImageURI(Uri.parse(imageUri));
        }
    }

    @BindingAdapter("android:srcCompat")
    public static void setImageUri(AppCompatImageView view, int resourceId) {
        view.setBackgroundResource(resourceId);
    }

    @BindingAdapter("android:srcCompat")
    public static void setImageUri(ImageView view, Uri imageUri) {
        view.setImageURI(imageUri);
    }

    @BindingAdapter("android:srcCompat")
    public static void setImageDrawable(ImageView view, Drawable drawable) {
        view.setImageDrawable(drawable);
    }

    @BindingAdapter("android:srcCompat")
    public static void setImageResource(ImageView imageView, int resource){
        imageView.setImageResource(resource);
    }

    @BindingAdapter("android:textNumber")
    public static void setTextFromNumber(AppCompatTextView textView, int number) {
        String value = String.valueOf(number);
        textView.setText(value);
    }


    /**
     * Define 'margin left' attribute for data binding
     */
    @BindingAdapter("android:layout_marginLeft")
    public static void setLeftMargin(View view, float leftMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins((int) leftMargin, layoutParams.topMargin,
                layoutParams.rightMargin, layoutParams.bottomMargin);
        view.setLayoutParams(layoutParams);
    }



}
