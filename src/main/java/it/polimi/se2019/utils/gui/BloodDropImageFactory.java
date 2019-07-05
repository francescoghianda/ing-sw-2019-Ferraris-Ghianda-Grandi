package it.polimi.se2019.utils.gui;

import it.polimi.se2019.utils.constants.GameColor;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.EnumMap;

/**
 * creates the blood drop image on the gameboard
 */
public class BloodDropImageFactory
{
    private static BloodDropImageFactory instance;

    private final EnumMap<GameColor, Image> images;
    private final Image baseImage;

    private BloodDropImageFactory()
    {
        images = new EnumMap<>(GameColor.class);
        baseImage = new Image(getClass().getResourceAsStream("/img/blood.png"));
        generateImages();
    }

    public static BloodDropImageFactory getInstance()
    {
        if(instance == null)instance = new BloodDropImageFactory();
        return instance;
    }

    public Image getBloodDropImage(GameColor color)
    {
        return images.get(color);
    }

    private void generateImages()
    {
        for(GameColor color : GameColor.values())images.put(color, generateImage(color));
    }

    private Image generateImage(GameColor color)
    {
        int width = (int)baseImage.getWidth();
        int height = (int)baseImage.getHeight();
        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();
        PixelReader reader = baseImage.getPixelReader();

        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                if(isColorToSubstitute(reader.getColor(x, y))) writer.setColor(x, y, Color.web(color.getColor()));
                else writer.setColor(x, y, reader.getColor(x, y));
            }
        }

        return image;
    }

    private boolean isColorToSubstitute(Color color)
    {
        return color.getGreen() >= 0.6 && color.getRed() == 0.0 && color.getBlue() == 0.0 && color.getOpacity() == 1.0;
    }
}
