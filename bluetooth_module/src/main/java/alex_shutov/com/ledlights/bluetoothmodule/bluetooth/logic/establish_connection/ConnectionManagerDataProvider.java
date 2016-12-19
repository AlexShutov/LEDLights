package alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.establish_connection;

import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtConnectorPort.hex.BtConnPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtPortAdapterCreator;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.BtScannerPort.hex.BtScanPort;
import alex_shutov.com.ledlights.bluetoothmodule.bluetooth.logic.DataProvider;

/**
 * Created by Alex on 10/27/2016.
 */
public interface ConnectionManagerDataProvider extends DataProvider {

    /**
     * Manages connection to device, needed of coarse
     * @return
     */
    BtConnPort  provideBtConnPort();

    /**
     * In case there is no devices in database history, we have to show UI, listing
     * all currently available devices so user can select one. To do the scanning, we need
     * BtScanPort. According strategy will subscribe to 'device found' event and update UI
     * @return
     */
    BtScanPort provideBtScanPort();

    /**
     * Provide DI component, capable of creating any entity :) This interface was created
     * to not to provide this component in a first place, but, strategy need a presenter and we
     * can either provide it from facade (which isn't good), or, use DI directly.
     * @return
     */
    BtPortAdapterCreator provideDiComponent();
}
