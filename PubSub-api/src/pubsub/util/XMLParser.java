package pubsub.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author John Gasparis
 */
public abstract class XMLParser {
    private final Logger logger = Logger.getLogger(XMLParser.class);
    private String fileName;
    protected Document doc;
    private List<XMLElement> nodeList;
    
    public XMLParser(String fileName) {
        this.fileName = fileName;
        this.nodeList = new ArrayList<XMLElement>();
    }
    
    public void parseXMLFile(String tagName) {
        parseFile();

        parseDocument(tagName);
    }
    
    private void parseFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(fileName);
        } catch (ParserConfigurationException ex) {
            logger.error(ex.getMessage(), ex);
        } catch (SAXException ex) {
            logger.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    private void parseDocument(String tagName) {
        Element rootElement = doc.getDocumentElement();
        NodeList nList = rootElement.getElementsByTagName(tagName);
        Element element;
        XMLElement xmlElement;

        if (nList != null && nList.getLength() > 0) {
            for (int i = 0; i < nList.getLength(); i++) {
                element = (Element) nList.item(i);

                xmlElement = getXMLElement(element);

                nodeList.add(xmlElement);
            }
        }
    }
    
    protected String getTextValue(Element element, String tagName) {
        String value = "";
        NodeList nList = element.getElementsByTagName(tagName);

        if (nList != null && nList.getLength() > 0) {
            Element elmnt = (Element) nList.item(0);
            value = elmnt.getFirstChild().getNodeValue();
        }

        return value;
    }

    public List<XMLElement> getNodeList() {
        return nodeList;
    }
    
    protected abstract XMLElement getXMLElement(Element nodeElement);
    
    public interface XMLElement {
        
    }
}
