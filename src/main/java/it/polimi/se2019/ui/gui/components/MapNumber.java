package it.polimi.se2019.ui.gui.components;

import it.polimi.se2019.utils.xml.XMLPointsReader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

import java.util.HashMap;


public enum MapNumber
{
    MAP_1(2, 2), MAP_2(2, 1), MAP_3(1, 1), MAP_4(1, 2);

    private Image leftImage;
    private Image rightImage;

    private HashMap<Point2D, Rectangle2D> leftAmmoPosition;
    private HashMap<Point2D, Rectangle2D> rightAmmoPosition;

    private int left;
    private int right;

    private static XMLPointsReader reader = new XMLPointsReader("/xml/mapView/ammoCardPoints.xml");

    MapNumber(int left, int right)
    {
        this.left = left;
        this.right = right;
        loadImages();
    }

    private void loadImages()
    {
        String path = "/img/map/left/1.png";
        if(left != 1)path = "/img/map/left/2.png";
        this.leftImage = new Image(getClass().getResourceAsStream(path));
        path = "/img/map/right/1.png";
        if(right != 1)path = "/img/map/right/2.png";
        this.rightImage = new Image(getClass().getResourceAsStream(path));
    }

    HashMap<Point2D, Rectangle2D> getLeftAmmoPosition()
    {
        if(leftAmmoPosition == null)leftAmmoPosition = new HashMap<>(reader.getLeftPoints(left));
        return this.leftAmmoPosition;
    }

    HashMap<Point2D, Rectangle2D> getRightAmmoPosition()
    {
        if(rightAmmoPosition == null)rightAmmoPosition = new HashMap<>(reader.getRightPoint(right));
        return this.rightAmmoPosition;
    }

    Image getLeftImage()
    {
        return this.leftImage;
    }

    Image getRightImage()
    {
        return this.rightImage;
    }
}

