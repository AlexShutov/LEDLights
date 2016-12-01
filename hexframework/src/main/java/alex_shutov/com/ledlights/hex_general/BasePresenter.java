package alex_shutov.com.ledlights.hex_general;

/**
 * Created by lodoss on 19/10/16.
 */


import org.greenrobot.eventbus.EventBus;

/**
 * Base Presenter class. Notice, it is a class, not interface. There is a practice - mind
 * presenter as interface, implemented by some fragment. Here Presenter is a separate class,
 * Activity and Fragment is just system containers, holding concrete Presenter implementation.
 * @param <V> type of concrete view
 */
public abstract class BasePresenter<M extends BaseModel, V extends BaseView> {
    private static final String LOG_TAG = BasePresenter.class.getSimpleName();
    private M model;
    private V view;

    private EventBus eventBus;

    protected abstract void onViewAttached();
    protected abstract void onViewDetached();

    protected abstract void onModelAttached();
    protected abstract void onModelDetached();

    public BasePresenter(EventBus bus){
        this.eventBus = bus;
    }

    public void attachModel(M model) {
        this.model = model;
        onModelAttached();
    }

    public void detachModel() {
        this.model = null;
        onModelDetached();
    }

    public void attachView(V view) {
        this.view = view;
        onViewAttached();
    }

    public void detachView() {
        this.view = null;
        onViewDetached();
    }

    // accessors

    public V getView() {
        return view;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
