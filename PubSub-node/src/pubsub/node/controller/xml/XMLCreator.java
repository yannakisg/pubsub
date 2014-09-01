package pubsub.node.controller.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 *
 * @author John Gasparis
 */
public class XMLCreator {

    private static final Logger logger = Logger.getLogger(XMLCreator.class);
    private File file;
    private Document doc;
    private List<XMLNode> nodeList;

    public XMLCreator(File file) {
        this.file = file;
        this.nodeList = new ArrayList<XMLNode>();
    }

    public void addXMLNode(XMLNode node) {
        nodeList.add(node);
    }

    public void createXMLFile() {
        if (nodeList.isEmpty()) {
            return;
        }

        createDocument();

        createDomTree();

        writeToFile();
    }

    private void createDocument() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            doc = db.newDocument();
        } catch (ParserConfigurationException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void createDomTree() {
        Element rootElement = doc.createElement("topology");
        Element element;
        doc.appendChild(rootElement);

        for (XMLNode node : nodeList) {
            element = createNodeElement(node);
            rootElement.appendChild(element);
        }
    }

    private Element createNodeElement(XMLNode node) {
        Element element = doc.createElement("element");
        element.setAttribute("type", node.getType());
        element.setAttribute("name", node.getName());
        element.setAttribute("x", Double.toString(node.getX()));
        element.setAttribute("y", Double.toString(node.getY()));

        Element ipElement = doc.createElement("ip");
        Text ipText = doc.createTextNode(node.getIP());
        ipElement.appendChild(ipText);
        element.appendChild(ipElement);


        Element connectionElement;
        for (XMLNode.AttachedNode attNode : node.getAttachedNodes()) {
            connectionElement = doc.createElement("connection");
            connectionElement.setAttribute("node", attNode.getConnectionNodeName());
            connectionElement.setAttribute("port", String.valueOf(attNode.getPort()));
            connectionElement.setAttribute("weight", String.valueOf(attNode.getWeight()));
            element.appendChild(connectionElement);
        }

        return element;
    }

    private void writeToFile() {
        try {
            Source source = new DOMSource(doc);

            Result result = new StreamResult(file);

            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        } catch (TransformerConfigurationException ex) {
            logger.error(ex.getMessage(), ex);
        } catch (TransformerException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
