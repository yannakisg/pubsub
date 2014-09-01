package pubsub.messages;

/**
 *
 * @author John Gasparis
 */
public class MessageType {

    public enum Type {

        NOTHING((byte) -1),
        LINK_EST_ANNOUNCEMENT((byte) 0),
        LINK_DOWN((byte) 1),
        HELLO((byte) 2),
        LSA((byte) 3),
        LSA_UPDATE((byte) 4),
        GET_NEIGHBORS((byte) 5),
        GET_MYNODE_ID((byte) 6),
        GET_FID((byte) 7),
        GET_DEFAULT_GW((byte) 8),
        DEBUG_TOPOLOGY((byte) 9),
        DEBUG_DIJKSTRA((byte) 10),
        GET_HOSTS((byte) 11),
        GET_FID_A_B((byte) 12),
        TOPOLOGY_MESSAGE((byte) 13),
        GET_PROXY_ROUTER((byte) 14),
        NET_LSA_PROXY_ROUTER((byte) 15),
        GET_RVP((byte) 16),
        GET_FID_HOST((byte) 17),
        GET_LID((byte) 18),
        TMC_ACK((byte) 19),
        RVA_PROXY_INFO_MESSAGE((byte) 20),
        RVA_REQUEST_PROXY_INFO_MESSAGE((byte) 21),
        RVA_ERROR_MESSAGE((byte) 22),
        RVA_FORWARD_NET_MESSAGE((byte) 23),
        RVA_FORWARD_IPC_MESSAGE((byte) 24),
        IPC_LSA_PROXY_ROUTER((byte) 25),
        RVP_ACK_MESSAGE((byte) 26),
        INSTRUCT_DOCUMENT_MESSAGE((byte) 27),
        CONTROL_MESSAGE((byte) 28),
        DATA_MESSAGE((byte) 29),
        REQUEST_MESSAGE((byte) 30),
        CHANNEL_MESSAGE((byte) 31),
        RVA_ACK_MESSAGE((byte) 32),
        TOPOLOGY_INTEREST_CHUNK((byte) 33),
        INSTRUCT_RVA_ACK_MESSAGE((byte) 34),
        INSTRUCT_CHANNEL_MESSAGE((byte) 35),
        CTRL_ACK_MESSAGE((byte) 36),
        INSTRUCT_UNSUBSCRIBE_MESSAGE((byte) 37),
        REQUEST_STEINER_TREE((byte) 38);
        private byte index;

        private Type(byte index) {
            this.index = index;
        }

        public byte getType() {
            return this.index;
        }

        public static Type findBy(byte i) {
            Type type = null;

            for (Type t : values()) {
                if (t.getType() == i) {
                    type = t;
                    break;
                }
            }

            return type;
        }

        public static byte findBy(Type type) {
            byte b = 0;

            for (Type t : values()) {
                if (t == type) {
                    return b;
                }
                b++;
            }

            return (-2);
        }
    }
}
