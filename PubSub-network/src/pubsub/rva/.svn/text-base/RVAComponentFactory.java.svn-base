package pubsub.rva;

import org.apache.log4j.Logger;

/**
 * @author xvas
 * @author John Gasparis
 */
public class RVAComponentFactory {

    private static final Logger logger = Logger.getLogger(RVAComponentFactory.class);

    /**
     *
     * @param type the enum type of the right Rendezvous Agent component to be returned
     * @return the appropriate RVA component incarnation according to the type parameter or null
     *          if something goes wrong with constructing the RVA component requested
     * @throws UnknownRVAException in case an unknown type is requested
     * 
     * @see pubsub.rva.RVAComponentFactory.RVAType
     */
    public static RVAComponentBase rvaComponent(RVA_Mode type) throws UnknownRVAException {
        RVAComponentBase toReturn;

        if (type == RVA_Mode.HOST) {
            toReturn = new RVAHostComponent();
        } else if (type == RVA_Mode.ROUTER) {
            toReturn = new RVARouterComponent();
        } else if (type == RVA_Mode.RVP) {
            toReturn = new RVARVPComponent();
        } else {
            throw new UnknownRVAException("Unkonwn RVAType: the requested type of RVA "
                    + "component is unknown");
        }

        logger.info("RVAComponentFactory:" + toReturn.getClass() + " returned from factory");
        return toReturn;
    }
}
