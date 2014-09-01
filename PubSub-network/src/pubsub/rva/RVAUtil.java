package pubsub.rva;

import pubsub.PubSubID;
import pubsub.invariants.WellKnownIds;

/**
 *
 * @author John Gasparis
 */
public class RVAUtil {

    /**
     * Scope Identifier owned by RVA.
     */
    public static final PubSubID RVA_SID = PubSubID.fromHexString(
            WellKnownIds.RVA.RVA_SID);
    /**
     * Rid used to publish data to be received by the RVA component.
     */
    public static final PubSubID RVA_RID = PubSubID.fromHexString(
            WellKnownIds.RVA.RVA_RID);
    /**
     * Rid used for communication between RVA's.
     */
    public static final PubSubID RVA_COM_RID = PubSubID.fromHexString(
            WellKnownIds.RVA.RVA_COM_RID);
    /**
     * Rid used for pushing the RVP Proxy location
     */
    public static final PubSubID RVA_PROXY_PUSH_RID = PubSubID.fromHexString(
            WellKnownIds.RVA.RVA_PUSH_PROXY_RID);
}
