package pubsub.invariants;

/**
 *
 * @author tsilo
 * @author John Gasparis
 * @author xvas
 */
public class WellKnownIds {

    public static class Fwd {

        public static final String FWD_SID = "10";
        public static final String FWD_KEEP_ALIVE = "10";
        public static final String FWD_LINK_ESTABLISH = "11";
        public static final String FWD_LINK_DOWN = "12";
        public static final String FWD_RID = "13";
        public static final String FWD_INFO = "14"; //used for IPC with FWD
        public static final String FWD_MANAGEMENT = "15"; //used for management queries        
    }

    public static class TMC {

        public static final String TMC_SID = "14";
        public static final String TMC_RID = "15";
        public static final String TMC_LOCAL_UTIL_RID = "999";
        public static final String TMC_DEBUG_RID = "998";
    }

    public static class RVA {

        /**
         * Scope Identifier owned by RVA.
         */
        public static final String RVA_SID = "16";
        /**
         * Rid used to publish data to be received by the RVA component.
         */
        public static final String RVA_RID = "21";
        
        public static final String RVA_COM_RID = "23";
        public static final String RVA_PUSH_PROXY_RID = "CC";
    }

    public static class RVP {

        public static final String RVP_SID = "25";
        public static final String RVP_RID = "26";
        /**
         * For an RVP to announce its existence to a prospective RVP proxy router
         */
        public static final String RVP_PRESENCE_RID = "27";
    }
}
