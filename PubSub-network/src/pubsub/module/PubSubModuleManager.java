package pubsub.module;

import pubsub.PubSubID;

/**
 *
 * @author tsilo
 */
public class PubSubModuleManager {

    private static PubSubModule instance;
    public static int SID_LEN = PubSubID.ID_LENGTH;
    public static int RID_LEN = PubSubID.ID_LENGTH;

    public static void setModule(PubSubModule m) {
        instance = m;
    }

    public static PubSubModule getModule() {
        if (instance == null) {
            throw new IllegalStateException("instance not set yet");
        }
        return instance;
    }
}
