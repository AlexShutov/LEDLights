package alex_shutov.com.ledlights;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import java.util.UUID;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCellDeployer;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtLogicCell;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStoragePort;
import alex_shutov.com.ledlights.hex_general.CellDeployer;

/**
 * Created by lodoss on 30/06/16.
 */
public class LEDApplication extends MultiDexApplication{
    private static final String LOG_TAG = LEDApplication.class.getSimpleName();


    BtConnPort connPort;
    BtScanPort scanPort;
    BtStoragePort dbPort;

    CellDeployer btCellDeployer;
    BtLogicCell cell;

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

        initCell();

        scanPort = cell.getBtScanPort();
        connPort = cell.getBtConnPort();
        dbPort = cell.getBtStoragePort();
    }

    @Override
    public void onTerminate() {
        cell.suspend();
        super.onTerminate();
    }



    private UUID uuidFromResource(int resId){
        String id = getResources().getString(resId);
        return UUID.fromString(id);
    }

    public BtConnPort getBtConnPort() {
        return connPort;
    }

    public BtScanPort getBtScanPort(){
        return scanPort;
    }

    public BtStoragePort getDbPort() {
        return dbPort;
    }
}
