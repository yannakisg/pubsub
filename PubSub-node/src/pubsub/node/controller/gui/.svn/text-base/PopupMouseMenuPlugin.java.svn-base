package pubsub.node.controller.gui;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.JPopupMenu;
import pubsub.node.controller.gui.GraphElements.MyEdge;
import pubsub.node.controller.gui.GraphElements.MyVertex;

/**
 *
 * @author John Gasparis
 */
public class PopupMouseMenuPlugin<V, E> extends AbstractPopupGraphMousePlugin {

    private JPopupMenu edgePopup, vertexPopup;

    public PopupMouseMenuPlugin() {
        this(MouseEvent.BUTTON3_MASK);
    }

    private PopupMouseMenuPlugin(int modifiers) {
        super(modifiers);
    }

    @Override
    protected void handlePopup(MouseEvent me) {
        final VisualizationViewer<V, E> vv = (VisualizationViewer<V, E>) me.getSource();
        Point2D point = me.getPoint();
        GraphElementAccessor<V, E> pick = vv.getPickSupport();

        if (pick != null) {
            final V v = pick.getVertex(vv.getGraphLayout(), point.getX(), point.getY());
            if (v != null) {
                updateVertexMenu(v, vv, point);
                vertexPopup.show(vv, me.getX(), me.getY());
            } else {
                final E e = pick.getEdge(vv.getGraphLayout(), point.getX(), point.getY());
                if (e != null) {
                    updateEdgeMenu(e, vv, point);
                    edgePopup.show(vv, me.getX(), me.getY());
                }
            }
        }
    }

    private void updateVertexMenu(V v, VisualizationViewer<V, E> vv, Point2D point) {
        if (vertexPopup == null) {
            return;
        }

        Component[] components = vertexPopup.getComponents();
        for (Component com : components) {
            if (com instanceof VertexMenuListener) {
                ((VertexMenuListener) com).viewVertex((MyVertex) v, vv);
            }
            if (com instanceof MenuPointListener) {
                ((MenuPointListener) com).setPoint(point);
            }

        }
    }

    private void updateEdgeMenu(E e, VisualizationViewer<V, E> vv, Point2D point) {
        if (edgePopup == null) {
            return;
        }

        Component[] components = edgePopup.getComponents();
        for (Component com : components) {
            if (com instanceof EdgeMenuListener) {
                ((EdgeMenuListener) com).viewEdge((MyEdge) e, vv);
            }
            if (com instanceof MenuPointListener) {
                ((MenuPointListener) com).setPoint(point);
            }
        }
    }

    public void setVertexPopup(JPopupMenu vertexPopup) {
        this.vertexPopup = vertexPopup;
    }

    public void setEdgePopup(JPopupMenu edgePopup) {
        this.edgePopup = edgePopup;
    }
}
