package pubsub.node.debug;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.TransformerUtils;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;
import pubsub.ByteIdentifier;
import pubsub.messages.MessageType;
import pubsub.tmc.graph.Link;
import pubsub.messages.ipc.DebugTopologyMessage;

/**
 *
 * @author John Gasparis
 */
public class NodeViewer extends JFrame {

    private Map<ByteIdentifier, Map<ByteIdentifier, Link>> topology;
    private Map<ByteIdentifier, Link> neighborHosts;
    private VisualizationViewer<ByteIdentifier, Link> visual;
    private Graph<ByteIdentifier, Link> graph;
    private GraphMouse<ByteIdentifier, Link> gm;
    private CircleLayout layout;
    private List<ByteIdentifier> vertices;
    private MessageType.Type msgType;

    public NodeViewer(byte[] data) {
        super("PSI - Node Viewer");

        DebugTopologyMessage message = DebugTopologyMessage.createNew(data);
        this.msgType = message.getMessageType();


        vertices = new LinkedList<ByteIdentifier>();

        if (msgType == MessageType.Type.GET_NEIGHBORS) {
            topology = message.getTopology();
            for (ByteIdentifier id : topology.keySet()) {
                vertices.add(id);
            }
        } else {
            neighborHosts = message.getHosts();
            for (Link link : neighborHosts.values()) {
                vertices.add(link.getEndpoint().getID());
            }
        }


        setSize(new Dimension(800, 800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        createGraph();

        Container content = getContentPane();
        GraphZoomScrollPane zoom = new GraphZoomScrollPane(visual);
        content.add(zoom);


        JComboBox modeBox = gm.getModeComboBox();
        modeBox.addItemListener(gm.getModeListener());

        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                scaler.scale(visual, 1.1f, visual.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                scaler.scale(visual, 1 / 1.1f, visual.getCenter());
            }
        });

        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2, 1));

        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        zoomControls.add(plus);
        zoomControls.add(minus);

        controls.add(zoomControls);
        controls.add(modeBox);

        content.add(controls, BorderLayout.SOUTH);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width / 2) - (this.getWidth() / 2);
        int y = (screen.height / 2) - (this.getHeight() / 2);
        this.setLocation(x, y);

        this.setVisible(true);
    }

    private void createGraph() {
        if (layout != null) {
            layout.reset();
        }
        if (visual != null) {
            visual.removeAll();
        }

        graph = new UndirectedOrderedSparseMultigraph<ByteIdentifier, Link>();

        layout = new CircleLayout(graph);
        layout.setVertexOrder(vertices);

        Dimension preferredSize = new Dimension(400, 400);

        final VisualizationModel<ByteIdentifier, Link> visualizationModel = new DefaultVisualizationModel<ByteIdentifier, Link>(layout);

        visual = new VisualizationViewer<ByteIdentifier, Link>(visualizationModel, preferredSize);

        visual.getRenderContext().setVertexLabelTransformer(MapTransformer.<ByteIdentifier, String>getInstance(
                LazyMap.<ByteIdentifier, String>decorate(new HashMap<ByteIdentifier, String>(), new ToStringLabeller<ByteIdentifier>())));

        visual.getRenderContext().setEdgeLabelTransformer(MapTransformer.<Link, String>getInstance(
                LazyMap.<Link, String>decorate(new HashMap<Link, String>(), new ToStringLabeller<Link>())));


        visual.setBackground(Color.white);

        visual.setVertexToolTipTransformer(new ToStringLabeller<ByteIdentifier>());


        gm = new GraphMouse<ByteIdentifier, Link>();
        gm.setMode(ModalGraphMouse.Mode.PICKING);

        visual.setGraphMouse(gm);

        createNodes();
    }

    private void createNodes() {
        Map<ByteIdentifier, String> map = new HashMap<ByteIdentifier, String>();
        Transformer<ByteIdentifier, String> trans;
        Map<ByteIdentifier, Link> mapTemp;

        visual.getRenderContext().getPickedVertexState().clear();
        visual.getRenderContext().getPickedEdgeState().clear();

        for (ByteIdentifier id : vertices) {
            map.put(id, id.toString());
            graph.addVertex(id);
        }

        if (msgType == MessageType.Type.DEBUG_TOPOLOGY) {
            for (ByteIdentifier id : vertices) {
                mapTemp = topology.get(id);

                for (Link link : mapTemp.values()) {
                    graph.addEdge(link, id, link.getEndpoint().getID(), EdgeType.UNDIRECTED);
                }
            }
        }

        trans = TransformerUtils.mapTransformer(map);
        visual.setVertexToolTipTransformer(trans);
        visual.repaint();
    }
}
