package me.videogame.recaf.intellij.utils;

import me.videogame.recaf.RecafExtension;
import me.videogame.recaf.constants.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IntellijUtils {
    public static IntellijUtils INSTANCE = new IntellijUtils();

    public void createRunConfig(RecafExtension extension) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String mainClass = extension.getMainClass();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(new File(Constants.INTELLIJ_WORKSPACE));

        Node rootNode = document.getElementsByTagName(Constants.PROJECT).item(0);

        NodeList rootNodeChildren = rootNode.getChildNodes();

        for (int i = 0; i < rootNodeChildren.getLength(); i++) {
            Node projectNodeChild = rootNodeChildren.item(i);
            String nodeName = projectNodeChild.getNodeName();

            if (nodeName.equals(Constants.COMPONENT)) {
                String nodeAttributeName = projectNodeChild
                        .getAttributes()
                        .getNamedItem(Constants.NAME)
                        .getTextContent();

                if (nodeAttributeName.equals(Constants.RUN_MANAGER)) {

                    Map<String, String> recafElementMap = new HashMap<>();

                    // Name
                    recafElementMap.put(Constants.NAME, Constants.RUN_CONFIG_NAME);
                    recafElementMap.put(Constants.TYPE, Constants.RUN_CONFIG_TYPE);
                    recafElementMap.put(Constants.FACTORY_NAME, Constants.RUN_CONFIG_TYPE);
                    recafElementMap.put(Constants.NAME_IS_GENERATED, Constants.TRUE);

                    Element recafElement = generateConfiguration(document, recafElementMap);

                    // Main Class Option
                    Map<String, String> mainClassOptions = new HashMap<>();
                    mainClassOptions.put(Constants.NAME, Constants.MAIN_CLASS_NAME);
                    mainClassOptions.put(Constants.VALUE, Constants.MAIN_CLASS);

                    recafElement.appendChild(generateOption(document, mainClassOptions));

                    // Program Arguments
                    Map<String, String> programArguments = new HashMap<>();
                    programArguments.put(Constants.NAME, Constants.PROGRAM_PARAMETERS);
                    programArguments.put(Constants.VALUE, "--mainClass " + Objects.requireNonNull(mainClass));

                    recafElement.appendChild(generateOption(document, programArguments));

                    Map<String, String> methodOptions = new HashMap<>();
                    methodOptions.put("v", "2");
                    Element method = generateMethod(document, methodOptions);

                    Map<String, String> make = new HashMap<>();
                    make.put(Constants.NAME, Constants.INTELLIJ_MAKE);
                    make.put(Constants.ENABLED, Constants.TRUE);
                    method.appendChild(generateOption(document, make));

                    projectNodeChild.appendChild(recafElement);

                    Node listNode = null;
                    for (int i1 = 0; i1 < projectNodeChild.getChildNodes().getLength(); i1++) {
                        Node node = projectNodeChild.getChildNodes().item(i1);
                        if (node.getNodeName().equals(Constants.LIST)) {
                            listNode = node;
                            break;
                        }
                    }

                    if (listNode == null) {
                        listNode = document.createElement("list");
                    }

                    listNode.appendChild(generateListItem(document, Constants.RUN_CONFIG_TYPE + "." + Constants.RUN_CONFIG_NAME));

                    TransformerFactory factory1 = TransformerFactory.newInstance();

                    Transformer transformer = factory1.newTransformer();

                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, Constants.NO);
                    transformer.setOutputProperty(OutputKeys.METHOD, Constants.XML);
                    transformer.setOutputProperty(OutputKeys.INDENT, Constants.YES);
                    transformer.setOutputProperty(OutputKeys.ENCODING, Constants.UTF8);
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                    DOMSource source = new DOMSource(document);
                    StreamResult result = new StreamResult(new File(Constants.INTELLIJ_WORKSPACE));

                    transformer.transform(source, result);
                    break;
                }
            }
        }
    }

    public Element generateConfiguration(Document document, Map<String, String> attributes) {
        return generateElement(document, Constants.INTELLIJ_CONFIGURATION, attributes);
    }

    public Element generateOption(Document document, Map<String, String> attributes) {
        return generateElement(document, Constants.INTELLIJ_OPTION, attributes);
    }

    public Element generateMethod(Document document, Map<String, String> attributes) {
        return generateElement(document, Constants.INTELLIJ_METHOD, attributes);
    }

    public Element generateListItem(Document document, String value) {
        Map<String, String> map = new HashMap<>();
        map.put("itemvalue", value);
        return generateElement(document, Constants.ITEM, map);
    }

    private Element generateElement(Document document, String tagName, Map<String, String> attributes) {
        Element element = document.createElement(tagName);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            element.setAttribute(entry.getKey(), entry.getValue());
        }

        return element;
    }
}
