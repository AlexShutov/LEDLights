package alex_shutov.com.ledlights;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCellDeployer;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.LogListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtLogicCell;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.LogScannerListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.hex_general.CellDeployer;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lodoss on 30/06/16.
 */
public class LEDApplication extends MultiDexApplication{
    private static final String LOG_TAG = LEDApplication.class.getSimpleName();

    /** Connector port */
    BtConnAdapter btAdapter;
    BtConnPort btPort;
    private LogListener btPortListener;

    /** Scanner port */
    BtScanAdapter scanAdapter;
    LogScannerListener scannerListener;
    BtScanPort scanPort;

    CellDeployer btCellDeployer;
    LogicCell cell;

    void initCell(){
        // create cell deployer
        btCellDeployer = new BtCellDeployer(this);
        // create new logic cell
        cell = new BtLogicCell();
        // deploy this cell- create and createObjects ports, connect ports to the cell
        btCellDeployer.deploy(cell);

        Context context = ((BtLogicCell) cell).getContext();
        String msg = context == null ? "Context is null" : "Context is not null, DI work";
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        // createObjects ConnectorPort
        btPortListener = new LogListener(this);
        btAdapter = new BtConnAdapter(this);
        btAdapter.setPortListener(btPortListener);
        btAdapter.initialize();
        /** adapter is a port */
        btPort = btAdapter;

        /** createObjects ScannerPort */
        scannerListener = new LogScannerListener(this);
        scanAdapter = new BtScanAdapter(this);
        scanAdapter.setPortListener(scannerListener);
        scanAdapter.initialize();
        scanPort = scanAdapter;

        initCell();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }



    private UUID uuidFromResource(int resId){
        String id = getResources().getString(resId);
        return UUID.fromString(id);
    }

    public BtConnPort getBtPort() {
        return btPort;
    }

    public BtScanPort getBtScanPort(){
        return scanPort;
    }
}
