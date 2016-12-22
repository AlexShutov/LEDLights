package alex_shutov.com.ledlights.device_commands.main_logic.serialization_general;

/**
 * Created by lodoss on 22/12/16.
 */

/**
 * How many bytes different serialized models take. Those constants are moved to this
 * class, because there is no actual serialized models. Another solution- define constants in
 * serializers, but, if so, serializers will depend on each other.
 */
public class Constants {
    public static final int COLOR_SERIALIZED_SIZE = 3;
    public static final int TIME_INTERVAL_SERIALIZED_SIZE = 4;

}
