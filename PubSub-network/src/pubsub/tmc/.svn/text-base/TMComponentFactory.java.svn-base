package pubsub.tmc;

import pubsub.tmc.host.TMCHost;
import pubsub.tmc.router.TMCRouter;
import pubsub.tmc.rvp.TMCRVP;

/**
 *
 * @author John Gasparis
 */
public class TMComponentFactory {

    public static TMC_Mode TMC_MODE = TMC_Mode.ROUTER;

    public static void configureTMCMode(TMC_Mode mode) {
        TMC_MODE = mode;
    }

    public static TMCComponent createNewTMC(TMC_Mode mode) {
        if (mode == TMC_Mode.ROUTER) {
            return new TMCRouter();
        } else if (mode == TMC_Mode.HOST) {
            return new TMCHost();
        } else {
            return new TMCRVP();
        }
    }
}
