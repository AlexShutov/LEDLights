package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.select_another_device_strategy.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import alex_shutov.com.ledlights.bluetoothmodule.BR;

/**
 * Created by lodoss on 06/12/16.
 */

public class DevicePickerViewModel extends BaseObservable {

    private int emptyText;
    private boolean isEmpty;

    @Bindable
    public int getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(int emptyText) {
        this.emptyText = emptyText;
        notifyPropertyChanged(BR.emptyText);
    }

    @Bindable
    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
        notifyPropertyChanged(BR.empty);
    }
}
