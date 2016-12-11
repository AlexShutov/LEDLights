package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.databinding.DeviceDetailsBinding;

/**
 * Created by Alex on 12/11/2016.
 */

public class DeviceInfoDialog extends DialogFragment {
    private DeviceDetailsBinding viewBinding;
    // set before you show dialog
    private DeviceInfoViewModel viewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.device_details, null);
        viewBinding = DataBindingUtil.bind(view);
        builder.setView(view);
        // configure 'confirm' button
        viewBinding.diBtnOk.setOnClickListener(v -> {
            dismiss();
        });
        viewBinding.setModel(viewModel);
        return builder.create();
    }

    public void setViewModel(DeviceInfoViewModel viewModel) {
        this.viewModel = viewModel;
    }
}
