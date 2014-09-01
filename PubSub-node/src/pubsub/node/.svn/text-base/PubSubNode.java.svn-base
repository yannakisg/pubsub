package pubsub.node;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import pubsub.configuration.Configuration;
import pubsub.forwarding.FwdComponent;
import pubsub.localrendezvous.LocRCClientFactory;
import pubsub.localrendezvous.LocRCFactory;
import pubsub.localrendezvous.LocalRComponent;
import pubsub.localrendezvous.singlejvm.LocalRCManager;
import pubsub.module.PubSubModule;
import pubsub.module.PubSubModuleManager;
import pubsub.module.ScopeRendezvousIdTable;
import pubsub.module.SimplePubSubModuleImpl;
import pubsub.rva.RVAComponentBase;
import pubsub.rva.RVAComponentFactory;
import pubsub.tmc.Atomic;
import pubsub.tmc.TMCComponent;
import pubsub.tmc.TMComponentFactory;

public class PubSubNode {

    private static final Logger logger = Logger.getLogger(PubSubNode.class);
    private LocalRComponent locRC;
    private FwdComponent fwdC;
    private TMCComponent tmc;
    private RVAComponentBase rva;            

    public PubSubNode() throws IOException, Exception {
        PubSubModule module = new SimplePubSubModuleImpl(new ScopeRendezvousIdTable());
        PubSubModuleManager.setModule(module);
        this.locRC = LocRCFactory.createLocalRendezvousComponent();
        LocRCClientFactory.configureManager(new LocalRCManager());                        

        this.fwdC = new FwdComponent();
        this.tmc = TMComponentFactory.createNewTMC(Configuration.getTMCMode());
        this.rva = RVAComponentFactory.rvaComponent(Configuration.getRVAMode());
    }

	public void startAll() {
        logger.debug("starting LocRC");
        locRC.startAll();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }

        logger.debug("starting FwdC");
        fwdC.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }

        tmc.start();

        while (Atomic.getValue() != 1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }

        logger.debug("Starting RVA");
        rva.start();
    }

    public List<String> listAllLinks() {
        return fwdC.listAllLinks();
    }
}
