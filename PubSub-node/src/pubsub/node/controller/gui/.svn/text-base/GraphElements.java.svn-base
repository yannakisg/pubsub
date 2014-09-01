package pubsub.node.controller.gui;

import org.apache.commons.collections15.Factory;

/**
 *
 * @author John Gasparis
 */
public class GraphElements {

    public static class MyEdge {

        private String name;
        private int port;
        private double weight;

        public MyEdge(String name) {
            this.name = name;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getPort() {
            return this.port;
        }

        public double getWeight() {
            return this.weight;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        
        public static class EdgeFactory implements Factory<MyEdge> {

            private static int totalEdges = 0;
            private static double defaultWeight = 1.0;
            private static int defaultPort = 20000;
            private static EdgeFactory instance = new EdgeFactory();

            public static EdgeFactory getInstance() {
                return instance;
            }

            @Override
            public MyEdge create() {
                String name = "Link" + totalEdges++;
                MyEdge edge = new MyEdge(name);
                edge.setPort(defaultPort);
                edge.setWeight(defaultWeight);

                return edge;
            }

            public static int getDefaultPort() {
                return defaultPort;
            }

            public static double getDefaultWeight() {
                return defaultWeight;
            }

            public static void setDefaultPort(int port) {
                defaultPort = port;
            }

            public static void setDefaultWeight(double weight) {
                defaultWeight = weight;
            }

            public static void reset() {
                totalEdges = 0;
                defaultWeight = 1.0;
                defaultPort = 20000;
            }
        }
    }

    public static class MyVertex {

        public enum VertexType {

            ROUTER,
            RVP,
            HOST,
            DEFAULT
        }
        private String name;
        private String ip;
        private VertexType type;

        public MyVertex(String name) {
            this.name = name;
            this.type = VertexType.DEFAULT;
        }
        

        @Override
        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIP() {
            return this.ip;
        }

        public void setIP(String ip) {
            this.ip = ip;
        }

        public void setType(String type) {
            type = type.toLowerCase();
            if (type.equals("router")) {
                this.type = VertexType.ROUTER;
            } else if (type.equals("rvp")) {
                this.type = VertexType.RVP;
            } else if (type.equals("host")) {
                this.type = VertexType.HOST;
            } else {
                this.type = VertexType.DEFAULT;
            }
        }

        public void setType(VertexType type) {
            this.type = type;
        }

        public String getStringType() {
            if (type == VertexType.ROUTER) {
                return "router";
            } else if (type == VertexType.HOST) {
                return "host";
            } else if (type == VertexType.RVP) {
                return "rvp";
            } else {
                return "default";
            }
        }

        public VertexType getType() {
            return this.type;
        }

        public static class VertexFactory implements Factory<MyVertex> {

            private static int totalNodes = 0;
            private static String defaultIP = "NONE";
            private static VertexFactory instance = new VertexFactory();

            public static VertexFactory getInstance() {
                return instance;
            }

            @Override
            public MyVertex create() {
                String name = "Node" + totalNodes++;
                MyVertex vertex = new MyVertex(name);
                vertex.setIP(defaultIP);
                return vertex;
            }

            public static void setDefaultIP(String ip) {
                defaultIP = ip;
            }

            public static String getDefaultIP() {
                return defaultIP;
            }

            public static void reset() {
                totalNodes = 0;
            }
        }
    }
}
