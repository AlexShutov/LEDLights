package alex_shutov.com.ledlights.hex_general.esb;

/**
 * Created by lodoss on 05/10/16.
 */
public class TestEventStore extends EsbEventStore {

    public static class ArgumentStringEvent {
        public String string;
    }

    public static class ArgumentIntegerEvent {
        public Integer integer;
    }

    public static class ArgumentStringAndIntegerEvent {
        String string;
        Integer integer;
    }

}
