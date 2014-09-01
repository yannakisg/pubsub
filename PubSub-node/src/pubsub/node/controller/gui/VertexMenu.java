package pubsub.node.controller.gui;

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

import pubsub.localrendezvous.LocRCFactory;
import pubsub.node.controller.gui.GraphElements.MyVertex;
import pubsub.node.debug.Command;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 *
 * @author John Gasparis
 */
public class VertexMenu extends JPopupMenu {

    public VertexMenu(final JFrame frame) {
        super("Vertex Menu");
        this.add(new DeleteVertexMenuItem());
        this.addSeparator();
        this.add(new NameMenuItem());
        this.add(new TypeMenuItem());
        this.add(new IPMenuItem());
        this.addSeparator();
        this.add(new RoutingTableItem(frame));
        this.add(new VertexPropertiesItem(frame));
    }

    private class DeleteVertexMenuItem extends JMenuItem implements VertexMenuListener {

        private MyVertex v;
        private VisualizationViewer vv;

        public DeleteVertexMenuItem() {
            super("Delete Vertex");

            this.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    vv.getPickedVertexState().pick(v, false);
                    vv.getGraphLayout().getGraph().removeVertex(v);

                    /* TODO LINK DOWN */

                    vv.repaint();
                }
            });
        }

        @Override
        public void viewVertex(MyVertex v, VisualizationViewer vv) {
            this.v = v;
            this.vv = vv;
            this.setText("Delete Vertex " + v);
        }
    }

    private class NameMenuItem extends JMenuItem implements VertexMenuListener {

        @Override
        public void viewVertex(MyVertex v, VisualizationViewer vv) {
            this.setText("Name = " + v.getName());
        }
    }

    private class TypeMenuItem extends JMenuItem implements VertexMenuListener {

        @Override
        public void viewVertex(MyVertex v, VisualizationViewer vv) {
            this.setText("Type = " + v.getStringType());
        }
    }

    private class IPMenuItem extends JMenuItem implements VertexMenuListener {

        @Override
        public void viewVertex(MyVertex v, VisualizationViewer vv) {
            this.setText("IP  = " + v.getIP());
        }
    }

    private class RoutingTableItem extends JMenuItem implements VertexMenuListener {

        private MyVertex v;
        private VisualizationViewer vv;

        protected RoutingTableItem(final JFrame frame) {
            super("View Routing Table");
            this.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Command com = new Command();
                    com.showRemoteTopologyG(v.getIP(), LocRCFactory.PORT);
                }
            });
        }

        @Override
        public void viewVertex(MyVertex v, VisualizationViewer vv) {
            this.v = v;
            this.vv = vv;
        }
    }

    private class VertexPropertiesItem extends JMenuItem implements VertexMenuListener, MenuPointListener {

        private MyVertex v;
        private VisualizationViewer vv;
        private Point2D point;

        protected VertexPropertiesItem(final JFrame frame) {
            super("Edit Vertex Properties");

            this.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    VertexPropertyDialog dialog = new VertexPropertyDialog(frame, v, vv);
                    dialog.setLocation((int) point.getX() + frame.getX(), (int) point.getY() + frame.getY());
                    dialog.setVisible(true);
                }
            });
        }

        @Override
        public void viewVertex(MyVertex v, VisualizationViewer vv) {
            this.v = v;
            this.vv = vv;
        }

        @Override
        public void setPoint(Point2D point) {
            this.point = point;
        }
    }

    private class VertexPropertyDialog extends JDialog {

        private JLabel labelIP;
        private JLabel labelType;
        private JLabel labelName;
        private JTextField txtType;
        private JTextField txtName;
        private JTextField txtIP;
        private JButton btnOK;
        private JButton btnCancel;

        protected VertexPropertyDialog(JFrame frame, final MyVertex v, final VisualizationViewer vv) {
            super(frame, true);

            this.setTitle("Vertex " + v);

            labelIP = new JLabel("IP Address : ");
            labelName = new JLabel("Name : ");
            labelType = new JLabel("Type : ");

            txtIP = new JTextField(v.getIP());
            txtName = new JTextField(v.getName());
            txtType = new JTextField(v.getStringType());

            btnCancel = new JButton("Cancel");
            btnCancel.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    VertexPropertyDialog.this.dispose();
                }
            });

            btnOK = new JButton("OK");
            btnOK.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (txtIP.getText().equals("") || txtName.getText().equals("")
                            || txtType.getText().equals("")) {
                        return;
                    }

                    v.setIP(txtIP.getText());
                    v.setName(txtName.getText());
                    v.setType(txtType.getText());

                    vv.repaint();

                    dispose();
                }
            });

            GridLayout layout = new GridLayout(4, 2);
            this.setLayout(layout);

            this.add(labelIP);
            this.add(txtIP);
            this.add(labelName);
            this.add(txtName);
            this.add(labelType);
            this.add(txtType);
            this.add(btnOK);
            this.add(btnCancel);

            this.getRootPane().setDefaultButton(btnOK);
            this.setSize(230, 120);
            this.setResizable(false);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
    }
}
