package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import alex_shutov.com.ledlights.bluetoothmodule.R;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection.strategies.select_another_device_strategy.databinding.DeviceInfoViewModel;
import alex_shutov.com.ledlights.bluetoothmodule.databinding.DeviceInfoBinding;

/**
 * Created by lodoss on 06/12/16.
 */

public class ListOfDevicesAdapter extends RecyclerView.Adapter<ListOfDevicesAdapter.DeviceInfoViewHolder> {
    class DeviceInfoViewHolder extends RecyclerView.ViewHolder {
        private DeviceInfoBinding binding;
        public DeviceInfoViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public void bind(DeviceInfoViewModel viewModel) {
            binding.setModel(viewModel);
        }

    }

    private LayoutInflater inflater;
    private List<DeviceInfoViewModel> deviceList = new ArrayList<>();

    public ListOfDevicesAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public DeviceInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = DataBindingUtil.inflate(inflater, R.layout.device_info, parent, false)
                .getRoot();
        DeviceInfoViewHolder viewHolder = new DeviceInfoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceInfoViewHolder holder, int position) {
        DeviceInfoViewModel device = deviceList.get(position);
        holder.bind(device);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    /**
     * Add info of newly discovered device into the list
     * @param device
     */
    public void addDevice(DeviceInfoViewModel device) {
        deviceList.add(device);
        notifyDataSetChanged();
    }

    /**
     * Set entire list
     * @param devices
     */
    public void setDevices(List<DeviceInfoViewModel> devices) {
        deviceList = devices;
        notifyDataSetChanged();
    }

}
