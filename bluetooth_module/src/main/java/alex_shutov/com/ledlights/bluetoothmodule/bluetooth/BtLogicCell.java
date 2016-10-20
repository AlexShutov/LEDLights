package alex_shutov.com.ledlights.bluetoothmodule.bluetooth;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.CommFeedbackInterface;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.CommInterface;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtCommPort.hex.BtCommPortListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.LogConnectorListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnListenerEsbReceiveMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.esb.BtConnListenerEsbSendMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.LogScannerListener;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb.BtScanListenerEsbReceiveMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.esb.BtScanListenerEsbSendMapper;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStorageAdapter;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtStoragePort.hex.BtStoragePort;
import alex_shutov.com.ledlights.hex_general.LogicCell;
import alex_shutov.com.ledlights.hex_general.PortInfo;
import alex_shutov.com.ledlights.hex_general.PortListener;

/**
 * Created by lodoss on 24/08/16.
 */

/**
 * Logic cell, connecting all ports from Bluetooth module.
 * For two- way communication with ports it implement listener interfaces from
 * every port :BtScanPortListener,  BtConnPortListener,
 */
public class BtLogicCell extends LogicCell {
    private static final String LOG_TAG = LogicCell.class.getSimpleName();
    /** references to ports connectoed to this LogicCell
     * Those intances should be created by DI - we can configure port types by
     * changing modules during creation of DI component (.createPortAdapterCreator()) method.
     * Adapters are set by CellDeployer, but other
     */
    private BtScanAdapter btScanAdapter;
    private BtConnAdapter btConnAdapter;
    private BtStorageAdapter btStorageAdapter;
    private BtCommAdapter btCommAdapter;
    /**
     * In case of internal ports, connected to this logic cell, usually ESB mappers serves as
     * feedback ports. But, communication port is an external port, so we have to
     * save external feedback. Moreover, for avoiding null checking it is better to use
     * some 'dummy feedback', receiving all feedback messages while external listener not
     * specified.
     */
    private BtCommPortListener btCommPortListener;

    @Inject
    public Context context;

    // loggers
    @Inject
    public LogScannerListener logScannerListener;
    @Inject
    public LogConnectorListener logConnectorListener;
    /**
     * Setting external port listener is up to logic cell, so this backup listener is created here.
     */
    @Inject
    @Named("dummy_comm_listener")
    BtCommPortListener dummyCommPortLogger;

    // ESB mappers
    // maps bt connection listener's method to event bus events (send mapper ) and
    // listens for event bus events and notifies registered receiver
    @Inject
    BtConnListenerEsbSendMapper connListenerSendMapper;
    @Inject
    BtConnListenerEsbReceiveMapper connListenerEsbReceiveMapper;
    // BtScanner event mappers (maps listener)
    @Inject
    BtScanListenerEsbSendMapper scanListenerSendMapper;
    @Inject
    BtScanListenerEsbReceiveMapper scanListenerReceiveMapper;

    /**
     *  Initialize all internal dependencies here
     *  do nothing for now, all BT pairing, data transfer algorithims
     *  has to be initialized here.
     */
    @Override
    public void init() {
        Log.i(LOG_TAG, "BtLogicCell.init()");
        btConnAdapter.initialize();
        btScanAdapter.initialize();
        btStorageAdapter.initialize();
        // connect external port first, then call 'initialize', because it is a
        // decorator.
        hookUpExternalCommPort();
        btCommAdapter.initialize();
        initializeEsbMappers();
    }

    @Override
    public void suspend() {
        suspendEsbMappers();
    }

    @Override
    protected void injectThisCell() {
        BtPortAdapterCreator creator = (BtPortAdapterCreator) getAdaperCreator();
        creator.injectBtLogicCell(this);
    }

    /**
     *  Accessors
     */
    public BtConnPort getBtConnPort() {
        return btConnAdapter;
    }
    public BtScanPort getBtScanPort() {
        return btScanAdapter;
    }
    public BtStoragePort getBtStoragePort(){
        return btStorageAdapter;
    }
    public BtCommPort getBtCommPort(){
        return btCommAdapter;
    }

    public void setBtScanAdapter(BtScanAdapter btScanAdapter) {
        this.btScanAdapter = btScanAdapter;
    }

    public void setBtConnAdapter(BtConnAdapter btConnAdapter) {
        this.btConnAdapter = btConnAdapter;
    }

    public void setBtStorageAdapter(BtStorageAdapter btStorageAdapter) {
        this.btStorageAdapter = btStorageAdapter;
    }

    public void setBtCommAdapter(BtCommAdapter btCommAdapter) {
        this.btCommAdapter = btCommAdapter;
    }

    public BtCommPortListener getBtCommPortListener() {
        return btCommPortListener;
    }

    public void setBtCommPortListener(BtCommPortListener btCommPortListener) {
        if (null != btCommPortListener) {
            this.btCommPortListener = btCommPortListener;
        } else {
            // use dummy value instead of null
            this.btCommPortListener = dummyCommPortLogger;
        }
    }

    public Context getContext() {
        return context;
    }

    /**
     * Subscribe mappers to EventBus and register those mappers with adapters
     */
    private void initializeEsbMappers(){
        // initialize bluetoooth connector mappers
        connListenerSendMapper.register();
        connListenerEsbReceiveMapper.register();
        // register logger as wrapped callback in receiving mapper
        connListenerEsbReceiveMapper.setListener(logConnectorListener);
        btConnAdapter.setPortListener(connListenerSendMapper);

        // initialize bluetooth scanner mapper
        scanListenerSendMapper.register();
        scanListenerReceiveMapper.register();
        btScanAdapter.setPortListener(scanListenerSendMapper);
        scanListenerReceiveMapper.setListener(logScannerListener);

    }

    /**
     * Unregister mappers from adapters and unsubscribe mappers from EventBus
     */
    private void suspendEsbMappers(){
        connListenerEsbReceiveMapper.unregister();
        connListenerSendMapper.unregister();
        btConnAdapter.setPortListener(null);
        scanListenerSendMapper.unregister();
        scanListenerReceiveMapper.unregister();
    }

    private void hookUpExternalCommPort(){
        // use dummy logger by default, app will change it soon after Bluetooth cell
        // initialization.
        setBtCommPortListener(dummyCommPortLogger);
        btCommAdapter.setDecoree(new CommInterface() {
            @Override
            public void startConnection() {

            }

            @Override
            public void sendData(byte[] data) {

            }
        });

    }


}
