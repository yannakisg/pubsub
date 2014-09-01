package pubsub.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import pubsub.forwarding.CachingElement;
import pubsub.forwarding.CachingElement.CachingPolicy;
import pubsub.forwarding.communication.AttachLinkServer;
import pubsub.localrendezvous.LocRCFactory;
import pubsub.rva.RVA_Mode;
import pubsub.tmc.TMC_Mode;
import pubsub.tmc.TMComponentFactory;

public class Configuration {

    private static String FILENAME = "/home/gaspar/pubSub/config.properties";
    private static boolean inClasspath = false;

    public static void setConfFile(String name) {
        inClasspath = false;
        FILENAME = name;
    }
    private static Properties properties = new Properties();
    private static final String RVA_TYPE = "rva.type";
    private static RVA_Mode rvaMode;
    private static final String TMC_TYPE = "tmc.type";
    private static TMC_Mode tmcMode;
    private static final String LOCRC_DEF_PORT = "localrc.default_port";
    private static final String LOCRC_LOCAL_IP = "localrc.localhost";
    private static final String LOCRC_FILENAME = "localrc.filename";
    private static final String LOCRC_TYPE = "localrc.type";
    private static LocRCFactory.LocRCType locRcType;
    private static final String FWD_ATTACH_PORT_ = "fwd.attachlinkport";
    private static final String CACHE_SIZE = "cache.size";
    private static final String CACHE_POLICY = "cache.policy";
    private static final String CACHE_PROBABILITY = "cache.probability";

    public static void readConfiguration() throws ConfigurationException {
        File file = null;
        if (inClasspath) {
            URL resource = Configuration.class.getClassLoader().getResource(
                    FILENAME);
            if (resource == null) {
                throw new ConfigurationException(FILENAME
                        + " not found in classpath");
            }
            file = new File(resource.getFile());
        } else {
            file = new File(FILENAME);
        }

        readConfiguration(file);
    }

    public static void readConfiguration(File file)
            throws ConfigurationException {
        try {
            properties.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new ConfigurationException(e);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }
    
    public static LocRCFactory.LocRCType getLocRCType() {
        if (locRcType == null) {
            String property = properties.getProperty(LOCRC_TYPE);
            if (property == null) {
                throw new RuntimeException(LOCRC_TYPE + " not set in conf file");
            }
            
            property = property.toLowerCase().trim();
            
            // nio|uds
            if ("nio".equals(property)) {
                locRcType = LocRCFactory.TYPE.NIO;
            } else if ("uds".equals(property)) {
                locRcType = LocRCFactory.TYPE.UDS;
            } else {
                throw new RuntimeException("invalid option for locRC type: " + property);
            }
        }
        
        return locRcType;
    }

    public static TMC_Mode getTMCMode() {
        if (tmcMode == null) {
            String property = properties.getProperty(TMC_TYPE);
            if (property == null) {
                throw new RuntimeException(TMC_TYPE + " not set in conf file");
            }
            
            property = property.toLowerCase().trim();
            // host|router|rvp
            if ("host".equals(property)) {
                tmcMode = TMC_Mode.HOST;
            } else if ("router".equals(property)) {
                tmcMode = TMC_Mode.ROUTER;
            } else if ("rvp".equals(property)) {
                tmcMode = TMC_Mode.RVP;
            } else {
                throw new RuntimeException("invalid option for tmc type: " + property);
            }
        }
        return tmcMode;
    }

    public static RVA_Mode getRVAMode() {
        if (rvaMode == null) {
            String property = properties.getProperty(RVA_TYPE);
            if (property == null) {
                throw new RuntimeException(RVA_TYPE + " not set in conf file");
            }
            
            property = property.toLowerCase().trim();
            // host|router|rvp
            if ("host".equals(property)) {
                rvaMode = RVA_Mode.HOST;
            } else if ("router".equals(property)) {
                rvaMode = RVA_Mode.ROUTER;
            } else if ("rvp".equals(property)) {
                rvaMode = RVA_Mode.RVP;
            } else {
                throw new RuntimeException("invalid option for rva type: " + property);
            }
        }
        return rvaMode;
    }

    public static void install() {
        TMComponentFactory.configureTMCMode(getTMCMode());
        LocRCFactory.configureLocRCType(getLocRCType());
        
        getRVAMode();

        String property = properties.getProperty(FWD_ATTACH_PORT_);
        if (property != null) {
            try {
                int port = Integer.parseInt(property);
                AttachLinkServer.configureListeningPort(port);
            } catch (Exception e) {
                System.out.println("invalid port for fwd_attach_link_listener: " + property);
            }
        }

        property = properties.getProperty(LOCRC_LOCAL_IP);
        if (property != null) {
            LocRCFactory.configureHostAddr(property);
        }

        property = properties.getProperty(LOCRC_DEF_PORT);
        if (property != null) {
            try {
                int port = Integer.parseInt(property);
                LocRCFactory.configureHostPort(port);
            } catch (Exception e) {
                throw new RuntimeException("invalid " + LOCRC_DEF_PORT + ": " + property);
            }
        }
        
        property = properties.getProperty(LOCRC_FILENAME);
        if (property != null) {
            LocRCFactory.configureFileName(property);
        }

        property = properties.getProperty(FWD_ATTACH_PORT_);
        if (property != null) {
            try {
                int port = Integer.parseInt(property);
                AttachLinkServer.configureListeningPort(port);
            } catch (Exception e) {
                throw new RuntimeException("invalid " + FWD_ATTACH_PORT_ + ": " + property);
            }
        }

        property = properties.getProperty(CACHE_SIZE);
        if (property != null) {
            try {
                int size = Integer.parseInt(property);
                CachingElement.configureDefaultCacheSize(size);
            } catch (Exception e) {
                throw new RuntimeException("invalid " + CACHE_SIZE + ": " + property);
            }
        }

        property = properties.getProperty(CACHE_POLICY);
        if (property != null) {
            try {
                CachingElement.CachingPolicy policy = CachingPolicy.getPolicy(property.trim());
                CachingElement.configureDefaultPolicy(policy);
            } catch (Exception e) {
                throw new RuntimeException("invalid " + CACHE_POLICY + ": " + property + ". Returned error: " + e.getMessage());
            }
        }

        property = properties.getProperty(CACHE_PROBABILITY);
        if (property != null) {
            try {
                Double val = Double.parseDouble(property);
                CachingElement.configureCachingProbability(val);
            } catch (Exception e) {
                throw new RuntimeException("invalid " + CACHE_PROBABILITY + ": " + property);
            }
        }
    }
}
