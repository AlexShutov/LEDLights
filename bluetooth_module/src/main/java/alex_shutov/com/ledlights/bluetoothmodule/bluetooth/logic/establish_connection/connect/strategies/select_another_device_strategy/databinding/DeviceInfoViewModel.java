package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.strategies.select_another_device_strategy.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import alex_shutov.com.ledlights.bluetoothmodule.BR;

/**
 * Created by lodoss on 02/12/16.
 */

/**
 * View model for displaying device info.
 * This model is used by two different layouts: item of device list (brief explanation) and
 * layout for info details
 */
public class DeviceInfoViewModel extends BaseObservable {

    private String deviceName;
    private String deviceAddress;
    private String deviceDescription;
    private boolean showDescription;

    private boolean isDeviceFromHistory;
    private boolean isPairedDevice;

    private View.OnClickListener showDeviceDetailsListener;
    private View.OnClickListener devicePickedListener;

    @Bindable
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        notifyPropertyChanged(BR.deviceName);
    }

    @Bindable
    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
        notifyPropertyChanged(BR.deviceAddress);
    }

    @Bindable
    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
        notifyPropertyChanged(BR.deviceDescription);
    }

    @Bindable
    public boolean isDeviceFromHistory() {
        return isDeviceFromHistory;
    }

    public void setDeviceFromHistory(boolean deviceFromHistory) {
        isDeviceFromHistory = deviceFromHistory;
        notifyPropertyChanged(BR.deviceFromHistory);
    }

    @Bindable
    public boolean isPairedDevice() {
        return isPairedDevice;
    }

    public void setPairedDevice(boolean pairedDevice) {
        isPairedDevice = pairedDevice;
        notifyPropertyChanged(BR.pairedDevice);
    }

    @Bindable
    public View.OnClickListener getShowDeviceDetailsListener() {
        return showDeviceDetailsListener;
    }

    public void setShowDeviceDetailsListener(View.OnClickListener showDeviceDetailsListener) {
        this.showDeviceDetailsListener = showDeviceDetailsListener;
        notifyPropertyChanged(BR.showDeviceDetailsListener);
    }

    @Bindable
    public boolean isShowDescription() {
        return showDescription;
    }

    public void setShowDescription(boolean showDescription) {
        this.showDescription = showDescription;
        notifyPropertyChanged(BR.showDescription);
    }

    @Bindable
    public View.OnClickListener getDevicePickedListener() {
        return devicePickedListener;
    }

    public void setDevicePickedListener(View.OnClickListener devicePickedListener) {
        this.devicePickedListener = devicePickedListener;
    }
}
