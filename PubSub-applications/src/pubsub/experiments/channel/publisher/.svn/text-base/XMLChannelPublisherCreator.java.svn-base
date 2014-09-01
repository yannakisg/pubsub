package pubsub.experiments.channel.publisher;

import java.io.File;
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
import pubsub.transport.channel.source.ChannelSource.ChannelType;

/**
 *
 * @author John Gasparis
 */
public class XMLChannelPublisherCreator {

    private static final Logger logger = Logger.getLogger(XMLChannelPublisherCreator.class);
    private Document doc;
    private String[] channelNames;
    private long bitRate;
    private ChannelType type;

    public XMLChannelPublisherCreator(String[] channelNames, long bitRate, ChannelType type) {
        this.channelNames = channelNames;
        this.bitRate = bitRate;
        this.type = type;
    }

    public void createXMLFile() {
        for (String channelName : channelNames) {
            createDocument();
            createDomTree(channelName);

            writeToFile(channelName);
        }
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

    private void createDomTree(String channelName) {
        Element rootElement = doc.createElement("configuration");
        Element element;
        doc.appendChild(rootElement);

        element = createNodeElement(channelName);
        rootElement.appendChild(element);
    }

    private Element createNodeElement(String channelName) {
        Element element = doc.createElement("channel");

        Element nameElement = doc.createElement("name");
        Text chName = doc.createTextNode(channelName);
        nameElement.appendChild(chName);

        Element bitRateElement = doc.createElement("bitrate");
        Text bitText = doc.createTextNode(Long.toString(bitRate));
        bitRateElement.appendChild(bitText);

        Element typeElement = doc.createElement("type");
        Text typeText = doc.createTextNode(type.toString());
        typeElement.appendChild(typeText);

        element.appendChild(nameElement);
        element.appendChild(bitRateElement);
        element.appendChild(typeElement);


        return element;
    }

    private void writeToFile(String channelName) {
        try {
            File file = new File(channelName + ".xml");

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

    public static void main(String args[]) {
        String[] chNames = {"Discovery", "Fox", "National_Geographic", "Syfy", "Novasports", "Cartoon_Network", "NovaCinema", "Sky_Sports"};
        XMLChannelPublisherCreator creator = new XMLChannelPublisherCreator(chNames, 128000, ChannelType.MULTICAST);
        creator.createXMLFile();
    }
}
