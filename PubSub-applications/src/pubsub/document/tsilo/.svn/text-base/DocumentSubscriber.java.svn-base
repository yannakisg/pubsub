package pubsub.document.tsilo;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import pubsub.PubSubID;
import pubsub.transport.tsilo.receiver.DocumentReceiver;
import pubsub.transport.tsilo.receiver.DocumentReceiverExcpetion;
import pubsub.util.Util;

public class DocumentSubscriber {

    private static String filepath = null;

    public static void main(String[] args) throws NoSuchAlgorithmException,
            IOException {
        readOptions(args);
        validateOptions();

        File f = new File(filepath);

        PubSubID rid = Util.sha256toPubSubId(f.getName().getBytes());
        PubSubID scopeId = Util.sha256toPubSubId(rid.getId());

        DocumentReceiver receiver = new DocumentReceiver(scopeId, rid, f);
        try {
            receiver.connect();
            receiver.transfer();
        } catch (DocumentReceiverExcpetion e) {
            System.out.println("Could not fetch");
            e.printStackTrace();
        }
    }

    private static void readOptions(String[] args) {
        if (args.length == 0) {
            printUsage();
            exit(1);
        }

        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if ("-f".equals(option)) {
                filepath = args[++i];
            } else if ("-l".equals(option)) {
                String loopbackAddr = args[++i];
                DocumentReceiver.configureLocalhostAddr(loopbackAddr);
            } else if ("-p".equals(option)) {
                String loopbackPortStr = args[++i];
                try {
                    int port = Integer.parseInt(loopbackPortStr);
                    DocumentReceiver.configureLocalhostPort(port);
                } catch (Exception e) {
                    System.out.println("invalid port: " + loopbackPortStr);
                }
            }
        }
    }

    private static void validateOptions() {
        if (filepath == null) {
            System.out.println("filename not specified");
            printUsage();
            exit(1);
        }

        File f = new File(filepath);
        if (f.exists()) {
            System.out.println("File already exists. Cannot overwrite");
            exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("required options are:");
        System.out.println("-f filename");
        System.out.println("-l loopback_address (for locRC");
        System.out.println("-p port (for locRC");
    }

    private static void exit(int i) {
        System.out.println("Exiting with status " + i);
        System.exit(i);
    }
}
