package pubsub.node.start;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import pubsub.configuration.Configuration;
import pubsub.node.PubSubNode;

public class PubSubNodeStarter {

    private static final Logger logger = Logger.getLogger(PubSubNodeStarter.class);

    public static void main(String[] args) throws Exception {
    	
    	readOptions(args);
    	Configuration.readConfiguration();
    	Configuration.install();        

        try {
            logger.debug("starting node");
            PubSubNode node = new PubSubNode();
            node.startAll();

        } catch (IOException e) {
            logger.debug(e);
        }
    }

	private static void readOptions(String[] args) {
		if(args.length == 0){
			return;
		}
		
		for(int i=0; i<args.length; i++){
			String opCode = args[i];
			if("-f".equals(opCode)){
				String file = args[++i];
				File f = new File(file);
				System.out.printf("conf file location: %s\n", f.getAbsolutePath());
				Configuration.setConfFile(f.getAbsolutePath());
			}else{
				System.out.println("unknown option "+opCode);
			}
		}
		
	}
}
