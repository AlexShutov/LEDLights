package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.device_list_fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDevicePresenter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.mvp.AnotherDeviceView;
import alex_shutov.com.ledlights.bluetoothmodule.databinding.DeviceListBinding;

/**
 * Created by lodoss on 02/12/16.
 */

public abstract class DevicesFragment extends Fragment implements AnotherDeviceView{

    // activity provides presenter to fragments in ViewPager by this interface
    public interface PresenterProvider {
        AnotherDevicePresenter providePresenter();
    }

    private AnotherDevicePresenter presenter;

    /**
     * All device lists has the same layout, only logic differ
     */
    private DeviceListBinding viewBinding;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        PresenterProvider presenterProvider = (PresenterProvider) getActivity();
        presenter = presenterProvider.providePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.device_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.bind(view);
    }


    /**
     * Disconnect from Presenter
     */
    @Override
    public void onPause() {
        presenter.detachView();
        super.onPause();
    }

    /**
     * Attach this fragment as a View to Presenter
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.attachView(this);
    }


}
