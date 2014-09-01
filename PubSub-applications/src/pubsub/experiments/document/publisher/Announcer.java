package pubsub.experiments.document.publisher;

import pubsub.experiments.FileRead;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;
import pubsub.distribution.ParetoGenerator;
import pubsub.experiments.FileCreator;

/**
 *
 * @author John Gasparis
 */
public class Announcer {

    private static final Logger logger = Logger.getLogger(Announcer.class);
    private static String FILENAME = "";
    private static String PATHNAME = System.getProperty("user.home") + "/files";
    public static int CHUNK_SIZE = 4000;
    public static String LOOPBACK_ADDR = "localhost";
    public static int LOOPBACK_PORT = 10000;
    private static long LIFETIME = Long.MAX_VALUE;
    private static int TOTAL_CONTENTS = 33;
    private static double AVERAGE_SIZE = 80;
    private static boolean createFiles;

    public static void main(String args[]) {
        if (args.length == 0) {
            usage();
        }

        readOptions(args);
        validateOptions();

        if (createFiles) {
            createFiles();
        }

        try {
            readAndPublish();
        } catch (FileNotFoundException ex) {
            createFiles();
            try {
                readAndPublish();
            } catch (FileNotFoundException ex1) {
                logger.error(ex1.getMessage(), ex1);
            }
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static void createFiles() {
        ParetoGenerator generator = new ParetoGenerator(AVERAGE_SIZE * 2, 2.0);
        FileCreator creator = new FileCreator(PATHNAME);

        for (int i = 0; i < TOTAL_CONTENTS; i++) {
            try {
                creator.createFile((int) (generator.next() * 1024));
            } catch (FileNotFoundException ex) {
                logger.error(ex.getMessage(), ex);
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        if (!PATHNAME.endsWith(System.getProperty("file.separator"))) {
            PATHNAME += System.getProperty("file.separator");
        }
        
        FILENAME = PATHNAME + "files.txt";
        try {
            creator.createTxtFile(FILENAME);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static void readAndPublish() throws FileNotFoundException {

        FileRead fileRead = new FileRead(FILENAME);
        fileRead.read();
        List<String> records = fileRead.getRecords();

        for (String fName : records) {
            try {
                new Publisher(logger, fName, LIFETIME).announce();
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

    }

    private static void readOptions(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String option = args[i];
            if ("-c".equals(option)) {
                try {
                    CHUNK_SIZE = Integer.parseInt(args[++i]);
                    if (CHUNK_SIZE < 0) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    logger.error("invalid chunksize option " + args[i]);
                    System.exit(1);
                }
            } else if ("-l".equals(option)) {
                String loopbackAddr = args[++i];
                LOOPBACK_ADDR = loopbackAddr;
            } else if ("-p".equals(option)) {
                String loopbackPortStr = args[++i];
                try {
                    int port = Integer.parseInt(loopbackPortStr);
                    LOOPBACK_PORT = port;
                } catch (Exception e) {
                    logger.error("invalid port: " + loopbackPortStr);
                }
            } else if ("-f".equals(option)) {
                FILENAME = args[++i];
            } else if ("-avg".equals(option)) {
                String avg = args[++i];
                try {
                    double avgDouble = Double.parseDouble(avg);
                    AVERAGE_SIZE = avgDouble;
                } catch (Exception e) {
                    logger.error("invalid average size: " + avg);
                }
            } else if ("-tcon".equals(option)) {
                String tCon = args[++i];
                try {
                    int totalContents = Integer.parseInt(tCon);
                    TOTAL_CONTENTS = totalContents;
                } catch (Exception e) {
                    logger.error("invalid total contents: " + tCon);
                }
            } else if ("-path".equals(option)) {
                PATHNAME = args[++i];
            } else if ("-lt".equals(option)) {
                String strLTime = args[++i];
                try {
                    long lifeTime = Long.parseLong(strLTime);
                    LIFETIME = lifeTime;
                } catch (Exception e) {
                    logger.error("invalid lifetime: " + strLTime);
                }
            }
        }
    }

    private static void validateOptions() {
        if (PATHNAME.equals("")) {
            usage();
        }

        if (FILENAME.equals("")) {
            createFiles = true;
        } else {
            createFiles = false;
        }
    }

    private static void usage() {
        logger.error("Usage: -f <publications> [-l <loopback_address>] [-p <port>] [-c <chunksize>] [-lt <lifetime (ms)>]\n"
                + "Usage: -path <pathname> [-avg <averageContentSize>] [-tcon <totalContents>] [-l <loopback_address>] [-p <port>] [-c <chunksize>] [-lt <lifetime (ms)>]");
        System.exit(-1);
    }
}
