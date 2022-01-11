package io.ingestr.cli.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Pom {
    private String artifactId;
    private String groupId;
    private String version;
    private String description;

    public static Pom load() throws ParserConfigurationException, IOException, SAXException {
        File file = new File("pom.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);

        String artifactId = null;
        String groupId = null;
        String version = null;
        String description = null;

        NodeList childNodes = document.getDocumentElement().getChildNodes();
        for (int n = 0; n < childNodes.getLength(); n++) {
            Node node = childNodes.item(n);

            if (StringUtils.equalsIgnoreCase("artifactId", node.getNodeName())) {
                artifactId = node.getTextContent();
            }
            if (StringUtils.equalsIgnoreCase("groupId", node.getNodeName())) {
                groupId = node.getTextContent();
            }
            if (StringUtils.equalsIgnoreCase("version", node.getNodeName())) {
                version = node.getTextContent();
            }
            if (StringUtils.equalsIgnoreCase("description", node.getNodeName())) {
                description = node.getTextContent();
            }
        }

        return new Pom(artifactId, groupId, version, description);
    }
}
