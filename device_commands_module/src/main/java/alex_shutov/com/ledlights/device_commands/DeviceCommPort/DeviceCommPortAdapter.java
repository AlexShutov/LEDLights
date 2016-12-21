package alex_shutov.com.ledlights.device_commands.DeviceCommPort;

import alex_shutov.com.ledlights.hex_general.Adapter;
import alex_shutov.com.ledlights.hex_general.PortInfo;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by lodoss on 21/12/16.
 */

/**
 * LogicCell does actual job, this Adapter only receives response and in informs cell of it
 */
public class DeviceCommPortAdapter extends Adapter implements DeviceCommPort {

    private PublishSubject<byte[]> responsePipe;
    private PublishSubject<Boolean> sendPipe;

    @Override
    public PortInfo getPortInfo() {
        PortInfo portInfo = new PortInfo();
        portInfo.setPortCode(PortInfo.PORT_DEVICE_COMMANDS_COMM);
        portInfo.setPortDescription("Port for sending command to device (as byte array)");
        return portInfo;
    }

    @Override
    public void initialize() {
        responsePipe = PublishSubject.create();
        sendPipe = PublishSubject.create();
    }

    /**
     * Inherited from DeviceCommPort
     */

    @Override
    public void onDataSent() {
        sendPipe.onNext(true);
    }

    @Override
    public void onResponse(byte[] response) {
        responsePipe.onNext(response);
    }

    public Observable<byte[]> getResponsePipe() {
        return responsePipe.asObservable();
    }

    public Observable<Boolean> getSendPipe() {
        return sendPipe.asObservable();
    }

}

