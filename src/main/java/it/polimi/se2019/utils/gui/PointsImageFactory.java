package it.polimi.se2019.utils.gui;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public abstract class PointsImageFactory
{
    public static final Image ONE = new Image(PointsImageFactory.class.getResourceAsStream("/img/points/1.png"));
    public static final Image TWO = new Image(PointsImageFactory.class.getResourceAsStream("/img/points/2.png"));
    public static final Image FOUR = new Image(PointsImageFactory.class.getResourceAsStream("/img/points/4.png"));

    public static List<Image> getPointsImages(int points)
    {
        List<Image> images = new ArrayList<>();
        while(points > 0)
        {
            if(points >= 4)
            {
                images.add(FOUR);
                points -= 4;
            }
            else if(points >= 2)
            {
                images.add(TWO);
                points -= 2;
            }
            else
            {
                images.add(ONE);
                points--;
            }
        }
        return images;
    }
}
