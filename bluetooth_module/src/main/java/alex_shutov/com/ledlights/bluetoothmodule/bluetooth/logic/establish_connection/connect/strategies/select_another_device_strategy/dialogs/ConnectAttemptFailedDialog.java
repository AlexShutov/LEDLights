package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.select_another_device_strategy.dialogs;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.connect.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.databinding.AttemptFailedDialogBinding;

/**
 * Created by lodoss on 12/12/16.
 */

public class ConnectAttemptFailedDialog extends DialogFragment {

    public interface ConnectionFailedDialogCallback {
        void acceptFailure();
        void retryToConnect();
    }

    private AttemptFailedDialogBinding viewBinding;
    private DeviceInfoViewModel model;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.attempt_failed_dialog, null);
        viewBinding = DataBindingUtil.bind(view);
        viewBinding.setModel(model);
        builder.setView(view);
        // set listeners to buttons
        ConnectionFailedDialogCallback callback = (ConnectionFailedDialogCallback) getActivity();
        viewBinding.afBtnAccept.setOnClickListener(v -> {
            callback.acceptFailure();
            dismiss();
        });
        viewBinding.afBtnTryAgain.setOnClickListener(v -> {
            callback.retryToConnect();
            dismiss();
        });
        return builder.create();
    }

    public void setModel(DeviceInfoViewModel model) {
        this.model = model;
    }
}
