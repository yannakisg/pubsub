package pubsub.experiments.channel.subscriber;

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
import pubsub.distribution.UniformGenerator;

/**
 *
 * @author John Gasparis
 */
public class XMLChannelSubscriberCreator {

    private static final Logger logger = Logger.getLogger(XMLChannelSubscriberCreator.class);
    private Document doc;
    private String[] channelNames;
    private int totalSubscribers;
    private UniformGenerator chGenerator;
    private UniformGenerator zapGenerator = new UniformGenerator(2, 7);
    private UniformGenerator zapTimeGenerator = new UniformGenerator(10, 30);
    int prevChannel = -1;

    public XMLChannelSubscriberCreator(String[] channelNames, int totalSubscribers, double min, double max) {
        this.channelNames = channelNames;
        this.totalSubscribers = totalSubscribers;
        this.chGenerator = new UniformGenerator(min, max);
    }

    public void createXMLFile() {
        String name;
        for (int i = 0; i < totalSubscribers; i++) {
            name = "subscriber_channel" + i;
            createDocument();
            createDomTree();

            writeToFile(name);
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

    private void createDomTree() {
        Element rootElement = doc.createElement("configuration");
        Element element;
        doc.appendChild(rootElement);
        int totalDuration = 0;
        int maxDuration = 7200;
        int duration;
        int channel;
        int zapping;
        int zapTime;

        while (totalDuration < maxDuration) {
            duration = (int) Math.round(chGenerator.uniform());
            channel = chGenerator.uniform(channelNames.length);
            while (channel == prevChannel) {
                channel = chGenerator.uniform(channelNames.length);
            }
            prevChannel = channel;

            element = createNodeElement(channel, duration);
            rootElement.appendChild(element);

            zapping = (int) Math.round(zapGenerator.uniform());
            
            for (int i = 0; i < zapping; i++) {
                zapTime = (int) Math.round(zapTimeGenerator.uniform());
                element = createNodeElement((channel + i + 1) % channelNames.length, zapTime);
                rootElement.appendChild(element);
                totalDuration += zapTime;
            }
            totalDuration += duration;
        }
    }

    private Element createNodeElement(int channel, int duration) {
        Element element = doc.createElement("channel");

        Element nameElement = doc.createElement("name");
        Text chName = doc.createTextNode(channelNames[channel]);
        nameElement.appendChild(chName);

        Element bitRateElement = doc.createElement("duration");
        Text bitText = doc.createTextNode(Long.toString(duration));
        bitRateElement.appendChild(bitText);

        element.appendChild(nameElement);
        element.appendChild(bitRateElement);

        return element;
    }

    private void writeToFile(String fileName) {
        try {
            File file = new File(fileName + ".xml");

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
        String[] chNames = {"Discovery", "Fox", "National_Geographic", "Syfy", "Novasports", "Cartoon_Network"};
        XMLChannelSubscriberCreator creator = new XMLChannelSubscriberCreator(chNames, 30, 240, 480);
        creator.createXMLFile();
    }
}
