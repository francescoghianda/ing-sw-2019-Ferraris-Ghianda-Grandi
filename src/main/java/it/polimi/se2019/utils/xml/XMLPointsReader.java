package it.polimi.se2019.utils.xml;

import it.polimi.se2019.utils.logging.Logger;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XMLPointsReader
{
    private Document xmlDocument;

    public XMLPointsReader(String filePath)
    {
        try
        {
            if(!XMLValidator.isValid(filePath, "/xml/mapView/validator.xsd"))throw new NotValidXMLException(filePath+" - "+XMLValidator.getError());

            InputStream is = getClass().getResourceAsStream(filePath);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            xmlDocument = documentBuilder.parse(is);
            xmlDocument.getDocumentElement().normalize();
        }
        catch (ParserConfigurationException | SAXException | IOException | NotValidXMLException e)
        {
            e.printStackTrace();
        }
    }

    public Map<Point2D, Rectangle2D> getLeftPoints(int partNumber)
    {
        return parsePoints("left", partNumber);
    }

    public Map<Point2D, Rectangle2D> getRightPoint(int partNumber)
    {
        return parsePoints("right", partNumber);
    }

    private Map<Point2D, Rectangle2D> parsePoints(String side, int partNumber)
    {
        Map<Point2D, Rectangle2D> points = new HashMap<>();

        double blockDimension = Double.parseDouble(xmlDocument.getDocumentElement().getAttribute("blockDimension"));

        Element sideElement = (Element) xmlDocument.getElementsByTagName(side).item(0);
        Element partElement = (Element) sideElement.getElementsByTagName("part_"+ partNumber).item(0);
        NodeList pointList = partElement.getElementsByTagName("point");

        for(int i = 0; i < pointList.getLength(); i++) parsePoint((Element)pointList.item(i), points, blockDimension);

        return points;
    }

    private void parsePoint(Element pointElement, Map<Point2D, Rectangle2D> points, double blockDimension)
    {
        int blockX = Integer.parseInt(pointElement.getAttribute("blockX"));
        int blockY = Integer.parseInt(pointElement.getAttribute("blockY"));
        int x = Integer.parseInt(pointElement.getElementsByTagName("x").item(0).getTextContent());
        int y = Integer.parseInt(pointElement.getElementsByTagName("y").item(0).getTextContent());

        points.put(new Point2D(blockX, blockY), new Rectangle2D(x, y, blockDimension, blockDimension));

    }
}
