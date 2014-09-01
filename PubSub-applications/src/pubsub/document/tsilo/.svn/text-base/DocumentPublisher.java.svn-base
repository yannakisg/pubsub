package pubsub.document.tsilo;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import pubsub.ContentType;
import pubsub.PubSubID;
import pubsub.Subscription;
import pubsub.transport.tsilo.source.DocumentSource;
import pubsub.util.Util;

public class DocumentPublisher {

    private static String filepath = null;
    private static int piecesize = -1;

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        readOptions(args);
        validateOptions();

        File f = new File(filepath);
        PubSubID rid = Util.sha256toPubSubId(f.getName().getBytes());
        PubSubID scopeId = Util.sha256toPubSubId(rid.getId());
        Subscription sub = Subscription.createSubscription(scopeId, rid, ContentType.DOCUMENT);
        DocumentSource docSource = new DocumentSource(sub, f, piecesize);

        PubSubID feedBackRid = Util.sha256toPubSubId(rid.getId());
        docSource.serve(scopeId, feedBackRid);
    }

    private static void readOptions(String[] args) {
        filepath = "/home/gaspar/Desktop/1.mp3";
        DocumentSource.configureLocalhostAddr("localhost");
        DocumentSource.configureLocalhostPort(10000);
        piecesize = 4000;
        /*
        if (args.length == 0) {
            printUsage();
            exit(1);
        }

        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if ("-f".equals(option)) {
                filepath = args[++i];
            } else if ("-p".equals(option)) {
                try {
                    piecesize = Integer.parseInt(args[++i]);
                    if (piecesize < 0) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    System.out.println("invalid piece_size option " + args[i]);
                    exit(1);
                }
            } else if ("-l".equals(option)) {
                String loopbackAddr = args[++i];
                DocumentSource.configureLocalhostAddr(loopbackAddr);
            } else if ("-p".equals(option)) {
                String loopbackPortStr = args[++i];
                try {
                    int port = Integer.parseInt(loopbackPortStr);
                    DocumentSource.configureLocalhostPort(port);
                } catch (Exception e) {
                    System.out.println("invalid port: " + loopbackPortStr);
                }
            }
        }*/
    }

    private static void validateOptions() {
        if (filepath == null) {
            System.out.println("file not specified");
            printUsage();
            exit(1);
        }

        File f = new File(filepath);
        if (!f.exists()) {
            System.out.printf("File % does not exist.\n", f.getAbsolutePath());
            exit(1);
        }

        if (piecesize < 0) {
            System.out.println("piece_size not specified");
            printUsage();
            exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("required options are:");
        System.out.println("-f file");
        System.out.println("-c piece_size (in bytes)");
        System.out.println("-l loopback_address (for locRC");
        System.out.println("-p port (for locRC");
    }

    private static void exit(int i) {
        System.out.println("Exiting with status " + i);
        System.exit(i);
    }
}
