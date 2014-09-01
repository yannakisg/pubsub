package pubsub.node.controller.gui;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import pubsub.node.controller.gui.GraphElements.MyEdge;

/**
 *
 * @author John Gasparis
 */
public class EdgeMenu extends JPopupMenu {

    public EdgeMenu(final JFrame frame) {
        super("Edge Menu");
        this.add(new DeleteEdgeMenuItem());
        this.addSeparator();
        this.add(new PortDisplay());
        this.add(new WeightDisplay());
        this.addSeparator();
        this.add(new EdgePropertiesItem(frame));
    }

    private class DeleteEdgeMenuItem extends JMenuItem implements EdgeMenuListener {

        private MyEdge e;
        private VisualizationViewer vv;

        protected DeleteEdgeMenuItem() {
            super("Delete Edge");

            this.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evnt) {
                    vv.getPickedEdgeState().pick(e, false);
                    vv.getGraphLayout().getGraph().removeEdge(e);

                    /* TODO LinkDown */

                    vv.repaint();
                }
            });
        }

        @Override
        public void viewEdge(MyEdge e, VisualizationViewer vv) {
            this.e = e;
            this.vv = vv;
            this.setText("Delete Edge " + e);
        }
    }

    private class PortDisplay extends JMenuItem implements EdgeMenuListener {

        @Override
        public void viewEdge(MyEdge e, VisualizationViewer vv) {
            this.setText("Port = " + e.getPort());
        }
    }

    private class WeightDisplay extends JMenuItem implements EdgeMenuListener {

        @Override
        public void viewEdge(MyEdge e, VisualizationViewer vv) {
            this.setText("Weight = " + e.getWeight());
        }
    }

    private class EdgePropertiesItem extends JMenuItem implements EdgeMenuListener, MenuPointListener {

        private MyEdge e;
        private VisualizationViewer vv;
        private Point2D point;

        protected EdgePropertiesItem(final JFrame frame) {
            super("Edit Edge Properties");

            this.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    EdgePropertyDialog dialog = new EdgePropertyDialog(frame, EdgePropertiesItem.this.e);
                    dialog.setLocation((int) point.getX() + frame.getX(), (int) point.getY() + frame.getY());
                    dialog.setVisible(true);
                }
            });
        }

        @Override
        public void viewEdge(MyEdge e, VisualizationViewer vv) {
            this.e = e;
            this.vv = vv;
        }

        @Override
        public void setPoint(Point2D point) {
            this.point = point;
        }
    }

    private class EdgePropertyDialog extends JDialog {

        private final MyEdge e;
        private JLabel labelPort, labelWeight;
        private JTextField txtPort, txtWeight;
        private JButton btnOK;
        private JButton btnCancel;

        protected EdgePropertyDialog(JFrame frame, final MyEdge e) {
            super(frame, true);

            this.e = e;
            this.setTitle("Edge " + e);

            labelPort = new JLabel("Port : ");
            labelWeight = new JLabel("Weight : ");
            txtPort = new JTextField(e.getPort());
            txtWeight = new JTextField(String.valueOf(e.getWeight()));

            btnCancel = new JButton("Cancel");
            btnCancel.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    EdgePropertyDialog.this.dispose();
                }
            });

            btnOK = new JButton("OK");
            btnOK.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        e.setPort(Integer.parseInt(txtPort.getText()));
                        e.setWeight(Double.parseDouble(txtWeight.getText()));
                    } catch (NumberFormatException exc) {
                        txtPort.setText("");
                        txtWeight.setText("");
                        return;
                    }
                    dispose();
                }
            });

            this.setLayout(new GridLayout(3, 2));
            this.add(labelPort);
            this.add(txtPort);
            this.add(labelWeight);
            this.add(txtWeight);
            this.add(btnOK);
            this.add(btnCancel);

            this.getRootPane().setDefaultButton(btnOK);
            this.setSize(200, 100);
            this.setResizable(false);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
    }
}
