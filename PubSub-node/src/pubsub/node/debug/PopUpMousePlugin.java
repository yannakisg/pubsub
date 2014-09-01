package pubsub.node.debug;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import pubsub.tmc.graph.Node;

/**
 *
 * @author John Gasparis
 */
public class PopUpMousePlugin<V, E> extends AbstractPopupGraphMousePlugin {

    private JPopupMenu popup;

    public PopUpMousePlugin() {
        popup = new JPopupMenu();
    }

    @Override
    protected void handlePopup(MouseEvent me) {
        popup.removeAll();

        final VisualizationViewer<V, E> visual = (VisualizationViewer<V, E>) me.getSource();

        final Layout<V, E> layout = visual.getGraphLayout();

        final Point2D point = me.getPoint();

        final Point2D ppoint = point;

        GraphElementAccessor<V, E> pickSupport = visual.getPickSupport();

        if (pickSupport != null) {
            final V vertex = pickSupport.getVertex(layout, ppoint.getX(), ppoint.getY());

            final PickedState<V> pickedVertexState = visual.getPickedVertexState();

            JMenu showInfo = null;

            if (vertex != null) {
                Set<V> pickedSet = pickedVertexState.getPicked();

                if (pickedSet.size() > 0) {
                    showInfo = new JMenu("View Info");

                    popup.add(showInfo);
                    JMenuItem[][] itemArray = new JMenuItem[pickedSet.size()][3];
                    int index = 0;
                    for (final V next : pickedSet) {
                        itemArray[index][0] = new JMenuItem("Routing Table - " + next);
                        itemArray[index][0].addActionListener(new ActionList(next));

                        itemArray[index][1] = new JMenuItem("Foo1 - " + next);
                        itemArray[index][1].addActionListener(new ActionList(next));

                        itemArray[index][2] = new JMenuItem("Foo2 - " + next);
                        itemArray[index][2].addActionListener(new ActionList(next));


                        showInfo.add(itemArray[index][0]);
                        showInfo.add(itemArray[index][1]);
                        showInfo.add(itemArray[index][2]);

                        if (pickedSet.size() > 0 && index < pickedSet.size() - 1) {
                            showInfo.add(new JSeparator());
                        }

                        index++;
                    }
                }
            }
            if (popup.getComponentCount() > 0) {
                popup.show(visual, me.getX(), me.getY());
            }

            pickedVertexState.clear();

        }


    }

    private class ActionList implements ActionListener {

        private V node;

        public ActionList(V s) {
            node = s;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Node temp = (Node) node;

            if (e.getActionCommand().equals("Routing Table - " + temp)) {
                SwingUtilities.invokeLater(new ShowGui(temp, 0));
            } else if (e.getActionCommand().equals("Foo1 - " + temp)) {
                SwingUtilities.invokeLater(new ShowGui(temp, 1));
            } else {
                SwingUtilities.invokeLater(new ShowGui(temp, 2));
            }
        }
    }

    private class ShowGui implements Runnable {

        private Node node;
        private int i;

        ShowGui(Node node, int i) {
            this.node = node;
            this.i = i;
        }

        @Override
        public void run() {
            if (node == null) {
                return;
            }

            if (i == 0) {
                System.out.println("I => 0");
            } else if (i == 1) {
                System.out.println("I => 1");
            } else {
                System.out.println("I => 2");
            }

        }
    }
}
