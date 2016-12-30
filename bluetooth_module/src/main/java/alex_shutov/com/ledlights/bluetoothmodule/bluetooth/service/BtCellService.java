package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCellDeployer;
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
    public class BtCellBinder extends Binder {
        public BtCellService getService() {
            return BtCellService.this;
        }
    }
    private static final String LOG_TAG = BtCellService.class.getSimpleName();

    // reference to cell object itself
    private BtLogicCell cell;
    // DI component, responsible for creating all objects and deploying (initializing) that cell.
    private BtCellDeployer cellDeployer;
    // Binder, given to connecting entity

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createAndInitCell();
        return new BtCellBinder();
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

    public BtLogicCell getCell() {
        return cell;
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
    }



}
