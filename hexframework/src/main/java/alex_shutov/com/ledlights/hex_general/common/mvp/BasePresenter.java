package alex_shutov.com.ledlights.hex_general.common.mvp;

/**
 * Created by lodoss on 19/10/16.
 */

/**
 *
 * Base Presenter class. Notice, it is a class, not interface. There is a practice - mind
 * presenter as interface, implemented by some fragment. Here Presenter is a separate class,
 * Activity and Fragment is just system containers, holding concrete Presenter implementation.
 * @param <M> type of concrete model
 * @param <V> type of concrete view
 */
public abstract class BasePresenter<M extends BaseModel, V extends BaseView> {
    private M model;
    private V view;

    protected abstract void onModelAttached();
    protected abstract void onModelDetached();
    protected abstract void onViewAttached();
    protected abstract void onViewDetached();

    /**
     * Attach model to this presenter. After that concrete presenter will be both notified.
     * @param model
     */
    public void attachModel(M model){
        this.model = model;
        onModelAttached();
    }

    /**
     * Detach current model from this presenter
     */
    public void detachModel(){
        this.model = null;
        onModelDetached();
    }


    public void attachView(V view){
        this.view = view;
        onViewAttached();
    }

    public void detachView(){
        this.view = null;
        onViewDetached();
    }

    // accessors

    public M getModel() {
        return model;
    }

    public V getView() {
        return view;
    }
}
