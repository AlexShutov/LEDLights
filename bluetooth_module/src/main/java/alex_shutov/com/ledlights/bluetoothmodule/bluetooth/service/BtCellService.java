package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCellDeployer;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtLogicCell;

/**
 * Created by lodoss on 20/12/16.
 */

/**
 * This Service contain logic cell for Bluetooth communication.
 * It should run all the time.
 * App start this service and then bind to it for getting access to BtLogicCell instance for
 * setting callback, receiving all Bluetooth events and for accessing control methods of
 * that cell. BtLogicCell is controlled by BtCommInterface interface, provided by cell iteself.
 * That interface is returned inside Binder implementation.
 */
public class BtCellService extends Service {
    /**
     * Binder, providing reference to this Service instance
     */
    /**
     * First version of this binder returned access to entire Service. I did that for ease of
     * cheching if it work with device. But, this breaks incapsulation - outer world should
     * know nothing of what this service and logic cell consist of.
     */
    public class BtCellBinder extends Binder {

        public BtCommPort getBluetoothCommunicationPort() {
            BtCommPort commPort = cell.getBtCommPort();
            return commPort;
        }

        public void setCommPortListener(BtCommPortListener listener) {
            // Only this Service know that port is BtCommAdapter
            BtCommAdapter adapter = (BtCommAdapter) cell.getBtCommPort();
            adapter.setPortListener(listener);
        }
    }
    private static final String LOG_TAG = BtCellService.class.getSimpleName();

    // reference to cell object itself
    private BtLogicCell cell;
    // DI component, responsible for creating all objects and deploying (initializing) that cell.
    private BtCellDeployer cellDeployer;
    // Binder, given to connecting entity
    BtCellBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createAndInitCell();
        return binder;
    }

    /**
     * Always start this service
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createAndInitCell();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        cell.suspend();
        super.onDestroy();
    }

    /**
     * Cell is created when this Service is started. (or bound, depending on which called first).
     * It is unclear, which method will be called first - .startService() or .bind().
     * That is up to user. So, to solve that, check if cell is null and proceed if it is.
     */
    private void createAndInitCell() {
        if (null != cell) {
            // service is already initialized, aborting
            return;
        }
        cellDeployer = new BtCellDeployer(this);
        cell = new BtLogicCell();
        cellDeployer.deploy(cell);
        binder = new BtCellBinder();
    }
}
